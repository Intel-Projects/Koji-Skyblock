package koji.skyblock.files;

import koji.skyblock.item.Rarity;
import koji.skyblock.player.Stats;

public class Config {
   public static boolean willSetToBaseStatsOnJoin() {
      return Files.getConfig().getBoolean("set-to-base-stats-on-join");
   }

   public static boolean sendToActionBar() {
      return Files.getConfig().getBoolean("modules.actionbar.send-ability-to-chat");
   }

   public static boolean areDropsVisibleToEveryone() {
      return Files.getConfig().getBoolean("drops-visible-to-everyone");
   }

   public static boolean areDropsVisibleToNonKiller() {
      return Files.getConfig().getBoolean("drops-visible-to-non-killer");
   }

   public static boolean willAutoPickup() {
      return Files.getConfig().getBoolean("auto-pickup");
   }

   public static boolean willLoseCoinsOnDeath() {
      return Files.getConfig().getBoolean("lose-coins-on-death.enabled");
   }

   public static double getMultipliedCoinAmount(double coins) {
      return coins * Files.getConfig().getDouble("lose-coins-on-death.amount");
   }

   public static boolean willUseFileStorage() {
      return Files.getConfig().getBoolean("player-data.file-storage");
   }

   public static double getBaseValue(Stats stat) {
      return Files.getConfig().getDouble("player-data.base-stats." + stat.getPlaceholderTag());
   }

   public static boolean getPetsVisibleDefault() {
      return Files.getConfig().getBoolean("pets-visible-default");
   }

   public static boolean getGetsItDefault() {
      return !Files.getConfig().getBoolean("auto-pickup-message-default");
   }

   public static boolean getItemDropAlertDefault() {
      return Files.getConfig().getBoolean("has-item-drop-alert-default");
   }

   public static boolean getReforgeEnabled() {
      return Files.getConfig().getBoolean("modules.reforging.enabled");
   }

   public static double getReforgeCost(Rarity rarity) {
      return Files.getConfig().getDouble("modules.reforging.cost." + rarity.name().toLowerCase());
   }

   public static boolean getPetsEnabled() {
      return Files.getConfig().getBoolean("modules.pets.enabled");
   }

   public static boolean willClosePetMenuOnChange() {
      return Files.getConfig().getBoolean("modules.pets.menu.close-on-summon");
   }

   public static boolean getEnchantsEnabled() {
      return Files.getConfig().getBoolean("modules.enchants.enabled");
   }

   public static boolean getItemBuilderEnabled() {
      return Files.getConfig().getBoolean("modules.itembuilder.enabled");
   }
}
