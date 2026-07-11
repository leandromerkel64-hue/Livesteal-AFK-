package com.keygui.gui;

import com.keygui.KeyGUIPlugin;
import com.keygui.config.ConfigManager;
import com.keygui.hook.CrateData;
import com.keygui.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Baut das KeyGUI auf und oeffnet es fuer Spieler.
 *
 * <p>Alle darstellungsrelevanten Werte (Groesse, Titel, Slots, Fuell-Items,
 * Item-Material/-Name/-Lore) stammen aus der {@link ConfigManager}.</p>
 */
public class KeyGuiManager {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final KeyGUIPlugin plugin;

    public KeyGuiManager(KeyGUIPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Oeffnet das KeyGUI fuer den angegebenen Spieler.
     *
     * @param player der Spieler, fuer den das GUI gebaut wird
     */
    public void open(Player player) {
        ConfigManager cfg = plugin.getConfigManager();

        List<CrateData> crates = plugin.getCoreCratesHook() != null
                ? plugin.getCoreCratesHook().getCrates()
                : new ArrayList<>();

        int size = cfg.getGuiSize();
        String title = plugin.getMessageManager().formatText(player, cfg.getGuiTitle(), new HashMap<>());

        KeyGuiHolder holder = new KeyGuiHolder();
        Inventory inventory = Bukkit.createInventory(holder, size, toComponent(title));
        holder.setInventory(inventory);

        // 1) Fuell-Items setzen (bevor Crates platziert werden).
        applyFiller(cfg, inventory, size);

        // 2) Crate-Slots bestimmen.
        List<Integer> slots = resolveCrateSlots(cfg.getCrateSlots(), crates.size(), size);

        // 3) Crate-Items platzieren.
        for (int i = 0; i < crates.size() && i < slots.size(); i++) {
            int slot = slots.get(i);
            if (slot < 0 || slot >= size) {
                continue;
            }
            CrateData crate = crates.get(i);
            inventory.setItem(slot, buildCrateItem(player, cfg, crate));
            holder.mapSlot(slot, crate.id());
        }

        player.openInventory(inventory);
        cfg.playSound(player, "open");
    }

    /**
     * Legt fest, in welchen Slots die Crates dargestellt werden.
     *
     * <p>Sind explizite Slots konfiguriert, werden diese verwendet. Andernfalls
     * werden die Crates fortlaufend ab Slot 0 platziert.</p>
     */
    private List<Integer> resolveCrateSlots(List<Integer> configured, int crateCount, int size) {
        if (configured != null && !configured.isEmpty()) {
            return configured;
        }
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < crateCount && i < size; i++) {
            slots.add(i);
        }
        return slots;
    }

    /**
     * Befuellt die konfigurierten (oder alle) leeren Slots mit dem Fuell-Item.
     */
    private void applyFiller(ConfigManager cfg, Inventory inventory, int size) {
        if (!cfg.isFillerEnabled()) {
            return;
        }
        Material material = parseMaterial(cfg.getFillerMaterialName(), Material.BLACK_STAINED_GLASS_PANE);
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.displayName(toComponent(ColorUtil.colorize(cfg.getFillerName())));
            filler.setItemMeta(meta);
        }

        List<Integer> fillerSlots = cfg.getFillerSlots();
        if (fillerSlots.isEmpty()) {
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, filler.clone());
            }
        } else {
            for (int slot : fillerSlots) {
                if (slot >= 0 && slot < size) {
                    inventory.setItem(slot, filler.clone());
                }
            }
        }
    }

    /**
     * Baut das GUI-Item fuer eine einzelne Crate.
     */
    private ItemStack buildCrateItem(Player player, ConfigManager cfg, CrateData crate) {
        Material material;
        String configured = cfg.getCrateItemMaterial();
        if (configured == null || configured.equalsIgnoreCase("AUTO")) {
            material = crate.icon() != null ? crate.icon() : Material.CHEST;
        } else {
            material = parseMaterial(configured, Material.CHEST);
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("{crate}", crate.displayName());
            replacements.put("{crate_id}", crate.id());

            String name = plugin.getMessageManager().formatText(player, cfg.getCrateItemName(), replacements);
            meta.displayName(toComponent(name));

            List<Component> lore = new ArrayList<>();
            for (String line : cfg.getCrateItemLore()) {
                lore.add(toComponent(plugin.getMessageManager().formatText(player, line, replacements)));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Wandelt einen (bereits gefaerbten) Legacy-String in eine Adventure-Component
     * um und deaktiviert die standardmaessige Kursiv-Darstellung von Items.
     */
    private Component toComponent(String legacyColored) {
        return LEGACY.deserialize(legacyColored == null ? "" : legacyColored)
                .decoration(TextDecoration.ITALIC, false);
    }

    private Material parseMaterial(String name, Material fallback) {
        if (name == null) {
            return fallback;
        }
        Material material = Material.matchMaterial(name.trim().toUpperCase());
        return material != null ? material : fallback;
    }
}
