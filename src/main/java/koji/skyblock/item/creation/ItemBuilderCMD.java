package koji.skyblock.item.creation;

import java.io.File;
import koji.developerkit.commands.KCommand;
import koji.skyblock.commands.Messages;
import koji.skyblock.files.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemBuilderCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (Config.getItemBuilderEnabled()) {
         if (sender instanceof Player) {
            Player p = (Player)sender;
            p.openInventory(ItemBuilderGUI.getMainMenu(p, (ItemStack)null, (ItemBuilderGUI.ItemBuilderMenu)null).getInventory());
            return true;
         } else {
            sender.sendMessage(Messages.NOT_PLAYER.getMessage());
            return false;
         }
      } else {
         sender.sendMessage(YamlConfiguration.loadConfiguration(new File("spigot.yml")).getString("messages.unknown-command"));
         return true;
      }
   }
}
