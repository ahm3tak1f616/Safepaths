# Releasing Safepaths

One mod version covers both editions. One GitHub release starts the build and publishes both jars to the existing CurseForge and Modrinth projects.

## Files and platform entries

For a release such as `2.0.3`:

| Edition | File | Display name on CurseForge and Modrinth |
| --- | --- | --- |
| Forge 1.20.1 | `safepaths-2.0.3-1.20.1-forge.jar` | Safepaths 2.0.3 (Forge 1.20.1) |
| NeoForge 1.21.1 | `safepaths-2.0.3-1.21.1-neoforge.jar` | Safepaths 2.0.3 (NeoForge 1.21.1) |

Both entries use mod version `2.0.3`, the same changelog, and the same release channel. Each has its own Minecraft version, loader, Java version, and optional MineColonies dependency.

- **GitHub:** both jars are assets on one release, tagged `v2.0.3`.
- **CurseForge:** upload two normal files to the same project. Each file needs its own game and loader tags. Do not use Additional Files for the other edition; the CurseForge client does not support those as normal downloads.
- **Modrinth:** create two version entries in the same project. Each entry has one primary jar and its own game and loader tags. Metadata belongs to the version entry, so combining these jars in one entry would mix incompatible targets. Modrinth permits repeated version numbers; use each entry's version ID or the project's game/loader filters for direct selection.

References: [CurseForge file types](https://support.curseforge.com/support/solutions/articles/9000197242), [CurseForge upload API](https://support.curseforge.com/support/solutions/articles/9000197321), [Modrinth version metadata](https://docs.modrinth.com/api/operations/createversion/), [Modrinth version-number lookup](https://docs.modrinth.com/api/operations/getversionfromidornumber/).

## Release steps

1. Merge the reviewed changes into `main` after CI and client checks pass.
2. Set the new `mod_version` in the root `gradle.properties`. Use a new version; `2.0.2` is already published.
3. Move the relevant changelog notes from Unreleased into the new version and commit the release preparation to `main`.
4. Create a GitHub release from that commit with tag `v<mod_version>`, for example `v2.0.3`. Use a short title such as `Safepaths 2.0.3` and paste the player-facing changelog.
5. Publish the GitHub release. For a test release, select the prerelease option; the platforms receive the beta channel.
6. Check the Publish both editions workflow and confirm both entries on both platforms have the correct Minecraft version and loader.

The release workflow builds and tests both editions once, verifies the jars, and stores them as an artifact. The GitHub, Modrinth, and CurseForge jobs download that artifact; they do not rebuild independently. Project-page synchronization runs after uploads finish and links to the release tag.

The repository needs `MODRINTH_TOKEN` and `CURSEFORGE_TOKEN` secrets with upload access to the existing projects. GitHub release assets use the workflow's `GITHUB_TOKEN` with contents write permission.

If an upload fails after another succeeds, inspect the destination before retrying. The external upload action creates new entries; rerunning an already successful publishing job can duplicate a release. Retry the failed jobs rather than the entire workflow.

Older releases and the `1.20.1-forge` branch remain as published history. New releases use one `v<mod_version>` tag for both editions.
