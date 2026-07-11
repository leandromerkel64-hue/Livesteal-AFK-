package com.keygui.listener;

import com.keygui.KeyGUIPlugin;
import com.keygui.gui.KeyGuiHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Verarbeitet Interaktionen mit dem KeyGUI.
 *
 * <p>Klicks werden grundsaetzlich abgebrochen, damit keine Items entnommen
 * werden koennen. Das GUI bleibt nach einem Klick geoeffnet und schliesst
 * sich nur, wenn der Spieler es selbst schliesst (z. B. via ESC).</p>
 */
public class GuiListener implements Listener {

    private final KeyGUIPlugin plugin;

    public GuiListener(KeyGUIPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof KeyGuiHolder keyHolder)) {
            return;
        }

        // Jegliche Item-Manipulation im KeyGUI unterbinden.
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Nur Klicks im oberen (GUI-)Inventar auswerten.
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        String crateId = keyHolder.getCrateId(event.getRawSlot());
        if (crateId == null) {
            return;
        }

        // Sicherheitshalber die Berechtigung erneut pruefen.
        if (!player.hasPermission("keygui.use")) {
            plugin.getMessageManager().send(player, "no-permission");
            return;
        }

        plugin.getKeyService().giveKeyToAll(player, crateId);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof KeyGuiHolder) {
            event.setCancelled(true);
        }
    }
}
