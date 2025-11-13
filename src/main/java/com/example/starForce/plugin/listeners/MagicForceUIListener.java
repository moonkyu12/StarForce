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

    private boolean isItemAllowed(ItemStack item) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() != null || !event.getView().getTitle().equals(INVENTORY_TITLE)) {
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        // Always cancel clicks on placeholders
        if (currentItem != null && currentItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            event.setCancelled(true);
            return;
        }

        int rawSlot = event.getRawSlot();
        int topInventorySize = event.getView().getTopInventory().getSize();

        // Handle actions from the player's inventory
        if (rawSlot >= topInventorySize) {
            // Cancel shift-click of a disallowed item
            if (event.isShiftClick() && !isItemAllowed(currentItem)) {
                event.setCancelled(true);
            }
            return; // Other clicks in player inventory are fine
        }

        // Handle actions within the top inventory
        // Cancel clicks on blocked slots
        if (rawSlot != ALLOWED_SLOT) {
            event.setCancelled(true);
            return;
        }

        // Click is on the allowed slot. Cancel if placing a disallowed item.
        if (cursorItem != null && cursorItem.getType() != Material.AIR && !isItemAllowed(cursorItem)) {
            event.setCancelled(true);
        }
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

        boolean touchesTopInventory = false;
        for (int slot : event.getRawSlots()) {
            if (slot < event.getView().getTopInventory().getSize()) {
                touchesTopInventory = true;
                // If it touches a blocked slot, cancel immediately
                if (slot != ALLOWED_SLOT) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // If the drag touches the top inventory (which must be only in the allowed slot)
        // check if the item type is allowed.
        if (touchesTopInventory && !isItemAllowed(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }
}