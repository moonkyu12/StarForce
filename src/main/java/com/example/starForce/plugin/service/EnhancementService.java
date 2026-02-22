package com.example.starForce.plugin.service;

import com.example.starForce.plugin.EnhancementProbabilities;
import com.example.starForce.plugin.EnhancementResponse;
import com.example.starForce.plugin.EnhancementResult;
import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Collection;
import java.util.Iterator;

public class EnhancementService {

    private static final String LORE_PREFIX = ChatColor.GRAY + "강화: ";
    public static final int MAX_LEVEL = 10;
    private static final Random random = new Random();
    public static final UUID STARFORCE_DAMAGE_UUID = UUID.fromString("6a04a6e8-2b81-4b10-8b01-5e7e0e7e0e7e");
    public static final UUID STARFORCE_ARMOR_UUID = UUID.fromString("b45065e1-2b81-4b10-8b01-5e7e0e7e0e7e");
    public static final UUID STARFORCE_TOUGHNESS_UUID = UUID.fromString("c7809a12-2b81-4b10-8b01-5e7e0e7e0e7e");

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

        // Calculate cumulative bonusPercentage
        double bonusPercentage = 0;
        for (int i = 1; i <= newLevel; i++) {
            if (i >= 1 && i <= 2) {
                bonusPercentage += 0.10; // Add 10% for levels 1 and 2
            } else if (i >= 3 && i <= 5) {
                bonusPercentage += 0.20; // Add 20% for levels 3, 4, 5
            } else if (i >= 6 && i <= 7) {
                bonusPercentage += 0.30; // Add 30% for levels 6, 7
            } else if (i >= 8 && i <= 10) {
                bonusPercentage += 0.50; // Add 50% for levels 8, 9, 10
            }
        }

        // Ensure base stats are present before applying starforce modifiers
        ItemUtil.ensureBaseAttributes(meta, newItem.getType());

        // --- AttributeModifier Logic ---
        // Bukkit.getLogger().info("StarForce: Entering enhanceItem for item: " + (newItem != null ? newItem.getType().name() : "null") + ", newLevel: " + newLevel + ", bonusPercentage: " + bonusPercentage);

