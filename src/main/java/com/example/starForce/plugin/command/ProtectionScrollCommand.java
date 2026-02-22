package com.example.starForce.plugin.command;

import com.example.starForce.plugin.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProtectionScrollCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "이 명령어를 사용할 권한이 없습니다.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;
        int amount = 1;

        if (args.length > 1) {
            player.sendMessage(ChatColor.RED + "사용법: /파괴방지권 [개수]");
            return true;
        }

        if (args.length == 1) {
            try {
                amount = Integer.parseInt(args[0]);
                if (amount <= 0) {
                    player.sendMessage(ChatColor.RED + "개수는 1 이상이어야 합니다.");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "개수는 숫자로 입력해야 합니다.");
                return true;
            }
        }
        
        player.getInventory().addItem(ItemUtil.createProtectionScroll(amount));
        player.sendMessage(ChatColor.GREEN + "파괴 방지권을 " + amount + "개 받았습니다.");

        return true;
    }
}
