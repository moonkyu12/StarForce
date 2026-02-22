package com.example.starForce.plugin;

import com.example.starForce.plugin.command.EnhanceCommand;
import com.example.starForce.plugin.command.MagicForceCommand;
import com.example.starForce.plugin.command.ProtectionScrollCommand;
import com.example.starForce.plugin.listeners.BowListener;
import com.example.starForce.plugin.listeners.MagicForceUIListener;
import com.example.starForce.plugin.listeners.TridentListener;
import com.example.starForce.plugin.listeners.SwapHandListener;
import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ShapedRecipe;

public final class StarForce extends JavaPlugin {

    private static StarForce instance;

    @Override
    public void onEnable() {
        instance = this;

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new SwapHandListener(), this);
        getServer().getPluginManager().registerEvents(new MagicForceUIListener(), this);
        getServer().getPluginManager().registerEvents(new TridentListener(), this);
        getServer().getPluginManager().registerEvents(new BowListener(), this); // New BowListener registration

        getCommand("magicforce").setExecutor(new MagicForceCommand());
        getCommand("강화").setExecutor(new EnhanceCommand());
        getCommand("파괴방지권").setExecutor(new ProtectionScrollCommand());

        // Register crafting recipe for Protection Scroll
        NamespacedKey recipeKey = new NamespacedKey(this, "protection_scroll_recipe");
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, ItemUtil.createProtectionScroll(1));
        recipe.shape(" N ", "NDN", " N "); // N=Netherite, D=Diamond
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('D', Material.DIAMOND);
        getServer().addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static StarForce getInstance() {
        return instance;
    }
}
