package com.keygui.config;

import com.keygui.KeyGUIPlugin;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Typsicherer Zugriff auf die {@code config.yml}.
 *
 * <p>Kapselt das Auslesen und stellt sinnvolle Standardwerte bereit, damit
 * fehlerhafte oder unvollstaendige Konfigurationen nicht zu Abstuerzen fuehren.</p>
 */
public class ConfigManager {

    private final KeyGUIPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(KeyGUIPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    /**
     * Laedt die {@code config.yml} (neu) und legt sie bei Bedarf an.
     */
    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // ---------------------------------------------------------------- GUI

    public String getGuiTitle() {
        return config.getString("gui.title", "&8Crate Keys");
    }

    /**
     * @return die Anzahl der GUI-Slots (Zeilen * 9), begrenzt auf 9..54
     */
    public int getGuiSize() {
        int rows = config.getInt("gui.rows", 6);
        rows = Math.max(1, Math.min(6, rows));
        return rows * 9;
    }

    /**
     * @return die konfigurierten Slots fuer Crates (kann leer sein)
     */
    public List<Integer> getCrateSlots() {
        return new ArrayList<>(config.getIntegerList("gui.crate-slots"));
    }

    public boolean isFillerEnabled() {
        return config.getBoolean("gui.filler.enabled", true);
    }

    public String getFillerMaterialName() {
        return config.getString("gui.filler.material", "BLACK_STAINED_GLASS_PANE");
    }

    public String getFillerName() {
        return config.getString("gui.filler.name", " ");
    }

    public List<Integer> getFillerSlots() {
        return new ArrayList<>(config.getIntegerList("gui.filler.slots"));
    }

    public String getCrateItemMaterial() {
        return config.getString("gui.crate-item.material", "AUTO");
    }

    public String getCrateItemName() {
        return config.getString("gui.crate-item.name", "&b{crate}");
    }

    public List<String> getCrateItemLore() {
        return config.getStringList("gui.crate-item.lore");
    }

    // ------------------------------------------------------------ Cooldown

    public boolean isCooldownEnabled() {
        return config.getBoolean("cooldown.enabled", true);
    }

    public int getCooldownSeconds() {
        return Math.max(0, config.getInt("cooldown.seconds", 30));
    }

    // ----------------------------------------------------------- Broadcast

    public boolean isBroadcastEnabled() {
        return config.getBoolean("broadcast.enabled", true);
    }

    // -------------------------------------------------------------- Sounds

    /**
     * Spielt einen der konfigurierten Sounds fuer einen Spieler ab.
     *
     * @param player der Spieler
     * @param key    der Sound-Schluessel unter {@code sounds.} (z. B. "open")
     */
    public void playSound(Player player, String key) {
        ConfigurationSection section = config.getConfigurationSection("sounds." + key);
        if (section == null || !section.getBoolean("enabled", true)) {
            return;
        }
        String soundName = section.getString("sound", "");
        if (soundName == null || soundName.isEmpty()) {
            return;
        }
        float volume = (float) section.getDouble("volume", 1.0);
        float pitch = (float) section.getDouble("pitch", 1.0);
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().log(Level.WARNING, "Unbekannter Sound in config.yml: " + soundName);
        }
    }
}
