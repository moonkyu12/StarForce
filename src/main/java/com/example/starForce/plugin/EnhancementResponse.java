package com.example.starForce.plugin;

import org.bukkit.inventory.ItemStack;

public class EnhancementResponse {
    private final ItemStack item;
    private final EnhancementResult result;

    public EnhancementResponse(ItemStack item, EnhancementResult result) {
        this.item = item;
        this.result = result;
    }

    public ItemStack getItem() {
        return item;
    }

    public EnhancementResult getResult() {
        return result;
    }
}