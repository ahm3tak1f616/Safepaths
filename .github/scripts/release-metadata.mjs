import { appendFile, readFile } from 'node:fs/promises';

const properties = await readFile(new URL('../../gradle.properties', import.meta.url), 'utf8');
const version = properties.match(/^mod_version=(.+)$/m)?.[1].trim();
if (!version || !/^\d+\.\d+\.\d+(?:[-+][A-Za-z0-9.-]+)?$/.test(version)) {
    throw new Error('mod_version must be a valid release version');
}
const tag = process.env.RELEASE_TAG;
if (tag && tag !== `v${version}`) {
    throw new Error(`Release tag must be v${version}; received ${tag}`);
}
const outputs = `mod-version=${version}\nartifact-name=safepaths-${version}-jars\n`;
if (process.env.GITHUB_OUTPUT) await appendFile(process.env.GITHUB_OUTPUT, outputs);
console.log(`Validated both editions for Safepaths ${version}`);
