package com.example.starForce.plugin.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MagicForceUI {

    public static final String INVENTORY_TITLE = "스타★포스 인벤토리";
    public static final int ITEM_SLOT = 13;

    private static final ItemStack PLACEHOLDER;

    static {
        PLACEHOLDER = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = PLACEHOLDER.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            PLACEHOLDER.setItemMeta(meta);
        }
    }

    public static void openMagicForceUI(Player player) {
        Inventory ui = Bukkit.createInventory(null, 27, INVENTORY_TITLE);

        // Fill inventory with the static placeholder
        for (int i = 0; i < ui.getSize(); i++) {
            if (i != ITEM_SLOT) {
                ui.setItem(i, PLACEHOLDER);
            }
        }

        player.openInventory(ui);
    }
}
