package koji.skyblock.pets.commands;

import java.io.File;
import koji.developerkit.commands.KCommand;
import koji.skyblock.commands.Messages;
import koji.skyblock.files.Config;
import koji.skyblock.item.Rarity;
import koji.skyblock.pets.Pet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PetGiveCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
      if (Config.getPetsEnabled()) {
         if (!(sender instanceof Player)) {
            return true;
         }

         Player p = (Player)sender;
         String msg = handleGivePet(args, p);
         if (msg == null) {
            return true;
         }

         if (msg.equals("unknownCMD")) {
            p.sendMessage("§cUnknown Command");
            p.sendMessage("§cUsage: /petgive <Pet Type (EnderDragon, Pig, etc.)> <Rarity (Legendary, Epic, Rare, etc.)>");
            return false;
         }

         p.sendMessage(msg);
      } else {
         sender.sendMessage(YamlConfiguration.loadConfiguration(new File("spigot.yml")).getString("messages.unknown-command"));
      }

      return true;
   }

   public static String handleGivePet(String[] args, Player p) {
      if (args.length != 2) {
         return "unknownCMD";
      } else {
         Pet pet = Pet.matchFromType(args[0], true);
         if (pet == null) {
            return Messages.UNKNOWN_PET_NAME.getMessage() + args[0];
         } else {
            Rarity r;
            try {
               r = Rarity.valueOf(args[1].toUpperCase());
            } catch (Exception var5) {
               return Messages.UNKNOWN_RARITY.getMessage() + args[1];
            }

            if (p.getInventory().firstEmpty() == -1) {
               return Messages.FULL_INVENTORY.getMessage();
            } else if (!pet.rarities().contains(r)) {
               return Messages.NOT_VALID_RARITY.getMessage() + r.getDisplay();
            } else {
               p.getInventory().addItem(new ItemStack[]{pet.getItem(r, 1, 0.0D, (String)null)});
               return null;
            }
         }
      }
   }
}
