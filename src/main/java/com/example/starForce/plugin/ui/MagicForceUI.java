package com.example.starForce.plugin.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MagicForceUI {

    public static void openMagicForceUI(Player player) {
        Inventory ui = Bukkit.createInventory(null, 27, "스타★포스 인벤토리");
        player.openInventory(ui);
    }
}
