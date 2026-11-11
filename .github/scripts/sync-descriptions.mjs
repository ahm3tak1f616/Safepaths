import { readFile, readdir, writeFile } from 'node:fs/promises';

const root = new URL('../../', import.meta.url);
const properties = await readFile(new URL('gradle.properties', root), 'utf8');
const description = properties.match(/^mod_description=(.+)$/m)?.[1].trim();
if (!description || description.length > 250) throw new Error('mod_description must contain 1–250 characters');
const write = process.argv.includes('--write');
const keys = [
    'fml.menu.mods.info.description.safepaths',
    'neoforge.screen.mods.info.description.safepaths',
    'modmenu.descriptionTranslation.safepaths'
];

async function update(path, transform) {
    const url = new URL(path, root);
    const before = await readFile(url, 'utf8');
    const after = transform(before);
    if (before === after) return;
    if (!write) throw new Error(`${path} differs from its canonical description; run with --write`);
    await writeFile(url, after);
    console.log(`Updated ${path}`);
}

for (const [path, pattern] of [
    ['README.md', /(<\/p>\r?\n\r?\n)([^\r\n]+)(?=\r?\n)/],
    ['wiki/Home.md', /(^# [^\r\n]+\r?\n\r?\n)([^\r\n]+)(?=\r?\n)/]
]) {
    await update(path, text => {
        if (!pattern.test(text)) throw new Error(`Cannot locate the introduction in ${path}`);
        return text.replace(pattern, (_, prefix) => prefix + description);
    });
}

const languageDirectory = 'common/src/main/resources/assets/safepaths/lang/';
for (const file of (await readdir(new URL(languageDirectory, root))).filter(name => name.endsWith('.json'))) {
    await update(languageDirectory + file, text => {
        const values = JSON.parse(text);
        const localized = file === 'en_us.json' ? description : values[keys[0]];
        if (typeof localized !== 'string' || !localized.trim()) throw new Error(`Missing description in ${file}`);
        for (const key of keys) {
            if (!Object.hasOwn(values, key)) throw new Error(`Missing ${key} in ${file}`);
            if (values[key] === localized) continue;
            const escapedKey = key.replaceAll('.', '\\.');
            const pattern = new RegExp(`("${escapedKey}"\\s*:\\s*)"(?:[^"\\\\]|\\\\.)*"`);
            text = text.replace(pattern, (_, prefix) => prefix + JSON.stringify(localized));
        }
        return text;
    });
}

console.log(`Descriptions ${write ? 'synchronized' : 'validated'} from gradle.properties and localized source keys`);
