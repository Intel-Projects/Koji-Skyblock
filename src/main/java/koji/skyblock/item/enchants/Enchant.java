package koji.skyblock.item.enchants;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import koji.developerkit.listener.KListener;
import koji.developerkit.utils.duplet.Duplet;
import koji.skyblock.Skyblock;
import koji.skyblock.files.Files;
import koji.skyblock.item.ItemType;
import koji.skyblock.utils.StatMap;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

public abstract class Enchant extends KListener {
   private final HashMap sources = this.sources();
   private final ArrayList extraRequirements = new ArrayList();
   private static final ArrayList registeredEnchants = new ArrayList();
   private static final HashMap vanillaEquivalent = new HashMap();

   public Enchant() {
      if (this.getMaxLevel() > 5 && this.canAppearInEnchantTable()) {
         Skyblock.getPlugin().getLogger().log(Level.WARNING, this.getDisplayName() + " is invalid for enchant tables. Any enchants in the enchant table only appear up to level 5");
      }

   }

   public abstract boolean isUltimate();

   public abstract int getMaxLevel();

   public abstract String getName();

   public String getNameNoSpace() {
      return this.getDisplayName().replace(" ", "");
   }

   public abstract String getDisplayName();

   public abstract ArrayList getTargets();

   public void runEvent(Event e) {
      try {
         Method[] methods = this.getClass().getMethods();
         Method[] var3 = methods;
         int var4 = methods.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Method method = var3[var5];
            Annotation annotation = method.getDeclaredAnnotation(EventHandler.class);
            if (annotation != null && method.getParameterTypes().length > 0) {
               Class methodsClass = method.getParameterTypes()[0];
               if (methodsClass.equals(e.getClass())) {
                  method.setAccessible(true);
                  method.invoke(this, e);
               }
            }
         }

      } catch (Throwable var9) {
         throw var9;
      }
   }

   private HashMap sources() {
      return new HashMap();
   }

   public void addExtraRequirements(String extra) {
      this.extraRequirements.add(extra);
   }

   public int getHighestLevel() {
      AtomicInteger highestLevel = new AtomicInteger(this.getMaxLevel());
      this.getSources().forEach((a, b) -> {
         if ((Integer)b.getFirst() > highestLevel.get()) {
            highestLevel.set((Integer)b.getFirst());
         }

         if ((Integer)b.getSecond() > highestLevel.get()) {
            highestLevel.set((Integer)b.getSecond());
         }

      });
      return highestLevel.get();
   }

   public StatMap addStats(int level) {
      return new StatMap(new Duplet[0]);
   }

   public abstract double getVar(int var1);

   public List getExtraVars(int level) {
      return new ArrayList();
   }

   public boolean hasExtraVars(int index) {
      return getOrDefault(this.getExtraVars(1), index, (Object)null) != null;
   }

   public abstract boolean canAppearInEnchantTable();

   public abstract int getBaseExperienceCost();

   public abstract int getExperienceAddedPerLevel(int var1);

   public int getTotalExperience(int level) {
      int amount = this.getBaseExperienceCost();

      for(int i = 2; i <= level; ++i) {
         amount += this.getExperienceAddedPerLevel(i);
      }

      return amount;
   }

   public abstract int getBookshelfPowerRequirement();

   public abstract ArrayList getConflicts();

   public ArrayList targets(ItemType... targets) {
      ArrayList targetsList = new ArrayList();
      Collections.addAll(targetsList, targets);
      return targetsList;
   }

   public List getLore(int level) {
      SortedMap placeholders = new TreeMap();
      DecimalFormat formatter = (DecimalFormat)NumberFormat.getNumberInstance(Locale.US);
      formatter.applyPattern("###.##");
      formatter.setGroupingUsed(true);
      formatter.setGroupingSize(3);
      placeholders.put("%%", arrayList(new String[]{num(formatter.format(this.getVar(level)))}));

      for(int i = 1; i <= this.getExtraVars(level).size(); ++i) {
         int integer = (Integer)this.getExtraVars(level).get(i - 1);
         placeholders.put("%" + i + "%", arrayList(new String[]{num(formatter.format((long)integer))}));
      }

      ArrayList list = new ArrayList(Files.getConfig().getStringList("modules.enchants.enchantments." + this.getName().toLowerCase() + ".description"));
      return coloredList(replacePlaceholder(list, placeholders));
   }

   public static ArrayList replacePlaceholder(ArrayList original, SortedMap placeholder) {
      ArrayList lore = new ArrayList();
      Iterator var3 = original.iterator();

      while(true) {
         boolean more;
         String holder;
         do {
            if (!var3.hasNext()) {
               return lore;
            }

            String str = (String)var3.next();
            more = false;
            holder = "";
            List keySet = new ArrayList(placeholder.keySet());
            Collections.reverse(keySet);
            Iterator var8 = keySet.iterator();

            while(var8.hasNext()) {
               String place = (String)var8.next();
               if (str.contains(place)) {
                  str = str.replace(place, (CharSequence)((List)placeholder.get(place)).get(0));
                  if (((List)placeholder.get(place)).size() != 1) {
                     more = true;
                  }

                  holder = place;
               }
            }

            lore.add(str);
         } while(!more);

         for(int i = 1; i < ((List)placeholder.get(holder)).size(); ++i) {
            lore.add(((List)placeholder.get(holder)).get(i));
         }
      }
   }

   public static void registerEnchant(Enchant e) {
      registeredEnchants.add(e);
   }

   public static void registerEnchants(Enchant... e) {
      registeredEnchants.addAll(fromArray(e));
   }

   public HashMap getSources() {
      return this.sources;
   }

   public ArrayList getExtraRequirements() {
      return this.extraRequirements;
   }

   public static ArrayList getRegisteredEnchants() {
      return registeredEnchants;
   }

   public static HashMap getVanillaEquivalent() {
      return vanillaEquivalent;
   }
}
