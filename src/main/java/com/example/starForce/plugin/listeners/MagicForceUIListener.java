package com.example.starForce.plugin.listeners;

import com.example.starForce.plugin.service.EnhancementService;
import com.example.starForce.plugin.ui.MagicForceUI;
import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MagicForceUIListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(MagicForceUI.INVENTORY_TITLE)) {
            return;
        }

        // We are in our UI, so we can cancel the event by default and only allow specific actions.
        event.setCancelled(true);

        int rawSlot = event.getRawSlot();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();

        // Allow players to click in their own inventory
        if (clickedInventory != null && !clickedInventory.equals(topInventory)) {
            // But only allow shift-clicking valid items
            if (event.isShiftClick() && ItemUtil.isItemAllowed(event.getCurrentItem())) {
                event.setCancelled(false); // Let the default shift-click behavior happen
            } else if (!event.isShiftClick()) {
                event.setCancelled(false);
            }
            return;
        }

        // Handle clicks inside the top inventory
        if (rawSlot == MagicForceUI.ITEM_SLOT) {
            if (event.isRightClick()) {
                // Right-click is for enhancing
                ItemStack itemToEnhance = event.getCurrentItem();
                if (ItemUtil.isItemAllowed(itemToEnhance)) {
                    ItemStack enhancedItem = EnhancementService.enhanceItem(itemToEnhance);
                    topInventory.setItem(MagicForceUI.ITEM_SLOT, enhancedItem);
                }
                // Event remains cancelled
            } else {
                // Other clicks are for placing/taking items
                if (ItemUtil.isItemAllowed(event.getCursor()) || (event.getCursor() == null || event.getCursor().getType() == Material.AIR)) {
                    event.setCancelled(false);
                }
            }
        }
        // All other clicks in the top inventory (placeholders) remain cancelled.
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(MagicForceUI.INVENTORY_TITLE)) {
            return;
        }

        Inventory inventory = event.getInventory();
        ItemStack item = inventory.getItem(MagicForceUI.ITEM_SLOT);

        if (item != null) {
            Player player = (Player) event.getPlayer();
            Map<Integer, ItemStack> remainingItems = player.getInventory().addItem(item);
            if (!remainingItems.isEmpty()) {
                for (ItemStack remainingItem : remainingItems.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), remainingItem);
                }
            }
        }
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals(MagicForceUI.INVENTORY_TITLE)) {
            return;
        }

        boolean touchesTopInventory = false;
        for (int slot : event.getRawSlots()) {
            if (slot < event.getView().getTopInventory().getSize()) {
                touchesTopInventory = true;
                // If it touches a blocked slot, cancel immediately
                if (slot != MagicForceUI.ITEM_SLOT) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // If the drag touches the top inventory (which must be only in the allowed slot)
        // check if the item type is allowed.
        if (touchesTopInventory && !ItemUtil.isItemAllowed(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }
}