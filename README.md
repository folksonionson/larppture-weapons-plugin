# Larppture Weapons

Legendary artifacts for **Paper 1.21.11**, with a companion **resource pack**
(pack_format 75). Five relics, each with its own look, lore and ability.

| Artifact | Base item | Model | Ability |
|---|---|---|---|
| **Bloodbound Crusher** | mace | `larppture:mace` | +6 damage, steals 2 hearts on kill |
| **Reaper's Arc** | netherite sword | `larppture:scythe` | +3 damage, right-click reaps a 4-block arc (8 dmg + Wither II) |
| **Stormcaller** | trident | `larppture:golden_trident` | +2 damage, thrown hits call lightning (+4 dmg) |
| **Wings of the Void** | elytra | `larppture:elytra` | press **F** while worn for a Super Dash |
| **Chaos Cube** | nether star | `larppture:rubik_cube` | right-click twists colours (3D cube, 7 states) and grants a random boon |

All numbers are tunable in `config.yml`. Items are unbreakable, keep their
identity in a PersistentDataContainer (`larppture:id`) and get their looks from
the item-model component, so they survive renames, repairs and restarts.

## Commands

```
/lwp give <player> <mace|scythe|golden_trident|elytra|rubik_cube> [amount]   (larppture.give)
/lwp list                      - list artifact ids
/lwp info                      - inspect the item in your hand
/lwp reload                    - reload config.yml            (larppture.admin)
/lwp pack [player]             - send the resource-pack prompt (larppture.admin)
```

Aliases: `/larppture`, `/larpptureweapons`.

## Install

1. **Plugin** — drop `LarpptureWeapons-1.0.0.jar` (see
   [Releases](../../releases)) into the server's `plugins/` folder and restart.
   Built against `paper-api 1.21.11-R0.1-SNAPSHOT`, Java 21 bytecode.
2. **Resource pack** — two options:
   * **Server push (recommended):** host `LarpptureWeapons-Pack-1.0.0.zip`
     anywhere with direct downloads (GitHub release asset, nginx, object
     storage...), then in `plugins/LarpptureWeapons/config.yml`:
     ```yaml
     resource-pack:
       push-on-join: true
       url: "https://example.net/LarpptureWeapons-Pack-1.0.0.zip"
       sha1: "<sha1 printed by tools/build_pack.py>"
     ```
     Players get the vanilla pack prompt on join; `/lwp pack` re-sends it.
   * **Manual:** players put the zip into `.minecraft/resourcepacks`.

## Repository layout

```
plugin/            Gradle project (Paper plugin source, plugin.yml, config.yml)
resourcepack/      resource pack source tree (pack.mcmeta, assets/, pack.png)
tools/             build scripts (see below)
dist/              build output (gitignored; published via GitHub Releases)
```

## Building

### Normal (your machine)

```bash
cd plugin
gradle build          # or ./gradlew build with your wrapper
```

Produces `plugin/build/libs/LarpptureWeapons-1.0.0.jar`.
Pack: `python3 tools/build_pack.py` → `dist/LarpptureWeapons-Pack-1.0.0.zip`
(prints the sha-1 for `resource-pack.sha1`).

### Offline sandbox build (what produced the release jar)

The release jar in this repository was compiled in a network-restricted CI
sandbox with the WASM-ported OpenJDK 21 javac from
[`@wasm-oj/toolchain-java`](https://www.npmjs.com/package/@wasm-oj/toolchain-java)
(no JDK available there). It compiles the plugin sources together with
`tools/sandbox-stubs/` — exact compile-time mirrors of the Bukkit signatures
the plugin links against (the stubs are **not** shipped in the jar; the real
classes come from the Paper server at runtime). Events are registered
explicitly via `PluginManager#registerEvent` + `EventExecutor`, so no
annotation scanning is involved.

```bash
npm i @wasm-oj/toolchain-java @wasm-oj/server   # fetches the WASM javac assets
JAVAC_WASM_DIR=node_modules/@wasm-oj/toolchain-java/assets \
JAVA_STAGE_MJS=node_modules/@wasm-oj/server/dist/java-stage.mjs \
  node tools/build_plugin.mjs
python3 tools/package_plugin.py
```

Regenerate stubs after API changes with `python3 tools/gen_stubs.py`.

## Notes

* The F-key dash uses the offhand-swap event: while the Wings of the Void are
  worn, F dashes instead of swapping hands (and is cancelled during cooldown).
* The golden trident keeps vanilla throw/hold animations because the pack
  parents the vanilla trident model; lightning triggers on entity hits.
* Old uploads (`LarpptureWeapons (1).zip`, the cyrillic-named zip) are kept at
  the repo root for reference; `resourcepack/` supersedes them.
