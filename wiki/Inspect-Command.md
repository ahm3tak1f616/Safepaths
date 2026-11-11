# Inspect command

Safepaths 2.5.0 adds an operator command for diagnosing path creation, speed, and decay:

```text
/safepaths inspect
/safepaths inspect x y z
```

The command requires **permission level 2**. In singleplayer, commands must be enabled. On a server, the server owner can grant operator access.

Without coordinates, a player inspects the block they are looking at within eight blocks; if there is no block hit, the command uses their supporting ground block. The server console must supply coordinates. Coordinates refer to the command source's current dimension and must be in a loaded chunk; the command does not load chunks.

## Output

- Block coordinates and registry ID.
- Status, including creation/decay switches, sneaking prevention, supported conversions, and protection checks.
- Counted passes and remaining construction window, or the configured window when no active progress exists.
- Conversion target for eligible ground.
- Road speed bonus selected from global or per-rule settings.
- Original terrain and decay countdown when a valid tracked-road record exists.

For tracked roads, the status can explain disabled decay, unsafe restoration support, or MineColonies permission checks. A road without a decay record is identified separately. A zero countdown does not guarantee immediate restoration if support, permissions, or the decay switch prevent it.

The speed shown describes that block's road rule, not an entity's current velocity or all movement restrictions. If the global road-speed switch is disabled, it reports no bonus.

Inspect is read-only. It does not create or repair paths, grant permissions, or change configuration. Server messages include an English fallback, so a client does not need Safepaths installed to read them.

[Configuration](Configuration-Guide) · [Troubleshooting](FAQ-and-Troubleshooting)
