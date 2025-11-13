package com.example.starForce.plugin.command;

import com.example.starForce.plugin.service.EnhancementService;
import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MagicForceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1 && args[0].equalsIgnoreCase("enhance")) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (!ItemUtil.isItemAllowed(itemInHand)) {
                player.sendMessage(ChatColor.RED + "You can only enhance allowed items (armor, tools, weapons).");
                return true;
            }

            ItemStack enhancedItem = EnhancementService.enhanceItem(itemInHand);
            player.getInventory().setItemInMainHand(enhancedItem);
            player.sendMessage(ChatColor.GREEN + "Item has been enhanced by 1 star!");

            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage: /magicforce enhance");
        return true;
    }
}