        if (ItemUtil.isEnhanceableWeapon(newItem)) {
            EquipmentSlot slot = EquipmentSlot.HAND; // Weapons are typically in hand
            // Remove existing StarForce damage modifiers to prevent stacking
            Collection<AttributeModifier> currentModifiers = meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"));
            if (currentModifiers != null) {
                Iterator<AttributeModifier> iterator = currentModifiers.iterator();
                while (iterator.hasNext()) {
                    AttributeModifier modifier = iterator.next();
                    if (STARFORCE_DAMAGE_UUID.equals(modifier.getUniqueId())) {
                        // Bukkit.getLogger().info("StarForce: Removing existing StarForceDamage modifier: " + modifier.getName() + " (" + modifier.getAmount() + ")");
                        meta.removeAttributeModifier(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"), modifier);
                    }
                }
            }

            if (bonusPercentage > 0) {
                AttributeModifier modifier = new AttributeModifier(
                    STARFORCE_DAMAGE_UUID,
                    "StarForceDamage",
                    bonusPercentage,
                    Operation.MULTIPLY_SCALAR_1, // Use MULTIPLY_SCALAR_1 for percentage display
                    slot
                );
                meta.addAttributeModifier(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"), modifier);
                // Bukkit.getLogger().info("StarForce: Added new StarForceDamage modifier: " + modifier.getName() + " (" + modifier.getAmount() + ", " + modifier.getOperation() + ")");
            } else {
                // Bukkit.getLogger().info("StarForce: No bonusPercentage, ensuring existing modifiers are removed.");
            }
        } else if (ItemUtil.isArmor(newItem)) {
            EquipmentSlot slot = ItemUtil.getEquipmentSlot(newItem.getType());
            // GENERIC_ARMOR
            Collection<AttributeModifier> currentArmorModifiers = meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ARMOR"));
            if (currentArmorModifiers != null) {
                Iterator<AttributeModifier> iterator = currentArmorModifiers.iterator();
                while (iterator.hasNext()) {
                    AttributeModifier modifier = iterator.next();
                    if (STARFORCE_ARMOR_UUID.equals(modifier.getUniqueId())) {
                        meta.removeAttributeModifier(Attribute.valueOf("GENERIC_ARMOR"), modifier);
                    }
                }
            }
            if (bonusPercentage > 0) {
                AttributeModifier armorModifier = new AttributeModifier(
                    STARFORCE_ARMOR_UUID,
                    "StarForceArmor",
                    bonusPercentage,
                    Operation.MULTIPLY_SCALAR_1,
                    slot
                );
                meta.addAttributeModifier(Attribute.valueOf("GENERIC_ARMOR"), armorModifier);
            }

            // GENERIC_ARMOR_TOUGHNESS
            Collection<AttributeModifier> currentToughnessModifiers = meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ARMOR_TOUGHNESS"));
            if (currentToughnessModifiers != null) {
                Iterator<AttributeModifier> iterator = currentToughnessModifiers.iterator();
                while (iterator.hasNext()) {
                    AttributeModifier modifier = iterator.next();
                    if (STARFORCE_TOUGHNESS_UUID.equals(modifier.getUniqueId())) {
                        meta.removeAttributeModifier(Attribute.valueOf("GENERIC_ARMOR_TOUGHNESS"), modifier);
                    }
                }
            }
            if (bonusPercentage > 0) {
                AttributeModifier toughnessModifier = new AttributeModifier(
                    STARFORCE_TOUGHNESS_UUID,
                    "StarForceArmorToughness",
                    bonusPercentage,
                    Operation.MULTIPLY_SCALAR_1,
                    slot
                );
                meta.addAttributeModifier(Attribute.valueOf("GENERIC_ARMOR_TOUGHNESS"), toughnessModifier);
            }
        }
        // --- End AttributeModifier Logic ---


        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        // Remove existing StarForce related lore (only LORE_PREFIX for stars now)
        // Minecraft will automatically generate lore for AttributeModifiers
        lore.removeIf(line -> line.startsWith(LORE_PREFIX));

        // Add enhancement level lore
        String newStarLine = generateStarLore(newLevel);
        lore.add(0, newStarLine); // Always add star lore at the top

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

    public static ItemStack setEnhancementLevel(ItemStack item, int newLevel) {
        if (item == null) {
            return null;
        }

        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        if (meta == null) {
            return item; // Should not happen
        }

        // Calculate cumulative bonusPercentage for the target level
        double bonusPercentage = 0;
        for (int i = 1; i <= newLevel; i++) {
            if (i >= 1 && i <= 2) {
                bonusPercentage += 0.10; // Add 10% for levels 1 and 2
            } else if (i >= 3 && i <= 5) {
                bonusPercentage += 0.20; // Add 20% for levels 3, 4, 5
            } else if (i >= 6 && i <= 7) {
                bonusPercentage += 0.30; // Add 30% for levels 6, 7
            } else if (i >= 8 && i <= 10) {
                bonusPercentage += 0.50; // Add 50% for levels 8, 9, 10
            }
        }

        // Ensure base stats are present before applying starforce modifiers
        ItemUtil.ensureBaseAttributes(meta, newItem.getType());

        // --- AttributeModifier Logic ---
        if (ItemUtil.isEnhanceableWeapon(newItem)) {
            EquipmentSlot slot = EquipmentSlot.HAND; // Weapons are typically in hand
            // Remove existing StarForce damage modifiers to prevent stacking
            Collection<AttributeModifier> currentModifiers = meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"));
            if (currentModifiers != null) {
                Iterator<AttributeModifier> iterator = currentModifiers.iterator();
                while (iterator.hasNext()) {
                    AttributeModifier modifier = iterator.next();
                    if (STARFORCE_DAMAGE_UUID.equals(modifier.getUniqueId())) {
                        meta.removeAttributeModifier(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"), modifier);
                    }
                }
            }

            if (bonusPercentage > 0) {
                AttributeModifier modifier = new AttributeModifier(
                    STARFORCE_DAMAGE_UUID,
                    "StarForceDamage",
                    bonusPercentage,
                    Operation.MULTIPLY_SCALAR_1,
                    slot
                );
                meta.addAttributeModifier(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"), modifier);
            }
        } else if (ItemUtil.isArmor(newItem)) {
            EquipmentSlot slot = ItemUtil.getEquipmentSlot(newItem.getType());
            // GENERIC_ARMOR
            Collection<AttributeModifier> currentArmorModifiers = meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ARMOR"));
            if (currentArmorModifiers != null) {
                Iterator<AttributeModifier> iterator = currentArmorModifiers.iterator();
                while (iterator.hasNext()) {
                    AttributeModifier modifier = iterator.next();
                    if (STARFORCE_ARMOR_UUID.equals(modifier.getUniqueId())) {
                        meta.removeAttributeModifier(Attribute.valueOf("GENERIC_ARMOR"), modifier);
                    }
                }
            }
            if (bonusPercentage > 0) {
                AttributeModifier armorModifier = new AttributeModifier(
                    STARFORCE_ARMOR_UUID,
                    "StarForceArmor",
                    bonusPercentage,
                    Operation.MULTIPLY_SCALAR_1,
                    slot
                );
                meta.addAttributeModifier(Attribute.valueOf("GENERIC_ARMOR"), armorModifier);
            }

            // GENERIC_ARMOR_TOUGHNESS
            Collection<AttributeModifier> currentToughnessModifiers = meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ARMOR_TOUGHNESS"));
            if (currentToughnessModifiers != null) {
                Iterator<AttributeModifier> iterator = currentToughnessModifiers.iterator();
                while (iterator.hasNext()) {
                    AttributeModifier modifier = iterator.next();
                    if (STARFORCE_TOUGHNESS_UUID.equals(modifier.getUniqueId())) {
                        meta.removeAttributeModifier(Attribute.valueOf("GENERIC_ARMOR_TOUGHNESS"), modifier);
                    }
                }
            }
            if (bonusPercentage > 0) {
                AttributeModifier toughnessModifier = new AttributeModifier(
                    STARFORCE_TOUGHNESS_UUID,
                    "StarForceArmorToughness",
                    bonusPercentage,
                    Operation.MULTIPLY_SCALAR_1,
                    slot
                );
                meta.addAttributeModifier(Attribute.valueOf("GENERIC_ARMOR_TOUGHNESS"), toughnessModifier);
            }
        }
        // --- End AttributeModifier Logic ---

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.removeIf(line -> line.startsWith(LORE_PREFIX));

        if (newLevel > 0) {
            String newStarLine = generateStarLore(newLevel);
            lore.add(0, newStarLine);
        }

        meta.setLore(lore);
        newItem.setItemMeta(meta);

        return newItem;
    }
}
