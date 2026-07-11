package com.keygui;

import com.keygui.config.ConfigManager;
import com.keygui.config.MessageManager;
import com.keygui.hook.CoreCratesHook;
import com.keygui.util.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Kapselt die eigentliche Fachlogik der Key-Vergabe.
 *
 * <p>Trennt die Geschaeftslogik von GUI und Befehlen, sodass sie an einer
 * einzigen Stelle getestet und gewartet werden kann.</p>
 */
public class KeyService {

    private final KeyGUIPlugin plugin;

    public KeyService(KeyGUIPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gibt allen aktuell online befindlichen Spielern genau einen Key der
     * angegebenen Crate.
     *
     * @param initiator der ausloesende Spieler (fuer Cooldown und Feedback)
     * @param crateId   die interne Crate-ID
     */
    public void giveKeyToAll(Player initiator, String crateId) {
        ConfigManager cfg = plugin.getConfigManager();
        MessageManager messages = plugin.getMessageManager();
        CoreCratesHook hook = plugin.getCoreCratesHook();
        CooldownManager cooldowns = plugin.getCooldownManager();

        // CoreCrates verfuegbar?
        if (hook == null) {
            messages.send(initiator, "corecrates-missing");
            cfg.playSound(initiator, "error");
            return;
        }

        // Crate existiert noch?
        if (!hook.crateExists(crateId)) {
            messages.send(initiator, "crate-not-found");
            cfg.playSound(initiator, "error");
            return;
        }

        // Cooldown pruefen.
        if (cfg.isCooldownEnabled() && cooldowns.isOnCooldown(initiator.getUniqueId())) {
            Map<String, String> repl = new HashMap<>();
            repl.put("{time}", String.valueOf(cooldowns.getRemainingSeconds(initiator.getUniqueId())));
            messages.send(initiator, "cooldown", repl);
            cfg.playSound(initiator, "error");
            return;
        }

        // Keys an alle Online-Spieler vergeben.
        int delivered = 0;
        String crateName = hook.getDisplayName(crateId);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (hook.giveKey(online, crateId)) {
                delivered++;
                cfg.playSound(online, "give");
            }
        }

        // Cooldown erst nach erfolgreicher Vergabe setzen.
        if (cfg.isCooldownEnabled()) {
            cooldowns.applyCooldown(initiator.getUniqueId(), cfg.getCooldownSeconds());
        }

        Map<String, String> repl = new HashMap<>();
        repl.put("{crate}", crateName);
        repl.put("{amount}", String.valueOf(delivered));

        // Empfaenger benachrichtigen.
        for (Player online : Bukkit.getOnlinePlayers()) {
            messages.send(online, "key-received", repl);
        }

        // Ausfuehrenden Spieler bestaetigen.
        messages.send(initiator, "key-given", repl);

        // Broadcast senden.
        if (cfg.isBroadcastEnabled()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(messages.format(online, "broadcast", repl));
            }
        }
    }
}
