import { readFile } from 'node:fs/promises';

const readme = await readFile(new URL('../../README.md', import.meta.url), 'utf8');
const properties = await readFile(new URL('../../gradle.properties', import.meta.url), 'utf8');
const version = properties.match(/^mod_version=(.+)$/m)?.[1].trim();
if (!version) throw new Error('Missing mod_version');
const repository = process.env.GITHUB_REPOSITORY || 'ahm3tak1f616/Safepaths';
const ref = encodeURIComponent(process.env.PROJECT_REF || 'main');
const source = `https://github.com/${repository}/blob/${ref}`;
const body = readme.split(/\r?\n## Development\r?\n/)[0].trim()
    .replaceAll('src="common/src/main/resources/icon.png"', `src="https://raw.githubusercontent.com/${repository}/${ref}/common/src/main/resources/icon.png"`)
    .replaceAll('](CHANGELOG.md)', `](${source}/CHANGELOG.md)`)
    .replaceAll('](LICENSE)', `](${source}/LICENSE)`);
const payload = {
    title: 'Safepaths',
    description: "Paths form where you walk. Configurable road speed and optional MineColonies support.",
    body,
    client_side: 'optional',
    server_side: 'required',
    wiki_url: 'https://github.com/ahm3tak1f616/Safepaths/wiki'
};

if (!body.includes('-1.21.1-neoforge.jar') || !body.includes('-1.20.1-forge.jar')) {
    throw new Error('The project page must describe both release editions');
}

if (process.argv.includes('--check')) {
    console.log(`Project page validated for Safepaths ${version}`);
} else {
    const token = process.env.MODRINTH_TOKEN;
    if (!token) throw new Error('MODRINTH_TOKEN is required');
    const response = await fetch('https://api.modrinth.com/v2/project/ec6ewft2', {
        method: 'PATCH',
        headers: {
            Authorization: token,
            'Content-Type': 'application/json',
            'User-Agent': 'ahm3tak1f616/Safepaths release automation'
        },
        body: JSON.stringify(payload)
    });
    if (!response.ok) throw new Error(`Modrinth project update failed: HTTP ${response.status}`);
    console.log('Modrinth project page synchronized with README.md');
}
