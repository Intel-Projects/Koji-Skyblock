package koji.skyblock.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.KStatic;
import koji.developerkit.utils.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;

public enum Stats {
   DAMAGE("Damage", "❁", "§c", "%damage%", false, true, XMaterial.STONE_SWORD),
   STRENGTH("Strength", "❁", "§c", "%strength%", false, true, XMaterial.BLAZE_POWDER),
   CRIT_CHANCE("Crit Chance", "☣", "§9", "%crit_chance%", false, false, (new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0ZjQ5NTM1YTI3NmFhY2M0ZGM4NDEzM2JmZTgxYmU1ZjJhNDc5OWE0YzA0ZDlhNGRkYjcyZDgxOWVjMmIyYiJ9fX0=").build()),
   CRIT_DAMAGE("Crit Damage", "☠", "§9", "%crit_damage%", false, false, (new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGRhZmIyM2VmYzU3ZjI1MTg3OGU1MzI4ZDExY2IwZWVmODdiNzljODdiMjU0YTdlYzcyMjk2ZjkzNjNlZjdjIn19fQ==").build()),
   ATTACK_SPEED("Attack Speed", "⚔", "§e", "%attack_speed%", false, false, XMaterial.GOLDEN_AXE),
   MAX_HEALTH("Max Health", "❤", "§c", "%health%", true, true, XMaterial.GOLDEN_APPLE),
   DEFENSE("Defense", "❈", "§a", "%defense%", true, true, XMaterial.IRON_CHESTPLATE),
   SPEED("Speed", "✦", "§f", "%speed%", true, true, XMaterial.SUGAR),
   MAX_MANA("Max Mana", "✎", "§b", "%mana%", true, true, XMaterial.ENCHANTED_BOOK),
   FEROCITY("Ferocity", "", "§c", "%ferocity%", true, true, XMaterial.RED_DYE),
   HEALTH("Health", "❤", "§c"),
   MANA("Mana", "✎", "§b"),
   ABILITY_DAMAGE("Ability Damage", "๑", "§c", "%ability_damage%", true, true, XMaterial.BEACON),
   SEA_CREATURE_CHANCE("Sea Creature Chance", "α", "§3", "%sea_creature_chance%", true, true, XMaterial.PRISMARINE),
   MAGIC_FIND("Magic Find", "✯", "§b", "%magic_find%", true, true, XMaterial.STICK),
   PET_LUCK("Pet Luck", "♣", "§d", "%pet_luck%", true, true, (new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNjOGFhM2ZkZTI5NWZhOWY5YzI3ZjczNGJkYmFiMTFkMzNhMmU0M2U4NTVhY2NkNzQ2NTM1MjM3NzQxM2IifX19").build()),
   TRUE_DEFENSE("True Defense", "❂", "§f", "%true_defense%", true, true, XMaterial.BONE_MEAL),
   MINING_SPEED("Mining Speed", "⸕", "§6", "%mining_speed%", true, true, XMaterial.DIAMOND_PICKAXE),
   MINING_FORTUNE("Mining Fortune", "☘", "§6", "%mining_fortune%", true, true, (new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjczNTc5NTc1Y2E4OGIzYThhZmUxZWQxODkwN2IzMTI1ZmUwOTg3YjAyYTg4ZWYwZThhMDEwODdjM2QwMjRjNCJ9fX0=").build()),
   FORAGING_FORTUNE("Foraging Fortune", "☘", "§6", "%foraging_fortune%", true, true, (new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0NGUyYThkZmY5MGY1YjAwNWU3NmU2ZjVkYjdjMTJhZTU5Y2JiYzU2ZDhiYzgwNTBmM2UzZGJmMGMzYjczNCJ9fX0=").build()),
   FARMING_FORTUNE("Farming Fortune", "☘", "§6", "%farming_fortune%", true, true, (new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjIwZWU3NzQxZmYxYjk1OGRiYjlmYTdjZGRhZDljM2NjZTkzMzczZjQ3MGY5YjgzNGRhMDJkYTY3YzgyMDJhNCJ9fX0=").build()),
   PRISTINE("Pristine", "✧", "§5", "%pristine%", true, true, (new ItemBuilder(XMaterial.PLAYER_HEAD)).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDg4NmUwZjQxMTg1YjE4YTNhZmQ4OTQ4OGQyZWU0Y2FhMDczNTAwOTI0N2NjY2YwMzljZWQ2YWVkNzUyZmYxYSJ9fX0=").build()),
   HEALTH_REGEN("Health Regen", "❣", "§c", "%health_regen%", true, true, XMaterial.GLISTERING_MELON_SLICE);

   private final String name;
   private final String symbol;
   private final String color;
   private String placeholder;
   private final String defensive;
   private final String percentage;
   private final ItemStack item;

   public static List getNormalValues() {
      return (List)Arrays.stream(values()).filter((s) -> {
         return s != MANA && s != HEALTH;
      }).collect(Collectors.toList());
   }

   private Stats(String name, String symbol, String color) {
      this.placeholder = null;
      this.name = name;
      this.symbol = symbol;
      this.color = color;
      this.defensive = color;
      this.percentage = "";
      this.item = null;
   }

   private Stats(String name, String symbol, String color, String placeholder, boolean defensive, boolean normal, ItemStack item) {
      this.placeholder = null;
      this.name = name;
      this.symbol = symbol;
      this.color = color;
      this.placeholder = placeholder;
      this.defensive = defensive ? "§a" : "§c";
      this.percentage = normal ? "" : "%";
      this.item = item;
   }

   private Stats(String name, String symbol, String color, String placeholder, boolean defensive, boolean normal, XMaterial item) {
      this(name, symbol, color, placeholder, defensive, normal, item.parseItem());
   }

   public String getPetName() {
      return this.getNoSpaceName().substring(0, 1).toLowerCase() + this.getNoSpaceName().substring(1);
   }

   public String getStatName() {
      return this.defensive + this.name + this.percentage;
   }

   public String getMenuName() {
      return this.symbol + this.color + this.name + this.percentage;
   }

   public String getName() {
      return this.name;
   }

   public String getNoSpaceName() {
      return this.name.replace(" ", "");
   }

   public String getPlaceholder() {
      return this.placeholder;
   }

   public String getPlaceholderTag() {
      return this.placeholder.replaceAll("%", "");
   }

   public String getSymbol() {
      return this.symbol;
   }

   public String getColor() {
      return this.color;
   }

   public String baseStat() {
      return "base" + this.getNoSpaceName();
   }

   public String reforgeStat() {
      return "reforge" + this.getNoSpaceName();
   }

   public String statDisplayName() {
      switch(this) {
      case MAX_MANA:
         return "Intelligence";
      case MAX_HEALTH:
         return "Health";
      case ATTACK_SPEED:
         return "Bonus Attack Speed";
      default:
         return this.name;
      }
   }

   public String getNamePets() {
      return this.statDisplayName().toUpperCase().replace(" ", "_");
   }

   public String format(double amount) {
      String sign = KStatic.isNegative(amount) ? "" : "+";
      return sign + KStatic.num(amount) + this.getPercentage();
   }

   public static Stats get(String name) {
      Stats[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Stats stat = var1[var3];
         if (stat.getNamePets().toLowerCase().replace("_", "").equals(name.toLowerCase())) {
            return stat;
         }
      }

      return null;
   }

   public static Stats parseFromPlaceholder(String name) {
      Stats[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Stats stat = var1[var3];
         if (stat != HEALTH && stat != MANA && stat.getPlaceholderTag().equals(name.toLowerCase().replaceAll("%", ""))) {
            return stat;
         }
      }

      return null;
   }

   public static List getPlaceholders() {
      List returnList = new ArrayList();
      Stats[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Stats value = var1[var3];
         if (value != HEALTH && value != MANA) {
            returnList.add(value.getPlaceholderTag());
         }
      }

      return returnList;
   }

   public String getDefensive() {
      return this.defensive;
   }

   public String getPercentage() {
      return this.percentage;
   }

   public ItemStack getItem() {
      return this.item;
   }
}
