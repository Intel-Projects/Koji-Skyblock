package koji.skyblock.pets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import koji.developerkit.KBase;
import koji.developerkit.utils.ItemBuilder;
import koji.developerkit.utils.duplet.Duplet;
import koji.developerkit.utils.xseries.XMaterial;
import koji.skyblock.files.Files;
import koji.skyblock.item.Rarity;
import koji.skyblock.pets.api.PetAbility;
import koji.skyblock.pets.api.PetKind;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public abstract class Pet extends KBase {
   public abstract String getName();

   public abstract PetKind getPetKind();

   public abstract ArrayList rarities();

   public abstract HashMap baseStats();

   public abstract HashMap addStatPerLevel();

   public ArrayList getAbilities() {
      return arrayList(new PetAbility[]{this.getFirstAbility(), this.getSecondAbility(), this.getThirdAbility()});
   }

   public abstract PetAbility getFirstAbility();

   public abstract PetAbility getSecondAbility();

   public abstract PetAbility getThirdAbility();

   public abstract String getTexture();

   public String getNameSpace() {
      return space(this.getName());
   }

   public ArrayList getValidAbilities(Rarity rarity) {
      ArrayList abilities = new ArrayList();
      Iterator var3 = this.getAbilities().iterator();

      while(var3.hasNext()) {
         PetAbility ability = (PetAbility)var3.next();
         if (ability.validRarities().contains(rarity)) {
            abilities.add(ability);
         }
      }

      return abilities;
   }

   public List getLoreWithPlaceholders(Rarity rarity, boolean menu, boolean equipped) {
      ArrayList lore = arrayList(new String[]{"&8" + this.getPetKind().getName() + " Pet", " "});
      StatMap base = (StatMap)this.baseStats().getOrDefault(rarity, new StatMap(new Duplet[0]));
      StatMap perLevel = (StatMap)this.addStatPerLevel().getOrDefault(rarity, new StatMap(new Duplet[0]));
      if (!base.isEmpty() || !perLevel.isEmpty()) {
         Stats[] var7 = Stats.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Stats stat = var7[var9];
            if (base.get(stat) != 0.0D || perLevel.get(stat) != 0.0D) {
               lore.add(getStatLine(stat));
            }
         }

         lore.add(" ");
      }

      Iterator var11 = this.getValidAbilities(rarity).iterator();

      while(var11.hasNext()) {
         PetAbility abilities = (PetAbility)var11.next();
         lore.add("&6" + abilities.getDisplayName());
         lore.addAll(abilities.getLore());
         lore.add(" ");
      }

      lore.addAll(arrayList(new String[]{"%progress%", " ", menu ? (equipped ? ChatColor.RED + "Click to despawn!" : ChatColor.YELLOW + "Click to spawn!") : rarity.getBoldedColor() + rarity.getName()}));
      return coloredList(lore);
   }

   public static String getStatLine(Stats stat) {
      return color("&7" + stat.statDisplayName() + ": &a" + stat.getDefensive() + stat.getPlaceholder() + stat.getPercentage());
   }

   public List getLore(Rarity rarity, int level, double currentXp, boolean menu, boolean equipped) {
      HashMap placeholders = new HashMap();
      double xpRequirements = Levelable.getRequirementsForLevel(rarity, level);
      StringBuilder progress = new StringBuilder("§a");

      for(double d = 0.05D; d <= 1.0D; d += 0.05D) {
         if (currentXp / xpRequirements >= d) {
            progress.append("§m-");
         } else {
            progress.append("§f§m-");
         }
      }

      String currentXpFormatted = formatNumberSuffixes((long)currentXp);
      String xpRequirementsFormatted = formatNumberSuffixes((long)xpRequirements);
      progress.append("§r §e").append(currentXpFormatted).append("§6/§e").append(xpRequirementsFormatted);
      ArrayList progressLore;
      if (level < 100) {
         progressLore = arrayList(new String[]{"§7Progress to Level " + (level + 1) + ": §e" + num(Math.floor(currentXp / xpRequirements * 100.0D)) + "%", progress.toString()});
      } else {
         progressLore = arrayList(new String[]{"§bMAX LEVEL"});
      }

      placeholders.put("%progress%", progressLore);
      StatMap base = (StatMap)this.baseStats().getOrDefault(rarity, new StatMap(new Duplet[0]));
      StatMap perLevel = (StatMap)this.addStatPerLevel().getOrDefault(rarity, new StatMap(new Duplet[0]));
      Stats[] var16 = Stats.values();
      int var17 = var16.length;

      for(int var18 = 0; var18 < var17; ++var18) {
         Stats stat = var16[var18];
         if (stat.getPlaceholder() != null) {
            placeholders.put(stat.getPlaceholder(), arrayList(new String[]{num(base.get(stat) + perLevel.get(stat) * (double)level)}));
         }
      }

      Iterator var27 = this.getValidAbilities(rarity).iterator();

      while(true) {
         PetAbility ability;
         HashMap variablePlaceholders;
         do {
            if (!var27.hasNext()) {
               return replacePlaceholder(this.getLoreWithPlaceholders(rarity, menu, equipped), placeholders);
            }

            ability = (PetAbility)var27.next();
            variablePlaceholders = ability.getPlaceHolderSlotsBaseValue();
         } while(((HashMap)variablePlaceholders.getOrDefault(rarity, new HashMap())).isEmpty());

         Iterator var30 = ((HashMap)variablePlaceholders.get(rarity)).keySet().iterator();

         while(var30.hasNext()) {
            String key = (String)var30.next();
            Double[] value = (Double[])((HashMap)variablePlaceholders.get(rarity)).get(key);
            double value1 = value[0] == null ? 0.0D : value[0];
            double value2 = value[1] == null ? 0.0D : value[1];
            placeholders.put("%" + ability.getName() + capitalize(key) + "%", arrayList(new String[]{num(value1 + value2 * (double)level)}));
         }
      }
   }

   public ItemStack getItem(Rarity rarity, int level, double currentXp, String skin) {
      return this.getItem(rarity, level, currentXp, skin, false, false);
   }

   public ItemStack getItem(Rarity rarity, int level, double currentXp, String skin, boolean menu, boolean equipped) {
      boolean hasSkin = skin != null && !skin.isEmpty();
      String skinString = hasSkin ? " ✦" : "";
      ItemBuilder ib = (new ItemBuilder(XMaterial.PLAYER_HEAD)).setLore(this.getLore(rarity, level, currentXp, menu, equipped)).setName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + level + ChatColor.DARK_GRAY + "] " + rarity.getColor() + this.getNameSpace() + skinString).setString("petType", this.getName()).setString("petRarity", rarity.getName()).setInt("petLevel", level).setDouble("petCurrentXP", currentXp).setTexture(hasSkin ? skin : this.getTexture());
      if (skin != null) {
         ib.setString("petSkin", skin);
      }

      return ib.build();
   }

   public static Pet matchFromType(String type) {
      return matchFromType(type, false);
   }

   public static Pet matchFromType(String type, boolean ignoreEquals) {
      Iterator var2 = Files.getRegisteredPets().values().iterator();

      Pet pets;
      while(true) {
         if (!var2.hasNext()) {
            return null;
         }

         pets = (Pet)var2.next();
         if (ignoreEquals) {
            if (pets.getName().equalsIgnoreCase(type)) {
               break;
            }
         } else if (pets.getName().equals(type)) {
            break;
         }
      }

      return pets;
   }

   public static void registerPet(Pet pet) {
      Files.getRegisteredPets().put(pet.getName(), pet);
   }
}
