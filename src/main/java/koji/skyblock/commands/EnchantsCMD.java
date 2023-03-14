package koji.skyblock.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import koji.developerkit.commands.KCommand;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.files.Config;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.anvil.EnchantedBook;
import koji.skyblock.item.enchants.Enchant;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class EnchantsCMD extends KCommand implements TabExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (Config.getEnchantsEnabled()) {
         if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.NOT_PLAYER.getMessage());
            return false;
         } else {
            Player p = (Player)sender;
            if ((args.length != 3 || !args[0].equalsIgnoreCase("set")) && (!isInRange((double)args.length, 1.0D, 2.0D) || !args[0].equalsIgnoreCase("get")) && (args.length != 2 || !args[0].equalsIgnoreCase("remove"))) {
               sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Usages: \n" + ChatColor.RED + "/enchants <set> <enchant> <level>\n" + ChatColor.RED + "/enchants <get>\n" + ChatColor.RED + "/enchants <get> <enchant>");
               return false;
            } else {
               CustomItem ci = new CustomItem(p.getItemInHand());
               if (!isValidItem(ci.build()) || !ci.doesAllowEnchants() && ci.build().getType() != XMaterial.ENCHANTED_BOOK.parseMaterial() && ci.build().getType() != XMaterial.BOOK.parseMaterial()) {
                  sender.sendMessage(ChatColor.RED + "This item doesn't allow enchants.");
                  return false;
               } else {
                  Enchant enchant;
                  if (args[0].equalsIgnoreCase("get")) {
                     if (isValidItem(ci.build()) && ci.getEnchants().size() != 0) {
                        if (args.length > 1) {
                           enchant = CustomItem.matchingEnchant(args[1], true);
                           if (enchant != null) {
                              if (ci.hasEnchant(enchant)) {
                                 int level = ci.getEnchantLevel(enchant);
                                 sender.sendMessage(ChatColor.GREEN + "Item in hand has " + enchant.getDisplayName() + " at level " + level);
                              } else {
                                 sender.sendMessage(ChatColor.RED + "Item does not have " + enchant.getDisplayName());
                              }

                              return true;
                           }
                        }

                        sender.sendMessage(ChatColor.GREEN + ci.getString("Enchants").replaceAll(",", ", "));
                        return true;
                     } else {
                        sender.sendMessage(ChatColor.RED + "This item doesn't have any enchants.");
                        return false;
                     }
                  } else if (args[0].equalsIgnoreCase("remove")) {
                     enchant = CustomItem.matchingEnchant(args[1], true);
                     if (enchant != null) {
                        p.setItemInHand(ci.removeEnchant(p, enchant).buildWithAbilities());
                        return true;
                     } else {
                        sender.sendMessage(ChatColor.RED + args[1] + " is not a valid enchant.");
                        return false;
                     }
                  } else {
                     int level;
                     try {
                        level = Integer.parseInt(args[2]);
                        if (level <= 0) {
                           level = Integer.parseInt("error time lmao");
                        }
                     } catch (NumberFormatException var10) {
                        sender.sendMessage(ChatColor.RED + "Level must be a positive whole number.");
                        return false;
                     }

                     Enchant enchant = CustomItem.matchingEnchant(args[1], true);
                     if (enchant != null) {
                        if (ci.hasConflictingEnchant(enchant)) {
                           Enchant conflicting = ci.getConflictingEnchant(enchant);
                           sender.sendMessage(ChatColor.GREEN + conflicting.getDisplayName() + " was removed due to conflict.");
                        }

                        if (!ci.build().getType().equals(XMaterial.ENCHANTED_BOOK.parseMaterial()) && !ci.build().getType().equals(XMaterial.BOOK.parseMaterial())) {
                           ci.addEnchant(p, enchant, level, true);
                           p.setItemInHand(ci.buildWithAbilities());
                        } else {
                           EnchantedBook book = new EnchantedBook(XMaterial.ENCHANTED_BOOK);
                           book.addEnchants(p, ci.getEnchants());
                           book.addEnchant(p, enchant, level, true);
                           p.setItemInHand(book.build());
                        }

                        return true;
                     } else {
                        sender.sendMessage(ChatColor.RED + args[1] + " is not a valid enchant.");
                        return false;
                     }
                  }
               }
            }
         }
      } else {
         sender.sendMessage(YamlConfiguration.loadConfiguration(new File("spigot.yml")).getString("messages.unknown-command"));
         return true;
      }
   }

   public List onTabComplete(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 1) {
         return this.partial(args[0], arrayList(new String[]{"get", "set"}));
      } else if (args.length == 2) {
         List list = new ArrayList();
         Enchant.getRegisteredEnchants().forEach((ench) -> {
            list.add(ench.getNameNoSpace());
         });
         return this.partial(args[1], list);
      } else {
         return null;
      }
   }

   public List partial(String token, List collection) {
      List list = new ArrayList();
      StringUtil.copyPartialMatches(token, collection, list);
      Collections.sort(list);
      return list;
   }
}
