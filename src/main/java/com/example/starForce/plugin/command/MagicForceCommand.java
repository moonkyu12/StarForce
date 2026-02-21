package com.example.starForce.plugin.command;

import com.example.starForce.plugin.EnhancementResponse;
import com.example.starForce.plugin.EnhancementResult;
import com.example.starForce.plugin.service.EnhancementService;
import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

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

            // Check for diamond cost
            if (!player.getInventory().contains(Material.DIAMOND, 1)) {
                player.sendMessage(ChatColor.RED + "강화에는 다이아몬드 1개가 필요합니다!");
                return true;
            }
            player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));

            EnhancementResponse response = EnhancementService.enhanceItem(itemInHand);
            ItemStack enhancedItem = response.getItem();
            EnhancementResult result = response.getResult();

            player.getInventory().setItemInMainHand(enhancedItem);

            switch (result) {
                case SUCCESS:
                    player.sendMessage(ChatColor.GREEN + "강화에 성공했습니다!");
                    break;
                case FAILURE:
                    player.sendMessage(ChatColor.YELLOW + "강화에 실패했습니다.");
                    break;
                case DEMOTION:
                    player.sendMessage(ChatColor.RED + "강화에 실패하여 단계가 하락했습니다.");
                    break;
                case DESTRUCTION:
                    player.sendMessage(ChatColor.DARK_RED + "아이템이 파괴되었습니다!");
                    break;
            }

            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage: /magicforce enhance");
        return true;
    }
}
