package com.example.starForce.plugin.listeners;

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

    private final String INVENTORY_TITLE = "스타★포스 인벤토리";
    private final int ALLOWED_SLOT = 13;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() != null || !event.getView().getTitle().equals(INVENTORY_TITLE)) {
            return;
        }

        // Prevent taking the placeholder items by checking the material
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            event.setCancelled(true);
        }

        int rawSlot = event.getRawSlot();
        int topInventorySize = event.getView().getTopInventory().getSize();

        // If the click is in the top inventory, but not in the allowed slot, cancel it.
        if (rawSlot < topInventorySize && rawSlot != ALLOWED_SLOT) {
            event.setCancelled(true);
        }

        // The user wants shift-click to work. The default behavior will try to
        // move the item to the first empty slot, which is our ALLOWED_SLOT.
        // So, we just don't cancel the event. If the allowed slot is full,
        // the default behavior will do nothing, which is the desired outcome.
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_TITLE)) {
            return;
        }

        Inventory inventory = event.getInventory();
        ItemStack item = inventory.getItem(ALLOWED_SLOT);

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
        if (!event.getView().getTitle().equals(INVENTORY_TITLE)) {
            return;
        }

        // Check the slots the player is dragging over
        for (int slot : event.getRawSlots()) {
            // If any slot is in the top inventory AND is not the allowed slot, cancel the drag
            if (slot < event.getView().getTopInventory().getSize() && slot != ALLOWED_SLOT) {
                event.setCancelled(true);
                return;
            }
        }
    }
}