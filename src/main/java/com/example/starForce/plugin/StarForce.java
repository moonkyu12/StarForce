package com.example.starForce.plugin;

import com.example.starForce.plugin.listeners.SwapHandListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class StarForce extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new SwapHandListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
