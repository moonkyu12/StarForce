package com.example.starForce.plugin.listeners;

import com.example.starForce.plugin.model.EnhancementProbabilities;
import com.example.starForce.plugin.model.EnhancementResponse;
import com.example.starForce.plugin.service.EnhancementService;
import com.example.starForce.plugin.ui.MagicForceUI;
import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MagicForceUIListener implements Listener {

    private static final String PROBABILITY_LORE_PREFIX = ChatColor.GRAY + "  ";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(MagicForceUI.INVENTORY_TITLE)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory topInventory = event.getView().getTopInventory();
        InventoryAction action = event.getAction();

        // Handle right-click for enhancement
        if (event.getRawSlot() == MagicForceUI.ITEM_SLOT && event.isRightClick()) {
            event.setCancelled(true);
            ItemStack itemToEnhance = topInventory.getItem(MagicForceUI.ITEM_SLOT);
            if (ItemUtil.isItemAllowed(itemToEnhance)) {
                EnhancementResponse response = EnhancementService.enhanceItem(itemToEnhance.clone());
                ItemStack enhancedItem = response.getItem();
                updateProbabilitiesLore(enhancedItem); // Update lore for the new level
                topInventory.setItem(MagicForceUI.ITEM_SLOT, enhancedItem);
            }
            return;
        }

        // More complex handling for placing/taking items
        if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.SWAP_WITH_CURSOR) {
            if (event.getRawSlot() == MagicForceUI.ITEM_SLOT) {
                // Item is being placed into the slot
                ItemStack cursorItem = event.getCursor();
                if (ItemUtil.isItemAllowed(cursorItem)) {
                    // Use a scheduler to update lore after the event resolves
                    org.bukkit.Bukkit.getScheduler().runTaskLater(org.bukkit.Bukkit.getPluginManager().getPlugin("StarForce"), () -> {
                        ItemStack itemInSlot = topInventory.getItem(MagicForceUI.ITEM_SLOT);
                        updateProbabilitiesLore(itemInSlot);
                        player.updateInventory();
                    }, 1L);
                }
            }
        } else if (action == InventoryAction.PICKUP_ALL || action == InventoryAction.PICKUP_ONE) {
            if (event.getRawSlot() == MagicForceUI.ITEM_SLOT) {
                // Item is being picked up
                ItemStack itemInSlot = event.getCurrentItem();
                removeProbabilitiesLore(itemInSlot);
            }
        } else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
             if (event.getRawSlot() != MagicForceUI.ITEM_SLOT && event.getClickedInventory().equals(player.getInventory())) {
                 // Item is being shift-clicked into the UI
                 ItemStack clickedItem = event.getCurrentItem();
                 if(ItemUtil.isItemAllowed(clickedItem)) {
                     org.bukkit.Bukkit.getScheduler().runTaskLater(org.bukkit.Bukkit.getPluginManager().getPlugin("StarForce"), () -> {
                        ItemStack itemInSlot = topInventory.getItem(MagicForceUI.ITEM_SLOT);
                        updateProbabilitiesLore(itemInSlot);
                        player.updateInventory();
                    }, 1L);
                 }
             }
        }

        // Allow default behavior for allowed actions, cancel others
        if (event.getRawSlot() == MagicForceUI.ITEM_SLOT) {
             event.setCancelled(!ItemUtil.isItemAllowed(event.getCursor()) && event.getCursor().getType() != Material.AIR);
        } else if (event.getClickedInventory() == topInventory) {
            event.setCancelled(true); // Click on placeholder
        }
    }

    private void updateProbabilitiesLore(ItemStack item) {
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.removeIf(line -> line.startsWith(PROBABILITY_LORE_PREFIX)); // Remove old probabilities

        int currentLevel = EnhancementService.getEnhancementLevel(item);
        if (currentLevel >= EnhancementService.MAX_LEVEL) {
            meta.setLore(lore);
            item.setItemMeta(meta);
            return;
        }

        EnhancementProbabilities probs = EnhancementService.getProbabilities(currentLevel);
        lore.add(PROBABILITY_LORE_PREFIX);
        lore.add(PROBABILITY_LORE_PREFIX + ChatColor.GREEN + "성공 확률: " + probs.getSuccess() + "%");
        if (probs.getFailure() > 0) lore.add(PROBABILITY_LORE_PREFIX + ChatColor.YELLOW + "실패(유지) 확률: " + probs.getFailure() + "%");
        if (probs.getDemotion() > 0) lore.add(PROBABILITY_LORE_PREFIX + ChatColor.RED + "실패(하락) 확률: " + probs.getDemotion() + "%");
        if (probs.getDestruction() > 0) lore.add(PROBABILITY_LORE_PREFIX + ChatColor.DARK_RED + "파괴 확률: " + probs.getDestruction() + "%");

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private void removeProbabilitiesLore(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return;

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.removeIf(line -> line.startsWith(PROBABILITY_LORE_PREFIX));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(MagicForceUI.INVENTORY_TITLE)) {
            return;
        }

        Inventory inventory = event.getInventory();
        ItemStack item = inventory.getItem(MagicForceUI.ITEM_SLOT);

        if (item != null) {
            removeProbabilitiesLore(item); // Clean up lore before returning
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
        // Disallow dragging into the top inventory for simplicity
        for (int slot : event.getRawSlots()) {
            if (slot < event.getView().getTopInventory().getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}