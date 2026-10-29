# Conversion editor

Open the loader's **Mods → Safepaths → Config** menu, then **Configure Conversions...**. Forge 1.20.1 has a Safepaths settings screen; NeoForge uses its built-in settings screen.

While connected to a dedicated server, the menu displays instructions for the server owner. It does not edit the remote server's configuration.

## Edit a conversion

1. Add a conversion, or select an existing row on the left.
2. Select its source field, then choose the ground block from the palette on the right.
3. Select its target field and choose the path block.
4. Select **Done** to save. **Cancel** discards changes.

Search the palette by block name or ID. You can also enter a block ID directly, such as `minecraft:coarse_dirt`.

Choose the same source and target for a permanent speed road. Remove a row with its delete button. **Restore Defaults** replaces the conversion list after confirmation.

## Speed settings

The editor includes a speed toggle and multiplier. A multiplier of `0.2` means a 20% movement speed bonus. Decimal input accepts `0.25` or `0,25`.

## Valid rules

Air, liquids, non-solid blocks, and block entities cannot be conversion targets. World placement also needs a supported result that can survive at the position. Unsafe conversions leave the original terrain in place.

If a source has several rules, an explicit conversion takes priority over a rule mapping the block to itself. Among explicit conversions, the first rule wins.
