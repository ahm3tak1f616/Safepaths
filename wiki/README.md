# 🌿 Safepaths GitHub Wiki Source

This directory contains the complete source files for the official [Safepaths GitHub Wiki](https://github.com/ahm3tak1f616/Safepaths/wiki).

---

## 📑 File Structure & GitHub Wiki Mapping

GitHub Wiki uses a dedicated Git repository (`https://github.com/ahm3tak1f616/Safepaths.wiki.git`) where all Markdown documents reside at the root level:

| Local File | GitHub Wiki Page & Role |
| :--- | :--- |
| `Home.md` | Default landing page (`/wiki` or `/wiki/Home`) |
| `_Sidebar.md` | Persistent navigation menu shown in the sidebar of every wiki page |
| `_Footer.md` | Global footer displayed at the bottom of every wiki page |
| `Getting-Started.md` | Installation, requirements, and dedicated server setup (`/wiki/Getting-Started`) |
| `Core-Mechanics.md` | Stepping tracking, time limits, decay restoration, chunk unload safety (`/wiki/Core-Mechanics`) |
| `Speed-Boosts-and-Roadways.md` | Speed modifier, grace window, self-mapping speed roads (`/wiki/Speed-Boosts-and-Roadways`) |
| `In-Game-Configuration-and-GUI.md` | Visual palette editor, live search, decimal handling (`/wiki/In-Game-Configuration-and-GUI`) |
| `Configuration-Guide.md` | `safepaths-common.toml` breakdown, ticks table, presets (`/wiki/Configuration-Guide`) |
| `Block-and-Entity-Tags.md` | Datapack tags for blocks & entities (`/wiki/Block-and-Entity-Tags`) |
| `Compatibility-and-Integrations.md` | MineColonies, *BWG*, *BOP*, *RU*, *Terralith*, and shovel heuristics (`/wiki/Compatibility-and-Integrations`) |
| `FAQ-and-Troubleshooting.md` | Frequently asked questions, performance considerations, and issue reporting (`/wiki/FAQ-and-Troubleshooting`) |

---

## 🚀 How to Publish to GitHub Wiki

GitHub wikis can be updated automatically or pushed manually:

### Option A: Automatic Sync via GitHub Actions (Recommended)
An automated workflow is configured at [`.github/workflows/wiki-sync.yml`](../.github/workflows/wiki-sync.yml):
1. **Initialize the Wiki once**: If you haven't used the Wiki tab on GitHub yet, go to `https://github.com/ahm3tak1f616/Safepaths`, click the **Wiki** tab, and click **"Create the first page"** (save any placeholder title/content). This creates the wiki Git repository on GitHub.
2. Push your commits to `main`. Whenever changes are pushed to `wiki/**`, the workflow will automatically update your GitHub Wiki live!

### Option B: Push Manually via Git
You can also push directly to the GitHub Wiki Git repository:
```bash
# 1. Clone the wiki repository (must be initialized once on GitHub web UI)
git clone https://github.com/ahm3tak1f616/Safepaths.wiki.git safepaths-wiki-repo

# 2. Copy the markdown files from this wiki directory
cp wiki/*.md safepaths-wiki-repo/

# 3. Commit and push to GitHub Wiki
cd safepaths-wiki-repo
git add -A
git commit -m "docs(wiki): update complete wiki documentation"
git push origin master
```
