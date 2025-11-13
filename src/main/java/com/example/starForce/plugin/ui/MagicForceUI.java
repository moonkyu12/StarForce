package com.example.starForce.plugin.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MagicForceUI {

    public static void openMagicForceUI(Player player) {
        Inventory ui = Bukkit.createInventory(null, 27, "MagicForce UI");
        player.openInventory(ui);
    }
}
