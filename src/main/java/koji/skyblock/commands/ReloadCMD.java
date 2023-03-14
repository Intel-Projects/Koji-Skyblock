package koji.skyblock.commands;

import koji.developerkit.commands.KCommand;
import koji.skyblock.Skyblock;
import koji.skyblock.player.PClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      sender.sendMessage(ChatColor.GOLD + "Reloading Koji's Skyblock...");
      Bukkit.getServer().getOnlinePlayers().forEach((p) -> {
         PClass.getPlayer(p).getPetInstance().delete();
      });
      if (Skyblock.load()) {
         Skyblock.getPlugin().updateCommands();
         sender.sendMessage(ChatColor.GREEN + "Reloaded Koji's Skyblock!");
         return true;
      } else {
         sender.sendMessage(ChatColor.RED + "Failed to reload Koji's Skyblock!");
         return false;
      }
   }
}
