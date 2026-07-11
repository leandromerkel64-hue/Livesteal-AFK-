package com.keygui.hook;

import org.bukkit.Material;

/**
 * Entkoppeltes, unveraenderliches Datenobjekt fuer eine CoreCrates-Crate.
 *
 * <p>Damit muessen die GUI- und Command-Klassen nicht direkt gegen die
 * CoreCrates-Typen arbeiten. Das haelt die Kopplung an die Fremd-API klein
 * und auf den {@link CoreCratesHook} beschraenkt.</p>
 *
 * @param id          die interne ID der Crate
 * @param displayName der Anzeigename der Crate
 * @param icon        das in CoreCrates hinterlegte Icon-Material
 */
public record CrateData(String id, String displayName, Material icon) {
}
