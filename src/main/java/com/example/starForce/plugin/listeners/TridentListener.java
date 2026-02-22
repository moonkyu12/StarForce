package com.example.starForce.plugin.listeners;

import com.example.starForce.plugin.service.EnhancementService;
import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.UUID;

public class TridentListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Trident)) {
            return;
        }

        Trident trident = (Trident) event.getDamager();
        if (!(trident.getShooter() instanceof Player)) { // Only apply if thrown by a player
            return;
        }

        ItemStack thrownTridentItem = trident.getItemStack(); // Get the item stack the trident originated from

        // Check if the trident is an enhanceable weapon and has an enhancement level
        if (ItemUtil.isEnhanceableWeapon(thrownTridentItem) && EnhancementService.getEnhancementLevel(thrownTridentItem) > 0) {
            ItemMeta meta = thrownTridentItem.getItemMeta();
            if (meta == null) return;

            // Retrieve the StarForce damage modifier from the item's meta
            Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.valueOf("GENERIC_ATTACK_DAMAGE"));
            if (modifiers == null) return;

            double starForceBonus = 0;
            for (AttributeModifier modifier : modifiers) {
                if (EnhancementService.STARFORCE_DAMAGE_UUID.equals(modifier.getUniqueId()) && modifier.getOperation() == Operation.MULTIPLY_SCALAR_1) {
                    starForceBonus = modifier.getAmount();
                    break;
                }
            }

            if (starForceBonus > 0) {
                double originalDamage = event.getDamage();
                double newDamage = originalDamage * (1 + starForceBonus);
                event.setDamage(newDamage);
            }
        }
    }
}
