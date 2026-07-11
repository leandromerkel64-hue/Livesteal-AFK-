package com.keygui.hook;

import com.corecrates.CoreCrates;
import com.corecrates.crate.Crate;
import com.corecrates.crate.CrateManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kapselt saemtliche Zugriffe auf die offizielle CoreCrates-API.
 *
 * <p>Diese Klasse ist der einzige Ort, an dem gegen CoreCrates-Typen
 * gearbeitet wird. Sie wird ausschliesslich instanziiert, wenn das
 * CoreCrates-Plugin tatsaechlich geladen ist (siehe
 * {@code KeyGUIPlugin}). Dadurch entstehen bei fehlender Abhaengigkeit
 * keine {@link NoClassDefFoundError}-Probleme.</p>
 */
public class CoreCratesHook {

    private final CoreCrates coreCrates;
    private final Logger logger;

    /**
     * @param plugin die (bereits als vorhanden gepruefte) CoreCrates-Plugininstanz
     * @param logger der Logger des KeyGUI-Plugins
     */
    public CoreCratesHook(Plugin plugin, Logger logger) {
        this.coreCrates = (CoreCrates) plugin;
        this.logger = logger;
    }

    private CrateManager crateManager() {
        return coreCrates.getCrateManager();
    }

    /**
     * Liest alle aktuell in CoreCrates konfigurierten Crates aus.
     *
     * @return eine Liste entkoppelter {@link CrateData}-Objekte
     */
    public List<CrateData> getCrates() {
        List<CrateData> result = new ArrayList<>();
        try {
            for (Crate crate : crateManager().getCrates().values()) {
                if (crate == null) {
                    continue;
                }
                Material icon = crate.getIcon() != null ? crate.getIcon() : Material.CHEST;
                String display = crate.getDisplayName() != null ? crate.getDisplayName() : crate.getId();
                result.add(new CrateData(crate.getId(), display, icon));
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Konnte Crates nicht aus CoreCrates laden.", ex);
        }
        return result;
    }

    /**
     * Prueft, ob eine Crate mit der angegebenen ID existiert.
     *
     * @param crateId die interne Crate-ID
     * @return {@code true}, wenn die Crate existiert
     */
    public boolean crateExists(String crateId) {
        try {
            return crateManager().crateExists(crateId);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Konnte Existenz der Crate '" + crateId + "' nicht pruefen.", ex);
            return false;
        }
    }

    /**
     * Liefert den Anzeigenamen einer Crate (oder die ID als Fallback).
     *
     * @param crateId die interne Crate-ID
     * @return der Anzeigename oder die ID, falls nicht ermittelbar
     */
    public String getDisplayName(String crateId) {
        try {
            Crate crate = crateManager().getCrate(crateId);
            if (crate != null && crate.getDisplayName() != null) {
                return crate.getDisplayName();
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Konnte Anzeigenamen der Crate '" + crateId + "' nicht laden.", ex);
        }
        return crateId;
    }

    /**
     * Gibt einem Spieler ueber die offizielle CoreCrates-API genau einen Key
     * der angegebenen Crate.
     *
     * <p>Es wird bewusst {@link CrateManager#createKeyItem(Crate)} verwendet,
     * damit der Key exakt so erzeugt wird wie von CoreCrates selbst (inklusive
     * korrekter NBT-/PersistentData-Markierung).</p>
     *
     * @param player  der Empfaenger
     * @param crateId die interne Crate-ID
     * @return {@code true}, wenn der Key erfolgreich vergeben wurde
     */
    public boolean giveKey(Player player, String crateId) {
        try {
            Crate crate = crateManager().getCrate(crateId);
            if (crate == null) {
                return false;
            }
            ItemStack key = crateManager().createKeyItem(crate);
            if (key == null) {
                return false;
            }
            // addItem liefert nicht passende Items zurueck (z. B. volles Inventar).
            // Diese werden am Standort des Spielers fallen gelassen, damit kein Key verloren geht.
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(key);
            for (ItemStack item : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
            return true;
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Konnte Key der Crate '" + crateId
                    + "' nicht an " + player.getName() + " vergeben.", ex);
            return false;
        }
    }
}
