package koji.skyblock.commands;

import koji.developerkit.commands.KCommand;
import koji.skyblock.files.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearStashCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(Messages.NOT_PLAYER.getMessage());
         return false;
      } else {
         PlayerData.getPlayerData().clearStash((Player)sender, args.length > 0 && args[0].equalsIgnoreCase("confirm"));
         return true;
      }
   }
}
