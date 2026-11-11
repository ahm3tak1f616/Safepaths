# Development

Both editions compile the Java sources and resources in `common/`. Loader events, version-specific APIs, configuration, screens, and GameTests live in `forge-1.20.1/` and `neoforge-1.21.1/`.

Install JDK 17 and JDK 21. Run the root wrapper with Java 21; it selects Java 17 and Gradle 8.7 for Forge, and Java 21 and Gradle 9.2.1 for NeoForge. The wrappers stay separate because the loader plugins use different Gradle versions.

## Build and test

From the repository root on Windows:

```powershell
.\gradlew.bat build --console=plain
.\gradlew.bat test --console=plain
```

Use `./gradlew` on Linux and macOS. `build` compiles both editions, runs jar verification, and collects the production jars in `build/libs/`. `test` (or `runGameTestServer`) runs the GameTest suites. Add `-PwithoutMineColonies` to test without the optional MineColonies runtime.

| Task | Result |
| --- | --- |
| `buildForge` / `buildNeoForge` | Build one edition; jar in its own `build/libs/`. |
| `test` / `runGameTestServer` | Build and run GameTests for both editions. |
| `testForge` / `testNeoForge` | Build and run that edition's GameTests. |
| `assemble` | Build and collect both jars without verification. |
| `verifyJars` | Check mod version, loader metadata, all shared tags, resources, and Java bytecode versions. |
| `clean` | Remove the collected root jars. |

Use an edition's wrapper with `clean` to clear its own build output. Shared tags and structures use 1.21.1 directory names; Forge resource processing converts them to the 1.20.1 names.

Development runs log at INFO by default. Add `-PmodLogLevel=debug` for detailed Minecraft and loader diagnostics. Add `--warning-mode=all` to inspect Gradle deprecations; the root wrapper forwards both options to the edition builds. Javadoc still checks documentation syntax, references, and accessibility, but does not require comments on every internal member.

## IntelliJ

Link both edition `build.gradle` files as Gradle projects, using each project's wrapper and matching JDK. Run client and server configurations from the edition you want to test. Changes in `common/` apply to both builds.

The Gradle window shows three linked builds. The root `safepaths` build coordinates both editions, collects jars, and verifies them. The Forge and NeoForge entries expose their own development runs and use their own wrappers and JDKs.

Run data generation from an edition directory with `./gradlew runData -PwithoutMineColonies` (`.\gradlew.bat` on Windows). Use a Gradle run configuration with the same property for IntelliJ Data runs. The optional MineColonies runtime includes Domum Ornamentum, whose data generators require an additional development library. Safepaths data generation uses its own runtime without these optional mods.

The root `gradle.properties` holds mod identity, version, and the approved English `mod_description`. Both loader metadata templates use that description. Change it only for an explicitly requested summary update, then run `node .github/scripts/sync-descriptions.mjs --write` to refresh local README/wiki introductions and description aliases; translate the first description key in each non-English language file separately. Root `verifyDescriptions` checks local consistency, and `verifyJars` checks the packaged descriptions. These checks do not modify live project pages. Each edition's properties hold loader, mappings, and dependency versions.

### Dependency source tools

Both edition builds use the MixinMCP Gradle plugin 1.6.0. Install the matching MixinMCP IntelliJ plugin from JetBrains Marketplace and restart the IDE to expose its tools through the existing IntelliJ MCP server.

Run `genDependencySources --max-workers=1` with an edition's wrapper to prepare dependency sources manually. The task also runs during IntelliJ Gradle sync. Decompilation uses one thread per worker to limit memory use. Each edition records its classpath in `.gradle/mixinmcp/manifest.json`, with shared generated sources in the user's MixinMCP cache. Initial preparation can take several minutes, while subsequent runs reuse the cache.

## Before merging

CI and CodeQL run on pushes to `main` and pull requests targeting `main`. CI tests both editions with and without MineColonies. The setup action clears location-dependent NeoGradle data; CI passes `--no-build-cache` to avoid restoring classpaths from an older layout.

Also check both clients: create a path, leave it to decay, walk on dirt paths and gravel, edit conversions, reopen the config screen, and check MineColonies permissions if installed. Save an unchanged speed value such as GUI `12.5` (stored as `0.125`) and confirm it remains unchanged. Check joining a dedicated server without Safepaths installed on the client. GameTests do not verify client rendering, input, or multiplayer connections.

Settings and conversions share one draft: the conversion list's Done returns to settings, and the main Done writes both together. Cancel on the main screen discards both. Reset restores the defaults from the config spec and remains pending until Done. Speed fields contain numbers only; units and inherited speed appear beside them.

The settings menu displays speed in percent: a stored multiplier of `0.125` is shown as `12.5`. Conversion rules accept `source -> target @ multiplier`; an omitted multiplier inherits the global value. Verify two source blocks converting to the same target retain different bonuses, and that entering zero immediately removes a previous road bonus. Explicit self-mappings override incoming rules for their material; untracked roads otherwise use the first matching target rule.

Check the settings tabs, rule editor, and block picker at GUI scales 1–4 and Auto, including a 320×240 scaled viewport. Resize with incomplete block IDs and decimal inputs, navigate picker pages after resizing, cancel an edit, and reopen the screen. Missing optional-mod entries must remain listed as inactive; protected sources and unsafe targets must show an explanation. Confirm that no controls overlap or extend outside the screen.

Check creation and decay independently. Sneaking prevention affects new construction passes only; authorized traffic still refreshes existing roads even with creation disabled. Disabling decay retains records and elapsed time, so expired roads may revert when it is enabled again. `/safepaths inspect [x y z]` requires permission level 2 and must not load absent chunks. English fallback messages support unmodded clients.

Run `node .github/scripts/sync-modrinth.mjs --check` after changing the player-facing README. Project-page updates are manual and require an explicit request; pushes and releases preserve existing platform titles, summaries, descriptions, and branding. Keep release titles and changelogs consistent across platforms. See [Releasing](RELEASING.md) for the publishing boundary and manual page-update procedure.

## Add a Minecraft edition

Create a separate edition directory with its own loader setup and API adapters. Compile the existing `common/` sources there and port any APIs that changed; changing a Minecraft version property alone is not a port.

Register the edition in the root `build.gradle` targets and jar checks, then update the artifact paths in `build-editions.yml`, both publishing matrices, the toolchain setup, dependency monitoring, and download documentation. The Modrinth page validation also lists the supported jar suffixes. Keep `mod_version` in the root properties so all editions share the release version.

## Dependency monitoring

The dependency-submission workflow resolves and submits each edition separately. Its snapshots must point to that edition's Gradle files and include its loader dependency. Scanning only the root coordinator misses both mod dependency graphs.

Dependabot checks dependencies declared in the two edition directories. Libraries supplied indirectly by Minecraft, a loader, or its build plugin may appear in alerts but cannot be edited by the Gradle updater; these jobs can report `dependency_not_found` even when dependency submission succeeds. Investigate the affected dependency and its upstream fix before changing loader or plugin versions. Do not add arbitrary library overrides to silence an alert.

Routine loader and Minecraft version upgrades remain manual; GitHub Actions version updates continue weekly.
