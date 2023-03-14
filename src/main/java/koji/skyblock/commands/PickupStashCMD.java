package koji.skyblock.commands;

import koji.developerkit.commands.KCommand;
import koji.skyblock.files.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PickupStashCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player) {
         PlayerData.getPlayerData().grabFromItemStash((Player)sender);
         return true;
      } else {
         sender.sendMessage(Messages.NOT_PLAYER.getMessage());
         return false;
      }
   }
}
