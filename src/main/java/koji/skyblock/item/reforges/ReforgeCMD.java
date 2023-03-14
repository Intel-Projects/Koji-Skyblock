package koji.skyblock.item.reforges;

import java.io.File;
import koji.developerkit.commands.KCommand;
import koji.skyblock.commands.Messages;
import koji.skyblock.files.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ReforgeCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (Config.getReforgeEnabled()) {
         if (sender instanceof Player) {
            Player p = (Player)sender;
            p.openInventory(ReforgingGUI.getMainInventory(p));
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
