package koji.skyblock.pets.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public abstract class PetAbility implements Listener {
   public abstract String getName();

   public abstract String getDisplayName();

   public abstract ArrayList validRarities();

   public abstract HashMap getPlaceHolderSlotsBaseValue();

   public abstract ArrayList getLore();

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
}
