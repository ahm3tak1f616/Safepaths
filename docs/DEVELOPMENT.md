# Development

Both editions compile the Java sources and resources in `common/`. Loader events, version-specific APIs, configuration, screens, and GameTests live in `forge-1.20.1/` and `neoforge-1.21.1/`.

Install JDK 17 and JDK 21. Run the root wrapper with Java 21; it selects Java 17 and Gradle 8.7 for Forge, and Java 21 and Gradle 9.2.1 for NeoForge. The wrappers stay separate because the loader plugins use different Gradle versions.

## Build and test

From the repository root on Windows:

```powershell
.\gradlew.bat build --console=plain
.\gradlew.bat runGameTestServer -PwithoutMineColonies --console=plain
```

Use `./gradlew` on Linux and macOS. `build` runs both GameTest suites and jar verification, then collects the production jars in `build/libs/`. The second command tests both editions without the optional MineColonies runtime.

| Task | Result |
| --- | --- |
| `buildForge` / `buildNeoForge` | Build one edition; jar in its own `build/libs/`. |
| `testForge` / `testNeoForge` | Build and run that edition's GameTests. |
| `assemble` | Build and collect both jars without GameTests. |
| `verifyJars` | Check mod version, loader metadata, all shared tags, resources, and Java bytecode versions. |
| `clean` | Remove the collected root jars. |

Use an edition's wrapper with `clean` to clear its own build output. Shared tags and structures use 1.21.1 directory names; Forge resource processing converts them to the 1.20.1 names.

## IntelliJ

Link both edition `build.gradle` files as Gradle projects, using each project's wrapper and matching JDK. Run client and server configurations from the edition you want to test. Changes in `common/` apply to both builds.

The root `gradle.properties` holds mod identity and version. Each edition's properties hold loader, mappings, and dependency versions.

## Before merging

CI and CodeQL run on pushes to `main` and pull requests targeting `main`. CI tests both editions with and without MineColonies. The setup action clears location-dependent NeoGradle data; CI passes `--no-build-cache` to avoid restoring classpaths from an older layout.

Also check both clients: create a path, leave it to decay, walk on dirt paths and gravel, edit conversions, reopen the config screen, and check MineColonies permissions if installed. Save an unchanged speed value such as `0.125` and confirm it remains unchanged. Check joining a dedicated server without Safepaths installed on the client. GameTests do not verify client rendering, input, or multiplayer connections.

Run `node .github/scripts/sync-modrinth.mjs --check` after changing the player-facing README. See [Releasing](RELEASING.md) for distribution.

## Add a Minecraft edition

Create a separate edition directory with its own loader setup and API adapters. Compile the existing `common/` sources there and port any APIs that changed; changing a Minecraft version property alone is not a port.

Register the edition in the root `build.gradle` targets and jar checks, then update the artifact paths in `build-editions.yml`, both publishing matrices, the toolchain setup, dependency monitoring, and download documentation. The Modrinth page validation also lists the supported jar suffixes. Keep `mod_version` in the root properties so all editions share the release version.

## Dependency monitoring

The dependency-submission workflow resolves and submits each edition separately. Its snapshots must point to that edition's Gradle files and include its loader dependency. Scanning only the root coordinator misses both mod dependency graphs.

Dependabot checks dependencies declared in the two edition directories. Libraries supplied indirectly by Minecraft, a loader, or its build plugin may appear in alerts but cannot be edited by the Gradle updater; these jobs can report `dependency_not_found` even when dependency submission succeeds. Investigate the affected dependency and its upstream fix before changing loader or plugin versions. Do not add arbitrary library overrides to silence an alert.

Routine loader and Minecraft version upgrades remain manual; GitHub Actions version updates continue weekly.
