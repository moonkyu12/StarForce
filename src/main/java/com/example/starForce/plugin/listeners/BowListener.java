package com.example.starForce.plugin.listeners;

import com.example.starForce.plugin.StarForce;
import com.example.starForce.plugin.service.EnhancementService;
import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;

public class BowListener implements Listener {

    private static final NamespacedKey BOW_DAMAGE_BONUS_KEY = new NamespacedKey(StarForce.getInstance(), "bow_damage_bonus");

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) event.getEntity().getShooter();
        ItemStack bow = shooter.getInventory().getItemInMainHand(); // Get the item in hand when shot

        if (bow.getType() != Material.BOW) { // Only interested in bows
            return;
        }
        
        // Check if the bow is an enhanceable weapon and has an enhancement level
        if (ItemUtil.isEnhanceableWeapon(bow) && EnhancementService.getEnhancementLevel(bow) > 0) {
            ItemMeta meta = bow.getItemMeta();
            if (meta == null) return;

            // Retrieve the StarForce damage modifier from the bow's meta
            double starForceBonus = 0;
            Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"));
            if (modifiers != null) {
                for (AttributeModifier modifier : modifiers) {
                    if (EnhancementService.STARFORCE_DAMAGE_UUID.equals(modifier.getUniqueId()) && modifier.getOperation() == Operation.MULTIPLY_SCALAR_1) {
                        starForceBonus = modifier.getAmount();
                        break;
                    }
                }
            }

            if (starForceBonus > 0) {
                // Store the bonus in the arrow's PersistentDataContainer
                event.getEntity().getPersistentDataContainer().set(BOW_DAMAGE_BONUS_KEY, PersistentDataType.DOUBLE, starForceBonus);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) event.getDamager();

        // Check if the arrow has our custom damage bonus
        if (arrow.getPersistentDataContainer().has(BOW_DAMAGE_BONUS_KEY, PersistentDataType.DOUBLE)) {
            double starForceBonus = arrow.getPersistentDataContainer().get(BOW_DAMAGE_BONUS_KEY, PersistentDataType.DOUBLE);

            if (starForceBonus > 0) {
                double originalDamage = event.getDamage();
                double newDamage = originalDamage * (1 + starForceBonus);
                event.setDamage(newDamage);
            }
        }
    }
}
