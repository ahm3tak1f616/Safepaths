import { readFile } from 'node:fs/promises';

const { SNAPSHOT_FILE, EDITION_DIRECTORY, EDITION_LOADER } = process.env;
if (!SNAPSHOT_FILE || !EDITION_DIRECTORY || !['forge', 'neoforge'].includes(EDITION_LOADER)) {
    throw new Error('Snapshot file, edition directory, and loader are required');
}
const snapshot = JSON.parse(await readFile(SNAPSHOT_FILE, 'utf8'));
const manifest = Object.values(snapshot.manifests || {}).find(entry =>
    ["build.gradle", "settings.gradle"].some(file =>
        entry.file?.source_location === `${EDITION_DIRECTORY}/${file}`));
if (!manifest) throw new Error(`Dependency graph is missing the ${EDITION_DIRECTORY} manifest`);
const dependencies = Object.values(manifest.resolved || {});
if (!dependencies.some(entry => entry.package_url?.includes(`/${EDITION_LOADER}@`))) {
    throw new Error(`Dependency graph is missing the ${EDITION_LOADER} loader`);
}
console.log(`Verified ${EDITION_DIRECTORY}: ${dependencies.length} dependencies`);
