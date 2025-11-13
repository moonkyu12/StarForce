package com.example.starForce.plugin.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MagicForceUI {

    public static void openMagicForceUI(Player player) {
        Inventory ui = Bukkit.createInventory(null, 27, "스타★포스 인벤토리");

        // Create placeholder item
        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            placeholder.setItemMeta(meta);
        }

        // Fill inventory with placeholders
        for (int i = 0; i < ui.getSize(); i++) {
            if (i != 13) { // Leave slot 13 (the 14th slot) empty
                ui.setItem(i, placeholder);
            }
        }

        player.openInventory(ui);
    }
}
