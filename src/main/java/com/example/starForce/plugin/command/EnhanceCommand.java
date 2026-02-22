package com.example.starForce.plugin.command;

import com.example.starForce.plugin.service.EnhancementService;
import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnhanceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "이 명령어를 사용할 권한이 없습니다.");
            return true;
        }

        if (args.length != 2 || !args[0].equalsIgnoreCase("강화조정")) {
            player.sendMessage(ChatColor.RED + "사용법: /강화 강화조정 <등급>");
            return true;
        }

        try {
            int level = Integer.parseInt(args[1]);
            if (level < 0 || level > EnhancementService.MAX_LEVEL) {
                player.sendMessage(ChatColor.RED + "등급은 0에서 " + EnhancementService.MAX_LEVEL + " 사이여야 합니다.");
                return true;
            }

            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (!ItemUtil.isItemAllowed(itemInHand)) {
                player.sendMessage(ChatColor.RED + "강화할 수 없는 아이템입니다.");
                return true;
            }

            // This service method will be created in the next step.
            ItemStack adjustedItem = EnhancementService.setEnhancementLevel(itemInHand, level);
            
            player.getInventory().setItemInMainHand(adjustedItem);
            player.sendMessage(ChatColor.GREEN + "아이템의 강화 등급을 " + level + "로 설정했습니다.");

        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "등급은 숫자로 입력해야 합니다.");
        }

        return true;
    }
}
