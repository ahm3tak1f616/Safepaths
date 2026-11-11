# Releasing Safepaths

One mod version covers both editions. One GitHub release starts the build and publishes both jars to the existing CurseForge and Modrinth projects.

## Preserve project identity and page content

The user accepted the existing live [GitHub](https://github.com/ahm3tak1f616/Safepaths), [CurseForge](https://www.curseforge.com/minecraft/mc-mods/safepaths), and [Modrinth](https://modrinth.com/mod/safepaths) pages as the baseline on 2026-11-11. Preserve the repository name `Safepaths` and the storefront title `Safepaths (MineColonies)`. Platform-specific summaries and long descriptions may differ; do not force them to match the README or one another. Preparing an alternative summary locally does not authorize publishing it to any platform.

A routine release updates version entries, production jars, compatibility metadata, the release channel, and changelogs. Keep the established release-title format consistent across GitHub, CurseForge, and Modrinth. Do not rename projects or rewrite their titles, summaries, long descriptions, GitHub About text, icons, or banners as part of releasing. Change those only when the user explicitly requests that specific content change. Preserve older release notes.

The same player-facing changelog is used for both editions on all three platforms. Describe changes in that version; do not replace the changelog with the mod's general description or regenerate existing project pages to make a release look consistent.

`mod_description` is the approved short English summary for packaged metadata and local introductions. It is not a release checklist item to rewrite. When a summary change is explicitly requested, update the relevant local copies together and review the result. Local consistency checks do not authorize replacing live project-page content.

## Files and platform entries

The prepared release version is **2.5.0**. Creating or editing these files does not publish it.

The selected branding is the modern icon (option B), stored at `common/src/main/resources/icon.png` and used by both mod menus. For an explicitly requested branding update, use this same PNG for the CurseForge and Modrinth project icons. Its editable source is `docs/assets/safepaths-icon-modern-2.5.svg`; the shared banner is `docs/assets/safepaths-banner-2.5.png`. Existing platform branding stays unchanged during routine releases.

The banner's editable source is `docs/assets/safepaths-banner.svg`. Keep this stable path because existing project pages link to it directly. Export its PNG to the versioned banner file used by the README. Replacing an image at a URL used by a live page also changes that page's appearance; treat that as a branding update requiring an explicit request.

| Edition | File | Display name on CurseForge and Modrinth |
| --- | --- | --- |
| Forge 1.20.1 | `safepaths-2.5.0-1.20.1-forge.jar` | Safepaths 2.5.0 (Forge 1.20.1) |
| NeoForge 1.21.1 | `safepaths-2.5.0-1.21.1-neoforge.jar` | Safepaths 2.5.0 (NeoForge 1.21.1) |

Both entries use mod version `2.5.0`, the same changelog, and the same release channel. Each has its own Minecraft version, loader, Java version, and optional MineColonies dependency.

- **GitHub:** both jars are assets on one release, tagged `v2.5.0`.
- **CurseForge:** upload two normal files to the same project. Each file needs its own game and loader tags. Do not use Additional Files for the other edition; the CurseForge client does not support those as normal downloads.
- **Modrinth:** create two version entries in the same project. Each entry has one primary jar and its own game and loader tags. Metadata belongs to the version entry, so combining these jars in one entry would mix incompatible targets. Modrinth permits repeated version numbers; use each entry's version ID or the project's game/loader filters for direct selection.

References: [CurseForge file types](https://support.curseforge.com/support/solutions/articles/9000197242), [CurseForge upload API](https://support.curseforge.com/support/solutions/articles/9000197321), [Modrinth version metadata](https://docs.modrinth.com/api/operations/createversion/), [Modrinth version-number lookup](https://docs.modrinth.com/api/operations/getversionfromidornumber/).

## Release steps

1. Merge the reviewed changes into `main` after CI and client checks pass.
2. Confirm `mod_version=2.5.0` in the root `gradle.properties` and verify the release tag will be `v2.5.0`. Do not reuse a published version.
3. Review the prepared `2.5.0` section in `CHANGELOG.md`, add the actual release date when publishing, and commit the release preparation to `main`. Keep older release notes intact.
4. Create a GitHub release from that commit with tag `v<mod_version>`, for example `v2.5.0`. Use a short title such as `Safepaths 2.5.0` and paste the player-facing changelog.
5. Publish the GitHub release. For a test release, select the prerelease option; the platforms receive the beta channel.
6. Open **Actions → Publish both editions** and wait for the build and upload jobs to finish. The GitHub release is visible before the jars finish uploading.
7. Confirm the release has exactly the two production jars above, and check both entries on both platforms for the correct Minecraft version, loader, title format, and changelog. Verify existing project titles, summaries, descriptions, and branding remain unchanged. CurseForge files may await moderation before appearing publicly.

## Separate documentation and project-page updates

The wiki source under `wiki/` synchronizes when pushed to `main`. README and wiki edits are documentation changes; neither a release nor a push to `main` triggers a Modrinth project-page update.

After an explicitly requested summary change, run `node .github/scripts/sync-descriptions.mjs --write`, then `--check` to validate the local README, wiki introduction, and language aliases. Both packaged mod descriptions use `mod_description` from the root `gradle.properties`. Translate non-English descriptions separately when needed.

The manual **Update Modrinth project page (manual)** workflow replaces the Modrinth summary with `mod_description`, its long body with the player-facing part of `README.md`, and its client/server requirements and wiki link with the script's configured values. Review all of those fields and obtain explicit authorization for the page update before running it. The script requires `--apply` for a live update; `node .github/scripts/sync-modrinth.mjs --check` only validates local sources. This workflow does not change the project title or icon. Asset and source links use `main`, independently of release tags.

GitHub About text and CurseForge summaries/descriptions are maintained separately; the repository does not automatically replace them. For an explicitly requested cross-platform summary update, use the approved summary on the requested platforms. Do not replace a platform's existing long description with the README unless that replacement is explicitly requested. The CurseForge file changelog comes from the GitHub release body.

## Publishing and recovery

The release workflow builds and tests both editions once, verifies the jars, and stores them as an artifact. The GitHub, Modrinth, and CurseForge jobs download that artifact; they do not rebuild independently or synchronize project-page content.

The repository needs `MODRINTH_TOKEN` and `CURSEFORGE_TOKEN` secrets with upload access to the existing projects. GitHub release assets use the workflow's `GITHUB_TOKEN` with contents write permission.

If an upload fails after another succeeds, inspect the destination before retrying. The external upload action creates new entries; even a failed job may have completed its upload before losing the response. In **Actions**, open the run and choose **Re-run jobs → Re-run failed jobs** only after checking for an existing file. Do not rerun successful publishing jobs. Build artifacts are retained for 14 days, so recover a partial release within that window.

Release jobs use the tagged commit. Editing `main` and rerunning an old release does not include the fix; prepare a new version and tag when the code or workflow needs to change.

Older releases and tags remain available after deleting the old edition branches. Develop both editions on `main`; new releases use one `v<mod_version>` tag for both jars.
