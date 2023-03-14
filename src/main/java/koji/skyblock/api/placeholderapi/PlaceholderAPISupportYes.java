package koji.skyblock.api.placeholderapi;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlaceholderAPISupportYes implements PlaceholderAPISupport {
   public String chat(Player p, String msg) {
      if (msg == null) {
         return ChatColor.translateAlternateColorCodes('&', "&cConfig Missing Text");
      } else {
         msg = PlaceholderAPI.setPlaceholders(p, msg);
         if (!Pattern.compile("\\{#[0-9A-Fa-f]{6}}").matcher(msg).find()) {
            return ChatColor.translateAlternateColorCodes('&', msg);
         } else {
            String s;
            String sNew;
            for(Matcher m = Pattern.compile("\\{#[0-9A-Fa-f]{6}}").matcher(msg); m.find(); msg = msg.replace(s, sNew.replace("§{", "").replace("§}", ""))) {
               s = m.group();
               sNew = "§x" + ((String)Arrays.stream(s.split("")).map((s2) -> {
                  return "§" + s2;
               }).collect(Collectors.joining())).replace("§#", "");
            }

            return ChatColor.translateAlternateColorCodes('&', msg);
         }
      }
   }
}
