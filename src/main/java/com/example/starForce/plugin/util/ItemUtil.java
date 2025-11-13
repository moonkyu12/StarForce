package com.example.starForce.plugin.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {

    public static boolean isItemAllowed(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        String typeName = item.getType().name();
        return typeName.endsWith("_HELMET") ||
                typeName.endsWith("_CHESTPLATE") ||
                typeName.endsWith("_LEGGINGS") ||
                typeName.endsWith("_BOOTS") ||
                typeName.endsWith("_SWORD") ||
                typeName.endsWith("_AXE") ||
                typeName.endsWith("_PICKAXE") ||
                typeName.equals("BOW") ||
                typeName.equals("TRIDENT") ||
                typeName.equals("ELYTRA");
    }
}
