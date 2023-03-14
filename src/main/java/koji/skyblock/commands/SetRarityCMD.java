package koji.skyblock.commands;

import koji.developerkit.commands.KCommand;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.Rarity;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRarityCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player) {
         Player p = (Player)sender;
         if (isValidItem(p.getItemInHand())) {
            if (args.length == 1) {
               Rarity rar = Rarity.getRarity(args[0]);
               if (rar != null) {
                  CustomItem item = new CustomItem(p.getItemInHand());
                  String oldName = item.getName();
                  if (oldName.charAt(0) == 167) {
                     oldName = oldName.substring(2);
                  }

                  item.setRarity(rar).setName(rar.getColor() + oldName);
                  p.setItemInHand(item.buildWithAbilities());
                  p.sendMessage(ChatColor.GREEN + "Set the rarity of the item to " + rar.getDisplay() + "!");
                  return true;
               }

               p.sendMessage(ChatColor.RED + args[0] + " is not a valid rarity!");
            } else {
               sender.sendMessage(ChatColor.RED + "Usage:" + ChatColor.RED + "\n/setrarity <rarity>");
            }
         }
      } else {
         sender.sendMessage(Messages.NOT_PLAYER.getMessage());
      }

      return false;
   }
}
