package koji.skyblock.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import koji.developerkit.commands.KCommand;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.item.CustomItem;
import koji.skyblock.player.Stats;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class AddStatsCMD extends KCommand implements TabExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player) {
         Player p = (Player)sender;
         if (p.getItemInHand() != null && p.getItemInHand().getType() != XMaterial.AIR.parseMaterial()) {
            if (args.length == 2 && Stats.parseFromPlaceholder(args[0]) != null) {
               double value;
               try {
                  value = Double.parseDouble(args[1]);
               } catch (NumberFormatException var9) {
                  sender.sendMessage(ChatColor.RED + "Stat must be a number!");
                  return false;
               }

               CustomItem item = new CustomItem(p.getItemInHand());
               item.addStat(Stats.parseFromPlaceholder(args[0]), value, false);
               p.setItemInHand(item.buildWithAbilities());
               if (isNegative(value)) {
                  sender.sendMessage(ChatColor.GREEN + "Removed " + num(-value) + " from your item.");
               } else {
                  sender.sendMessage(ChatColor.GREEN + "Added " + num(value) + " to your item.");
               }

               return true;
            }

            sender.sendMessage(ChatColor.RED + "Usage: /addstat <stat name> <amount>");
         } else {
            sender.sendMessage(ChatColor.RED + "Must be holding an item!");
         }
      } else {
         sender.sendMessage(Messages.NOT_PLAYER.getMessage());
      }

      return false;
   }

   public List onTabComplete(CommandSender sender, Command command, String label, String[] args) {
      return args.length == 1 ? this.partial(args[0], Stats.getPlaceholders()) : null;
   }

   public List partial(String token, List collection) {
      List list = new ArrayList();
      StringUtil.copyPartialMatches(token, collection, list);
      Collections.sort(list);
      return list;
   }
}
