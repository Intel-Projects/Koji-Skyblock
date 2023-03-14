package koji.skyblock.item.ability;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import koji.developerkit.listener.KListener;
import koji.skyblock.item.CustomItem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public abstract class Ability extends KListener {
   FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(this.getFile());
   private final Hashtable cache = new Hashtable();

   public abstract String getIdentifier();

   public abstract String getDisplayName();

   public abstract List getLoreDefault();

   public double getActualManaCost(Player p) {
      return p == null ? 0.0D : this.getManaCost(p);
   }

   public abstract double getManaCost(Player var1);

   public BiPredicate getExtraConditions() {
      return null;
   }

   public abstract File getFile();

   public void reload() throws IOException, InvalidConfigurationException {
      this.fileConfiguration.load(this.getFile());
   }

   public List getLore(CustomItem item) {
      if (!item.hasAbility(this.getIdentifier())) {
         return coloredList(replacePlaceholders(this.getLoreDefault(), this.getPlaceholderDefaults()));
      } else if (this.getLoreDefault() == null) {
         return new ArrayList();
      } else {
         HashMap placeholder = new HashMap();
         item.getPlaceholders(this.getIdentifier()).forEach((a, b) -> {
            List var10000 = (List)placeholder.put("%" + a + "%", arrayList(new String[]{b}));
         });
         return coloredList(replacePlaceholder(this.getLoreDefault(), placeholder));
      }
   }

   public List getLore() {
      return (List)(this.getLoreDefault() == null ? new ArrayList() : coloredList(replacePlaceholders(this.getLoreDefault(), this.getPlaceholderDefaults())));
   }

   public boolean sendMessage() {
      return true;
   }

   public abstract HashMap getPlaceholderDefaults();

   public Ability() {
      Iterator var1 = ((List)Arrays.stream(this.getClass().getMethods()).filter((m) -> {
         return m.isAnnotationPresent(EventHandler.class);
      }).collect(Collectors.toList())).iterator();

      while(var1.hasNext()) {
         Method method = (Method)var1.next();
         Class clazz = method.getParameterTypes()[0];
         List list = (List)this.cache.getOrDefault(clazz, new ArrayList());
         list.add(method);
         this.cache.put(clazz, list);
      }

   }

   public void runEvent(Event e) {
      try {
         Iterator var2 = ((List)this.cache.getOrDefault(e.getClass(), new ArrayList())).iterator();

         while(var2.hasNext()) {
            Method m = (Method)var2.next();
            m.setAccessible(true);
            m.invoke(this, e);
         }

      } catch (Throwable var4) {
         throw var4;
      }
   }

   public boolean hasEvent(Class e) {
      return this.cache.containsKey(e);
   }

   public boolean hasEvent(Event e) {
      return this.cache.containsKey(e.getClass());
   }

   public boolean contains(String path) {
      return this.fileConfiguration.contains(path);
   }

   public int getInt(String location) {
      return this.fileConfiguration.getInt(location);
   }

   public double getDouble(String location) {
      return this.fileConfiguration.getDouble(location);
   }

   public String getString(String location) {
      return this.fileConfiguration.getString(location);
   }

   public static boolean isFullSet(ItemStack[] list, String value) {
      ItemStack[] var2 = list;
      int var3 = list.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemStack is = var2[var4];
         if (!isValidItem(is)) {
            return false;
         }

         CustomItem item = new CustomItem(is);
         if (!item.hasAbility(value)) {
            return false;
         }
      }

      return true;
   }

   public FileConfiguration getFileConfiguration() {
      return this.fileConfiguration;
   }
}
