package com.example.starForce.plugin.util;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.UUID;

public class ItemUtil {

    // Unique UUID for our base attribute modifiers, to distinguish from enchantment/plugin ones
    private static final UUID BASE_DAMAGE_UUID = UUID.fromString("1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a");
    private static final UUID BASE_SPEED_UUID = UUID.fromString("2b2b2b2b-2b2b-2b2b-2b2b-2b2b2b2b2b2b");


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

    public static boolean isSword(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        String typeName = item.getType().name();
        return typeName.endsWith("_SWORD");
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
            default:
                return 0.0; // Non-swords or unknown
        }
    }

    public static void ensureBaseAttributes(ItemMeta meta, Material material) {
        if (isSword(new ItemStack(material))) { // Check if it's a sword material
            // Ensure GENERIC_ATTACK_DAMAGE
            if (!meta.hasAttributeModifier(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"))) {
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
            if (!meta.hasAttributeModifier(Attribute.valueOf("GENERIC_ATTACK_SPEED"))) {
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
        }
    }
}
