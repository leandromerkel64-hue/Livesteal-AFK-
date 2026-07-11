package com.keygui.command;

import com.keygui.KeyGUIPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Behandelt den Befehl {@code /keygui} (Alias {@code /keys}).
 *
 * <p>Der Befehl oeffnet fuer berechtigte Spieler das KeyGUI. Die eigentliche
 * Key-Vergabe erfolgt erst durch einen Klick im GUI.</p>
 */
public class KeyGuiCommand implements CommandExecutor {

    private final KeyGUIPlugin plugin;

    public KeyGuiCommand(KeyGUIPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "players-only");
            return true;
        }

        if (!player.hasPermission("keygui.use")) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        // Optionaler Admin-Unterbefehl zum Neuladen der Konfiguration.
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")
                && player.hasPermission("keygui.admin")) {
            plugin.reloadAll();
            player.sendMessage(plugin.getMessageManager().formatText(player,
                    "{prefix}&aKonfiguration neu geladen.", null));
            return true;
        }

        if (plugin.getCoreCratesHook() == null) {
            plugin.getMessageManager().send(player, "corecrates-missing");
            return true;
        }

        if (plugin.getCoreCratesHook().getCrates().isEmpty()) {
            plugin.getMessageManager().send(player, "no-crates");
            return true;
        }

        plugin.getKeyGuiManager().open(player);
        return true;
    }
}
