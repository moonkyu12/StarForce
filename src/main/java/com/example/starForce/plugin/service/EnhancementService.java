package com.example.starForce.plugin.service;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnhancementService {

    private static final String LORE_PREFIX = ChatColor.GRAY + "강화: ";
    private static final int MAX_LEVEL = 10;

    public static ItemStack enhanceItem(ItemStack item) {
        if (item == null) {
            return null;
        }

        int currentLevel = getEnhancementLevel(item);
        if (currentLevel >= MAX_LEVEL) {
            return item; // Cannot enhance further
        }

        int newLevel = currentLevel + 1;

        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        if (meta == null) {
            return item; // Should not happen for allowed items
        }

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        // Find existing star lore index
        int starLineIndex = -1;
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith(LORE_PREFIX)) {
                starLineIndex = i;
                break;
            }
        }

        String newStarLine = generateStarLore(newLevel);

        if (starLineIndex != -1) {
            // Replace the existing star line to preserve order
            lore.set(starLineIndex, newStarLine);
        } else {
            // Add the new star line to the top if it doesn't exist
            lore.add(0, newStarLine);
        }

        meta.setLore(lore);
        newItem.setItemMeta(meta);

        return newItem;
    }

    private static int getEnhancementLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return 0;
        }
        for (String line : meta.getLore()) {
            if (line.startsWith(LORE_PREFIX)) {
                long filledStars = line.chars().filter(ch -> ch == '★').count();
                return (int) filledStars;
            }
        }
        return 0;
    }

    private static String generateStarLore(int level) {
        StringBuilder stars = new StringBuilder(LORE_PREFIX);
        stars.append(ChatColor.YELLOW);
        for (int i = 0; i < level; i++) {
            stars.append("★");
        }
        stars.append(ChatColor.GRAY);
        for (int i = 0; i < MAX_LEVEL - level; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }
}
