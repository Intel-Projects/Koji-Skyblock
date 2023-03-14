package koji.skyblock.pets.commands;

import java.io.File;
import koji.developerkit.commands.KCommand;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.commands.Messages;
import koji.skyblock.files.Config;
import koji.skyblock.item.Rarity;
import koji.skyblock.pets.Levelable;
import koji.skyblock.pets.Pet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PetSetLevelCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
      if (Config.getPetsEnabled()) {
         if (!(sender instanceof Player)) {
            return true;
         }

         Player p = (Player)sender;
         String msg = handleSetLevel(args, p);
         if (msg == null) {
            return true;
         }

         if (msg.equals("unknownCMD")) {
            p.sendMessage("§cUnknown Command");
            p.sendMessage("§cUsage: /petsetlevel <number>");
            return false;
         }

         p.sendMessage(msg);
      } else {
         sender.sendMessage(YamlConfiguration.loadConfiguration(new File("spigot.yml")).getString("messages.unknown-command"));
      }

      return true;
   }

   public static String handleSetLevel(String[] args, Player p) {
      if (args.length != 1) {
         return "unknownCMD";
      } else {
         ItemStack item = p.getItemInHand();
         if (item != null && !item.getType().equals(XMaterial.AIR.parseMaterial())) {
            int i;
            try {
               i = Integer.parseInt(args[0]);
            } catch (Exception var10) {
               return "§cThat's not a number: " + args[0];
            }

            if (i > 100) {
               return "§cLevel too high (max 100)! " + i;
            } else {
               ItemBuilder nbt = new ItemBuilder(item);
               Pet pet = Pet.matchFromType(nbt.getString("petType"));
               Rarity rarity = Rarity.valueOf(nbt.getString("petRarity"));
               String skin = null;
               if (nbt.hasKey("petSkin")) {
                  skin = nbt.getString("petSkin");
               }

               if (pet == null) {
                  return Messages.NOT_A_PET.getMessage();
               } else {
                  double exp = 0.0D;
                  if (i > 1) {
                     exp = Levelable.getRequirementsForLevel(rarity, i - 1);
                  }

                  p.setItemInHand(pet.getItem(rarity, i, exp, skin));
                  return null;
               }
            }
         } else {
            return "§cPlease hold an item in your hand!";
         }
      }
   }
}
