package koji.skyblock.pets.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import koji.developerkit.commands.KCommand;
import koji.skyblock.commands.Messages;
import koji.skyblock.files.Config;
import koji.skyblock.files.Files;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PetSkinCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
      if (Config.getPetsEnabled()) {
         if (!(sender instanceof Player)) {
            return true;
         }

         Player p = (Player)sender;
         String msg = handleGivePetSkin(args, p);
         if (msg == null) {
            return true;
         }

         if (msg.equals("unknownCMD")) {
            p.sendMessage("§cUnknown Command");
            p.sendMessage("§cUsage: /petskin <id>");
            return false;
         }

         p.sendMessage(msg);
      } else {
         sender.sendMessage(YamlConfiguration.loadConfiguration(new File("spigot.yml")).getString("messages.unknown-command"));
      }

      return true;
   }

   public static String handleGivePetSkin(String[] args, Player p) {
      if (args.length != 1) {
         return "unknownCMD";
      } else if (p.getInventory().firstEmpty() == -1) {
         return Messages.FULL_INVENTORY.getMessage();
      } else {
         Iterator var2 = Files.getPetSkins().keySet().iterator();

         String type;
         do {
            if (!var2.hasNext()) {
               return ChatColor.RED + "Not a valid skin ID!";
            }

            type = (String)var2.next();
         } while(!((HashMap)Files.getPetSkins().get(type)).containsKey(args[0].toLowerCase()));

         p.getInventory().addItem(new ItemStack[]{(ItemStack)((HashMap)Files.getPetSkins().get(type)).get(args[0].toLowerCase())});
         return null;
      }
   }
}
