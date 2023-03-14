package koji.skyblock.commands;

import java.util.UUID;
import koji.developerkit.commands.KCommand;
import koji.skyblock.files.data.PlayerData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetDataCMD extends KCommand {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(Messages.NOT_PLAYER.getMessage());
      } else {
         boolean one = args.length == 1 && args[0].equalsIgnoreCase("confirm");
         Player p;
         if (one || args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
            p = this.parsePlayer(sender, one, one ? args[0] : args[1]);
            if (p == null) {
               sender.sendMessage(ChatColor.RED + "Player is not valid name or UUID!");
               return false;
            }

            PlayerData.getPlayerData().createPlayer(p);
            sender.sendMessage(ChatColor.GREEN + "Reset " + (one ? "your" : p.getDisplayName() + "'s") + " player data!");
            return true;
         }

         if (args.length == 0 || args.length == 1 && Bukkit.getPlayer(args[0]) != null) {
            one = args.length == 0;
            p = this.parsePlayer(sender, one, one ? "" : args[0]);
            if (p == null) {
               sender.sendMessage(ChatColor.RED + "Player is not valid name or UUID!");
               return false;
            } else {
               TextComponent textComponent = new TextComponent("Are you sure you want to reset " + (one ? "your" : p.getDisplayName() + "'s") + " data?");
               textComponent.setColor(net.md_5.bungee.api.ChatColor.RED);
               TextComponent extra = new TextComponent("\nRESET YES I AM SURE");
               extra.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
               extra.setBold(true);
               extra.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent[]{new TextComponent(ChatColor.YELLOW + "Click here to reset the data.")}));
               String added = one ? "confirm" : p.getUniqueId() + " confirm";
               extra.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/kojiskyblock:resetdata " + added));
               textComponent.addExtra(extra);
               p.spigot().sendMessage(extra);
               return true;
            }
         }

         sender.sendMessage(ChatColor.RED + "Usages: \n" + ChatColor.RED + "/resetdata\n" + ChatColor.RED + "/resetdata <Username>\n" + ChatColor.RED + "/resetdata <UUID>");
      }

      return false;
   }

   private Player parsePlayer(CommandSender sender, boolean one, String user) {
      Player p = one ? (Player)sender : null;
      if (!one) {
         try {
            UUID uuid = UUID.fromString(user);
            p = (Player)Bukkit.getOfflinePlayer(uuid);
         } catch (IllegalArgumentException var6) {
            p = (Player)Bukkit.getOfflinePlayer(user);
         }
      }

      return p;
   }
}
