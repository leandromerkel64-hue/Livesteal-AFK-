package com.keygui.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link InventoryHolder} zur eindeutigen Identifikation des KeyGUI.
 *
 * <p>Ueber den Holder kann der Listener zuverlaessig erkennen, ob ein
 * Inventar zu diesem Plugin gehoert (statt fehleranfaellig ueber den Titel),
 * und die angeklickten Slots den jeweiligen Crate-IDs zuordnen.</p>
 */
public class KeyGuiHolder implements InventoryHolder {

    /** Zuordnung: Slot -> Crate-ID. */
    private final Map<Integer, String> slotToCrate = new HashMap<>();
    private Inventory inventory;

    /**
     * Registriert, welche Crate in welchem Slot liegt.
     */
    public void mapSlot(int slot, String crateId) {
        slotToCrate.put(slot, crateId);
    }

    /**
     * @return die Crate-ID im angegebenen Slot oder {@code null}
     */
    public String getCrateId(int slot) {
        return slotToCrate.get(slot);
    }

    void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
