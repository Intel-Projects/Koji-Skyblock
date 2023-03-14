package koji.skyblock.player.scoreboard;

import java.util.ArrayList;
import java.util.List;
import koji.developerkit.KBase;
import org.bukkit.ChatColor;

public class Scoreboards extends KBase {
   public static String parseTime(long time) {
      long hours = time / 1000L + 6L;
      long minutes = time % 1000L * 60L / 1000L;
      String ampm = "am ";
      if (hours >= 12L) {
         hours -= 12L;
         ampm = "pm ";
      }

      if (hours >= 12L) {
         hours -= 12L;
         ampm = "am ";
      }

      if (hours >= 7L && ampm.equals("pm ")) {
         ampm = "pm " + ChatColor.BLUE + "☽";
      } else if (hours < 7L && ampm.equals("am ")) {
         ampm = "am " + ChatColor.BLUE + "☽";
      } else if (hours < 7L) {
         ampm = "pm " + ChatColor.YELLOW + "☀";
      } else {
         ampm = "am " + ChatColor.YELLOW + "☀";
      }

      if (hours == 0L) {
         hours = 12L;
      }

      String mm = "0" + minutes;
      mm = mm.substring(mm.length() - 2);
      if (!mm.endsWith("0")) {
         mm = mm.charAt(0) + "0";
      }

      return " " + hours + ":" + mm + ampm;
   }

   public static String[] parseText(String text) {
      String[] result = new String[2];
      if (text.length() <= 16) {
         result[0] = text;
         result[1] = "";
      } else {
         String prefix = text.substring(0, 16);
         StringBuilder suffix = new StringBuilder();
         List colors;
         int i;
         ChatColor color;
         if (!prefix.endsWith("§")) {
            colors = getLastColors(prefix);

            for(i = colors.size() - 1; i >= 0; --i) {
               color = (ChatColor)colors.get(i);
               suffix.append(color);
            }
         } else if (ChatColor.getByChar(text.charAt(16)).isColor()) {
            suffix.append('§');
            prefix = prefix.substring(0, 15);
         } else {
            prefix = prefix.substring(0, 15);
            colors = getLastColors(prefix);

            for(i = colors.size() - 1; i >= 0; --i) {
               color = (ChatColor)colors.get(i);
               suffix.append(color);
            }

            suffix.append('§');
         }

         suffix.append(text.substring(16));
         String suffixString = suffix.toString();
         suffixString = suffixString.substring(0, Math.min(suffixString.length(), 16));
         result[0] = prefix;
         result[1] = suffixString;
      }

      return result;
   }

   private static List getLastColors(String text) {
      List colors = new ArrayList();
      char previousChar = 0;

      for(int i = text.length() - 1; i >= 0; --i) {
         char c = text.charAt(i);
         if (c == 167 && previousChar != 0) {
            ChatColor color = ChatColor.getByChar(previousChar);
            if (color == ChatColor.RESET) {
               return colors;
            }

            colors.add(color);
            if (color.isColor()) {
               return colors;
            }
         } else {
            previousChar = c;
         }
      }

      return colors;
   }
}
