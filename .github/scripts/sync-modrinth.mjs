import { readFile } from 'node:fs/promises';

const readme = await readFile(new URL('../../README.md', import.meta.url), 'utf8');
const properties = await readFile(new URL('../../gradle.properties', import.meta.url), 'utf8');
const version = properties.match(/^mod_version=(.+)$/m)?.[1].trim();
if (!version) throw new Error('Missing mod_version');
const repository = process.env.GITHUB_REPOSITORY || 'ahm3tak1f616/Safepaths';
const ref = encodeURIComponent(process.env.PROJECT_REF || 'main');
const source = `https://github.com/${repository}/blob/${ref}`;
const body = readme
    .replaceAll('src="common/src/main/resources/icon.png"', `src="https://raw.githubusercontent.com/${repository}/${ref}/common/src/main/resources/icon.png"`)
    .replaceAll('](CHANGELOG.md)', `](${source}/CHANGELOG.md)`)
    .replaceAll('](LICENSE)', `](${source}/LICENSE)`);
const payload = {
    description: "Natural paths from footsteps, configurable road speed, and safe terrain decay. Optional MineColonies support for Minecraft 1.21.1 NeoForge and 1.20.1 Forge.",
    body,
    client_side: 'optional',
    server_side: 'required',
    wiki_url: 'https://github.com/ahm3tak1f616/Safepaths/wiki'
};

if (!body.includes(`safepaths-${version}-1.21.1-neoforge.jar`) || !body.includes(`safepaths-${version}-1.20.1-forge.jar`)) {
    throw new Error('The project page must describe both release editions');
}

if (process.argv.includes('--check')) {
    console.log('Project page validated for both editions');
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
