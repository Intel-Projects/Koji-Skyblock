package koji.skyblock.commands;

import koji.developerkit.utils.KStatic;
import koji.skyblock.files.Files;

public enum Messages {
   FULL_INVENTORY("messages.inventory_full"),
   NOT_PLAYER("messages.no_player"),
   SCOREBOARD_TITLE("modules.scoreboard.title"),
   NOT_A_PET("messages.not_a_pet"),
   UNKNOWN_RARITY("messages.unknown_rarity"),
   UNKNOWN_PET_NAME("messages.unknown_pet_name"),
   COMMAND_CI_USAGE("messages.commands.ci.usage"),
   NOT_VALID_RARITY("messages.not_valid_rarity");

   private String path;

   private Messages(String path) {
      this.path = path;
   }

   public String getMessage() {
      return KStatic.color(Files.getConfig().getString(this.path));
   }
}
