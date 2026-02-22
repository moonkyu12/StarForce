package com.example.starForce.plugin.util;

import com.example.starForce.plugin.StarForce;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemUtil {

    // Unique UUID for our base attribute modifiers, to distinguish from enchantment/plugin ones
    private static final UUID BASE_DAMAGE_UUID = UUID.fromString("1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a");
    private static final UUID BASE_SPEED_UUID = UUID.fromString("2b2b2b2b-2b2b-2b2b-2b2b-2b2b2b2b2b2b");
    private static final UUID BASE_ARMOR_UUID = UUID.fromString("3c3c3c3c-3c3c-3c3c-3c3c-3c3c3c3c3c3c");
    private static final UUID BASE_TOUGHNESS_UUID = UUID.fromString("4d4d4d4d-4d4d-4d4d-4d4d-4d4d4d4d4d4d");
    private static final NamespacedKey PROTECTION_SCROLL_KEY = new NamespacedKey(StarForce.getPlugin(StarForce.class), "protection_scroll");


    public static ItemStack createProtectionScroll(int amount) {
        ItemStack scroll = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = scroll.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "파괴 방지권");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "강화 시 아이템 파괴를 1회 방지합니다."
        ));
        meta.getPersistentDataContainer().set(PROTECTION_SCROLL_KEY, PersistentDataType.BYTE, (byte) 1);
        scroll.setItemMeta(meta);
        return scroll;
    }

    public static boolean isProtectionScroll(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(PROTECTION_SCROLL_KEY, PersistentDataType.BYTE);
    }

    public static boolean useProtectionScroll(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (isProtectionScroll(item)) {
                item.setAmount(item.getAmount() - 1);
                inventory.setItem(i, item);
                return true;
            }
        }
        return false;
    }


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
                typeName.equals("ELYTRA") ||
                typeName.equals("MACE");
    }

    public static boolean isEnhanceableWeapon(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        String typeName = item.getType().name();
        return typeName.endsWith("_SWORD") || typeName.equals("TRIDENT") || typeName.equals("MACE") || typeName.equals("BOW");
    }

    public static boolean isArmor(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        String typeName = item.getType().name();
        return typeName.endsWith("_HELMET") ||
               typeName.endsWith("_CHESTPLATE") ||
               typeName.endsWith("_LEGGINGS") ||
               typeName.endsWith("_BOOTS");
    }

    public static double getBaseArmor(Material material) {
        switch (material) {
            case LEATHER_HELMET:
            case LEATHER_BOOTS:
                return 1.0;
            case LEATHER_LEGGINGS:
                return 2.0;
            case LEATHER_CHESTPLATE:
                return 3.0;
            case CHAINMAIL_HELMET:
            case GOLDEN_HELMET:
            case GOLDEN_BOOTS:
                return 2.0;
            case CHAINMAIL_BOOTS:
                return 1.0; // Corrected
            case CHAINMAIL_LEGGINGS:
            case IRON_HELMET:
            case IRON_BOOTS:
                return 2.0; // Corrected
            case IRON_LEGGINGS:
            case DIAMOND_HELMET:
            case DIAMOND_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_BOOTS:
                return 3.0;
            case IRON_CHESTPLATE:
                return 5.0; // Corrected
            case GOLDEN_LEGGINGS:
                return 3.0;
            case GOLDEN_CHESTPLATE:
                return 5.0;
            case DIAMOND_LEGGINGS:
            case NETHERITE_LEGGINGS:
                return 6.0;
            case DIAMOND_CHESTPLATE:
            case NETHERITE_CHESTPLATE:
                return 8.0;
            default:
                return 0.0;
        }
    }

    public static double getBaseArmorToughness(Material material) {
        switch (material) {
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return 2.0;
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
                return 3.0;
            default:
                return 0.0;
        }
    }

    public static double getBaseAttackDamage(Material material) {
        // Base attack damage values for common swords (Minecraft wiki values)
        switch (material) {
            case WOODEN_SWORD:
                return 4.0;
            case STONE_SWORD:
                return 5.0;
            case IRON_SWORD:
                return 6.0;
            case GOLDEN_SWORD:
                return 4.0;
            case DIAMOND_SWORD:
                return 7.0;
            case NETHERITE_SWORD:
                return 8.0;
            case TRIDENT:
                return 9.0; // Trident base damage
            case MACE:
                return 7.0; // Mace base damage (example value, adjust if needed)
            default:
                return 0.0;
        }
    }

    public static double getBaseAttackSpeed(Material material) {
        // Base attack speed values for common swords (Minecraft wiki values)
        switch (material) {
            case WOODEN_SWORD:
                return 1.6;
            case STONE_SWORD:
                return 1.6;
            case IRON_SWORD:
                return 1.6;
            case GOLDEN_SWORD:
                return 1.6;
            case DIAMOND_SWORD:
                return 1.6;
            case NETHERITE_SWORD:
                return 1.6;
            case TRIDENT:
                return 1.0; // Trident base attack speed
            case MACE:
                return 0.6; // Mace base attack speed (example value, adjust if needed)
            default:
                return 0.0; // Non-swords or unknown
        }
    }

    public static void ensureBaseAttributes(ItemMeta meta, Material material) {
        if (isEnhanceableWeapon(new ItemStack(material))) { // Check if it's an enhanceable weapon material
            // Ensure GENERIC_ATTACK_DAMAGE
            if (meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ATTACK_DAMAGE")) == null || meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ATTACK_DAMAGE")).isEmpty()) {
                double baseDamage = getBaseAttackDamage(material);
                if (baseDamage > 0) {
                    AttributeModifier damageModifier = new AttributeModifier(
                        BASE_DAMAGE_UUID,
                        "generic.attack_damage", // Vanilla name, but can be anything
                        baseDamage,
                        Operation.ADD_NUMBER,
                        EquipmentSlot.HAND
                    );
                    meta.addAttributeModifier(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"), damageModifier);
                }
            }

            // Ensure GENERIC_ATTACK_SPEED
            if (meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ATTACK_SPEED")) == null || meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ATTACK_SPEED")).isEmpty()) {
                double baseSpeed = getBaseAttackSpeed(material);
                if (baseSpeed > 0) {
                    AttributeModifier speedModifier = new AttributeModifier(
                        BASE_SPEED_UUID,
                        "generic.attack_speed", // Vanilla name, but can be anything
                        baseSpeed - 4.0, // Minecraft's base attack speed is 4.0, so swords add to this
                        Operation.ADD_NUMBER,
                        EquipmentSlot.HAND
                    );
                    meta.addAttributeModifier(Attribute.valueOf("GENERIC_ATTACK_SPEED"), speedModifier);
                }
            }
        } else if (isArmor(new ItemStack(material))) { // Check if it's armor
            EquipmentSlot slot = getEquipmentSlot(material);

            // Ensure GENERIC_ARMOR
            if (meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ARMOR")) == null || meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ARMOR")).isEmpty()) {
                double baseArmor = getBaseArmor(material);
                if (baseArmor > 0) {
                    AttributeModifier armorModifier = new AttributeModifier(
                        BASE_ARMOR_UUID,
                        "generic.armor",
                        baseArmor,
                        Operation.ADD_NUMBER,
                        slot
                    );
                    meta.addAttributeModifier(Attribute.valueOf("GENERIC_ARMOR"), armorModifier);
                }
            }

            // Ensure GENERIC_ARMOR_TOUGHNESS
            if (meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ARMOR_TOUGHNESS")) == null || meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ARMOR_TOUGHNESS")).isEmpty()) {
                double baseToughness = getBaseArmorToughness(material);
                if (baseToughness > 0) {
                    AttributeModifier toughnessModifier = new AttributeModifier(
                        BASE_TOUGHNESS_UUID,
                        "generic.armor_toughness",
                        baseToughness,
                        Operation.ADD_NUMBER,
                        slot
                    );
                    meta.addAttributeModifier(Attribute.valueOf("GENERIC_ARMOR_TOUGHNESS"), toughnessModifier);
                }
            }
        }
    }

    public static EquipmentSlot getEquipmentSlot(Material material) {
        String typeName = material.name();
        if (typeName.endsWith("_HELMET")) {
            return EquipmentSlot.HEAD;
        } else if (typeName.endsWith("_CHESTPLATE")) {
            return EquipmentSlot.CHEST;
        } else if (typeName.endsWith("_LEGGINGS")) {
            return EquipmentSlot.LEGS;
        } else if (typeName.endsWith("_BOOTS")) {
            return EquipmentSlot.FEET;
        } else {
            return EquipmentSlot.HAND; // Default or for weapons
        }
    }
}

