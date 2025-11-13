package com.example.starForce.plugin.listeners;

import com.example.starForce.plugin.ui.MagicForceUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class SwapHandListener implements Listener {

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            event.setCancelled(true);
            MagicForceUI.openMagicForceUI(player);
        }
    }
}
