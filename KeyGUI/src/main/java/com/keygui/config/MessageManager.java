package com.keygui.config;

import com.keygui.KeyGUIPlugin;
import com.keygui.hook.PlaceholderHook;
import com.keygui.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Laedt und verwaltet die {@code messages.yml} und stellt komfortable
 * Methoden zum Formatieren und Senden von Nachrichten bereit.
 *
 * <p>Beim Formatieren werden nacheinander eigene Platzhalter ersetzt,
 * PlaceholderAPI-Platzhalter aufgeloest (falls verfuegbar) und Farbcodes
 * (inkl. Hex) angewendet.</p>
 */
public class MessageManager {

    private final KeyGUIPlugin plugin;
    private FileConfiguration messages;
    private String prefix = "";

    public MessageManager(KeyGUIPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    /**
     * Laedt die {@code messages.yml} (neu) und legt sie bei Bedarf an.
     */
    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messages = YamlConfiguration.loadConfiguration(file);
        this.prefix = messages.getString("prefix", "");
    }

    /**
     * Liefert die Rohnachricht zu einem Schluessel (ohne Farb-/Platzhalter-Verarbeitung).
     *
     * @param key der Nachrichtenschluessel
     * @return die Rohnachricht oder ein Platzhaltertext, falls nicht vorhanden
     */
    public String raw(String key) {
        return messages.getString(key, "&cFehlende Nachricht: " + key);
    }

    /**
     * Formatiert eine Nachricht vollstaendig (Platzhalter, PAPI, Farben).
     *
     * @param player       der Spielerkontext fuer PlaceholderAPI (darf {@code null} sein)
     * @param key          der Nachrichtenschluessel
     * @param replacements Paare aus Platzhalter (z. B. "{crate}") und Wert
     * @return die fertig formatierte Nachricht
     */
    public String format(Player player, String key, Map<String, String> replacements) {
        String message = raw(key);
        return formatText(player, message, replacements);
    }

    /**
     * Formatiert einen freien Text (nicht aus messages.yml) vollstaendig.
     */
    public String formatText(Player player, String text, Map<String, String> replacements) {
        String message = text == null ? "" : text;
        message = message.replace("{prefix}", prefix);
        if (replacements != null) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
        }
        // PlaceholderAPI vor der Faerbung aufloesen.
        PlaceholderHook papi = plugin.getPlaceholderHook();
        if (papi != null) {
            message = papi.apply(player, message);
        }
        return ColorUtil.colorize(message);
    }

    /**
     * Sendet eine formatierte Nachricht an einen Empfaenger.
     *
     * @param sender       der Empfaenger
     * @param key          der Nachrichtenschluessel
     * @param replacements optionale Ersetzungen
     */
    public void send(CommandSender sender, String key, Map<String, String> replacements) {
        Player player = sender instanceof Player ? (Player) sender : null;
        String message = format(player, key, replacements);
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(message);
        }
    }

    /**
     * Bequemlichkeitsmethode ohne zusaetzliche Ersetzungen.
     */
    public void send(CommandSender sender, String key) {
        send(sender, key, new HashMap<>());
    }
}
