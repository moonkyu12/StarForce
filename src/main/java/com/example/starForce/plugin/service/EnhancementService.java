package com.example.starForce.plugin.service;

import com.example.starForce.plugin.model.EnhancementProbabilities;
import com.example.starForce.plugin.model.EnhancementResponse;
import com.example.starForce.plugin.model.EnhancementResult;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnhancementService {

    private static final String LORE_PREFIX = ChatColor.GRAY + "강화: ";
    public static final int MAX_LEVEL = 10;
    private static final Random random = new Random();

    public static EnhancementResponse enhanceItem(ItemStack item) {
        if (item == null) {
            return new EnhancementResponse(null, EnhancementResult.FAILURE);
        }

        int currentLevel = getEnhancementLevel(item);
        if (currentLevel >= MAX_LEVEL) {
            return new EnhancementResponse(item, EnhancementResult.FAILURE); // Cannot enhance further
        }

        EnhancementResult result = tryEnhance(currentLevel);
        int newLevel = currentLevel;

        switch (result) {
            case SUCCESS:
                newLevel++;
                break;
            case FAILURE:
                // Level remains the same
                break;
            case DEMOTION:
                newLevel = Math.max(0, newLevel - 1);
                break;
            case DESTRUCTION:
                return new EnhancementResponse(null, EnhancementResult.DESTRUCTION); // Item is destroyed
        }

        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        if (meta == null) {
            return new EnhancementResponse(item, EnhancementResult.FAILURE); // Should not happen
        }

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        int starLineIndex = -1;
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith(LORE_PREFIX)) {
                starLineIndex = i;
                break;
            }
        }

        String newStarLine = generateStarLore(newLevel);

        if (starLineIndex != -1) {
            lore.set(starLineIndex, newStarLine);
        } else {
            lore.add(0, newStarLine);
        }

        meta.setLore(lore);
        newItem.setItemMeta(meta);

        return new EnhancementResponse(newItem, result);
    }

    private static EnhancementResult tryEnhance(int currentLevel) {
        double roll = random.nextDouble() * 100;
        double successChance, demotionChance, destructionChance;

        switch (currentLevel) {
            case 0:  // 1 Star
                return EnhancementResult.SUCCESS;
            case 1:  // 2 Stars
                successChance = 90;
                break;
            case 2:  // 3 Stars
                successChance = 80;
                break;
            case 3:  // 4 Stars
                successChance = 70;
                break;
            case 4:  // 5 Stars
                successChance = 50;
                break;
            case 5:  // 6 Stars
                successChance = 40;
                demotionChance = 20;
                if (roll < successChance) return EnhancementResult.SUCCESS;
                if (roll < successChance + demotionChance) return EnhancementResult.DEMOTION;
                return EnhancementResult.FAILURE;
            case 6:  // 7 Stars
                successChance = 20;
                demotionChance = 30;
                destructionChance = 10;
                if (roll < successChance) return EnhancementResult.SUCCESS;
                if (roll < successChance + demotionChance) return EnhancementResult.DEMOTION;
                if (roll < successChance + demotionChance + destructionChance) return EnhancementResult.DESTRUCTION;
                return EnhancementResult.FAILURE;
            case 7:  // 8 Stars
                successChance = 10;
                demotionChance = 30;
                destructionChance = 30;
                if (roll < successChance) return EnhancementResult.SUCCESS;
                if (roll < successChance + demotionChance) return EnhancementResult.DEMOTION;
                if (roll < successChance + demotionChance + destructionChance) return EnhancementResult.DESTRUCTION;
                return EnhancementResult.FAILURE;
            case 8:  // 9 Stars
                successChance = 5;
                demotionChance = 20;
                destructionChance = 55;
                if (roll < successChance) return EnhancementResult.SUCCESS;
                if (roll < successChance + demotionChance) return EnhancementResult.DEMOTION;
                if (roll < successChance + demotionChance + destructionChance) return EnhancementResult.DESTRUCTION;
                return EnhancementResult.FAILURE;
            case 9:  // 10 Stars
                successChance = 5;
                demotionChance = 20;
                destructionChance = 75;
                if (roll < successChance) return EnhancementResult.SUCCESS;
                if (roll < successChance + demotionChance) return EnhancementResult.DEMOTION;
                if (roll < successChance + demotionChance + destructionChance) return EnhancementResult.DESTRUCTION;
                return EnhancementResult.FAILURE;
            default:
                return EnhancementResult.FAILURE;
        }

        if (roll < successChance) {
            return EnhancementResult.SUCCESS;
        } else {
            return EnhancementResult.FAILURE;
        }
    }

    public static int getEnhancementLevel(ItemStack item) {
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

    public static EnhancementProbabilities getProbabilities(int currentLevel) {
        switch (currentLevel) {
            case 0: return new EnhancementProbabilities(100, 0, 0, 0);
            case 1: return new EnhancementProbabilities(90, 10, 0, 0);
            case 2: return new EnhancementProbabilities(80, 20, 0, 0);
            case 3: return new EnhancementProbabilities(70, 30, 0, 0);
            case 4: return new EnhancementProbabilities(50, 50, 0, 0);
            case 5: return new EnhancementProbabilities(40, 40, 20, 0);
            case 6: return new EnhancementProbabilities(20, 40, 30, 10);
            case 7: return new EnhancementProbabilities(10, 30, 30, 30);
            case 8: return new EnhancementProbabilities(5, 20, 20, 55);
            case 9: return new EnhancementProbabilities(5, 0, 20, 75);
            default: return new EnhancementProbabilities(0, 0, 0, 0); // Max level or invalid
        }
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
