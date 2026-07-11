package com.keygui;

import com.keygui.command.KeyGuiCommand;
import com.keygui.config.ConfigManager;
import com.keygui.config.MessageManager;
import com.keygui.gui.KeyGuiManager;
import com.keygui.hook.CoreCratesHook;
import com.keygui.hook.PlaceholderHook;
import com.keygui.listener.GuiListener;
import com.keygui.util.CooldownManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Haupteinstiegspunkt des KeyGUI-Plugins.
 *
 * <p>Verantwortlich fuer das Laden der Konfiguration, das Erkennen der
 * optionalen Abhaengigkeiten (CoreCrates, PlaceholderAPI) sowie die
 * Registrierung von Befehl und Listener.</p>
 */
public final class KeyGUIPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private KeyGuiManager keyGuiManager;
    private KeyService keyService;
    private CooldownManager cooldownManager;

    private CoreCratesHook coreCratesHook;
    private PlaceholderHook placeholderHook;

    @Override
    public void onEnable() {
        // Konfiguration und Nachrichten laden.
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.cooldownManager = new CooldownManager();

        // Optionale Abhaengigkeiten erkennen (Plugin darf ohne sie nicht abstuerzen).
        setupPlaceholderApi();
        setupCoreCrates();

        // Kernkomponenten initialisieren.
        this.keyGuiManager = new KeyGuiManager(this);
        this.keyService = new KeyService(this);

        // Befehl registrieren.
        KeyGuiCommand commandExecutor = new KeyGuiCommand(this);
        if (getCommand("keygui") != null) {
            getCommand("keygui").setExecutor(commandExecutor);
        } else {
            getLogger().severe("Befehl 'keygui' ist nicht in der plugin.yml definiert!");
        }

        // Listener registrieren.
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        getLogger().info("KeyGUI wurde erfolgreich aktiviert.");
    }

    @Override
    public void onDisable() {
        getLogger().info("KeyGUI wurde deaktiviert.");
    }

    /**
     * Erkennt PlaceholderAPI und gibt eine verstaendliche Konsolenmeldung aus.
     */
    private void setupPlaceholderApi() {
        this.placeholderHook = new PlaceholderHook();
        if (placeholderHook.isAvailable()) {
            getLogger().info("PlaceholderAPI erkannt - Platzhalter werden unterstuetzt.");
        } else {
            getLogger().info("PlaceholderAPI nicht gefunden - Platzhalter werden nicht ersetzt.");
        }
    }

    /**
     * Erkennt CoreCrates und initialisiert den Hook nur, wenn das Plugin
     * tatsaechlich vorhanden ist.
     */
    private void setupCoreCrates() {
        PluginManager pm = getServer().getPluginManager();
        Plugin plugin = pm.getPlugin("CoreCrates");
        if (plugin == null) {
            getLogger().warning("CoreCrates wurde nicht gefunden! "
                    + "Das GUI oeffnet sich, es koennen jedoch keine Keys vergeben werden. "
                    + "Bitte installiere CoreCrates.");
            this.coreCratesHook = null;
            return;
        }
        try {
            this.coreCratesHook = new CoreCratesHook(plugin, getLogger());
            getLogger().info("CoreCrates erkannt - Key-Vergabe ist aktiv.");
        } catch (Throwable t) {
            // Fehlgeschlagene Verknuepfung darf das Plugin nicht abstuerzen lassen.
            getLogger().severe("CoreCrates konnte nicht eingebunden werden: " + t.getMessage());
            this.coreCratesHook = null;
        }
    }

    /**
     * Laedt Konfiguration und Nachrichten zur Laufzeit neu.
     */
    public void reloadAll() {
        configManager.reload();
        messageManager.reload();
    }

    // ----------------------------------------------------------- Zugriffe

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public KeyGuiManager getKeyGuiManager() {
        return keyGuiManager;
    }

    public KeyService getKeyService() {
        return keyService;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public CoreCratesHook getCoreCratesHook() {
        return coreCratesHook;
    }

    public PlaceholderHook getPlaceholderHook() {
        return placeholderHook;
    }
}
