package koji.skyblock.files.data;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.sql.DataSource;
import koji.developerkit.utils.duplet.Duplet;
import koji.skyblock.Skyblock;
import koji.skyblock.files.Config;
import koji.skyblock.files.Files;
import koji.skyblock.item.CustomItem;
import koji.skyblock.item.enchants.EnchantTableGUI;
import koji.skyblock.item.utils.ItemStackSerializer;
import koji.skyblock.player.PClass;
import koji.skyblock.player.Stats;
import koji.skyblock.utils.StatMap;
import org.bukkit.entity.Player;

public class SQLData extends PlayerData {
   private final HashMap bits = new HashMap();
   private final HashMap statMap = new HashMap();
   private final HashMap activePet = new HashMap();
   private final HashMap canSeePets = new HashMap();
   private final HashMap getsIt = new HashMap();
   private final HashMap dropItemAlert = new HashMap();
   private final HashMap tableSorting = new HashMap();
   private final HashMap itemStash = new HashMap();
   private final HashMap uuids = new HashMap();
   public MysqlDataSource dataSource;

   public void setBits(Player p, Integer i) {
      try {
         Connection conn = this.connection();
         Throwable var4 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPlayer(uuid, bits) VALUES(?, ?);");
            Throwable var6 = null;

            try {
               stmt.setString(1, p.getUniqueId().toString());
               stmt.setInt(2, i);
               stmt.setInt(3, i);
               stmt.execute();
            } catch (Throwable var31) {
               var6 = var31;
               throw var31;
            } finally {
               if (stmt != null) {
                  if (var6 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var33) {
            var4 = var33;
            throw var33;
         } finally {
            if (conn != null) {
               if (var4 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var29) {
                     var4.addSuppressed(var29);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var35) {
         var35.printStackTrace();
      }

      PClass.getPlayer(p).setBits(i);
   }

   public int getBits(Player p) {
      if (this.bits.containsKey(p)) {
         this.bits.get(p);
      }

      try {
         Connection conn = this.connection();
         Throwable var3 = null;

         int var7;
         try {
            PreparedStatement stmt = conn.prepareStatement("SELECT bits FROM kojiSBPlayer WHERE uuid = ?;");
            Throwable var5 = null;

            try {
               stmt.setString(1, p.getUniqueId().toString());
               ResultSet resultSet = stmt.executeQuery();
               if (!resultSet.next()) {
                  var7 = 0;
                  return var7;
               }

               this.bits.put(p, resultSet.getInt("bits"));
               var7 = resultSet.getInt("bits");
            } catch (Throwable var35) {
               var5 = var35;
               throw var35;
            } finally {
               if (stmt != null) {
                  if (var5 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var34) {
                        var5.addSuppressed(var34);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var37) {
            var3 = var37;
            throw var37;
         } finally {
            if (conn != null) {
               if (var3 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var33) {
                     var3.addSuppressed(var33);
                  }
               } else {
                  conn.close();
               }
            }

         }

         return var7;
      } catch (SQLException var39) {
         return 0;
      }
   }

   public void setStat(Player p, Stats type, Double value) {
      try {
         Connection conn = this.connection();
         Throwable var5 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPlayer(uuid, base_" + type.getPlaceholderTag() + ") VALUES(?, ?);");
            Throwable var7 = null;

            try {
               stmt.setString(1, p.getUniqueId().toString());
               stmt.setDouble(2, value);
               StatMap map = (StatMap)this.statMap.getOrDefault(p, new StatMap(new Duplet[0]));
               map.put(type, value);
               this.statMap.put(p, map);
               stmt.execute();
            } catch (Throwable var32) {
               var7 = var32;
               throw var32;
            } finally {
               if (stmt != null) {
                  if (var7 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var31) {
                        var7.addSuppressed(var31);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var34) {
            var5 = var34;
            throw var34;
         } finally {
            if (conn != null) {
               if (var5 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var30) {
                     var5.addSuppressed(var30);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var36) {
         var36.printStackTrace();
      }

   }

   public double getStat(Player p, Stats stat) {
      if (this.statMap.containsKey(p) && ((StatMap)this.statMap.get(p)).containsKey(stat)) {
         return ((StatMap)this.statMap.get(p)).get(stat);
      } else {
         try {
            Connection conn = this.connection();
            Throwable var4 = null;

            double var9;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT base_" + stat.getPlaceholderTag() + " FROM kojiSBPlayer WHERE uuid = ?;");
               Throwable var6 = null;

               try {
                  stmt.setString(1, p.getUniqueId().toString());
                  ResultSet resultSet = stmt.executeQuery();
                  StatMap map;
                  if (!resultSet.next()) {
                     map = 0.0D;
                     return (double)map;
                  }

                  map = (StatMap)this.statMap.getOrDefault(p, new StatMap(new Duplet[0]));
                  map.put(stat, resultSet.getDouble("base_" + stat.getPlaceholderTag()));
                  this.statMap.put(p, map);
                  var9 = resultSet.getDouble("base_" + stat.getPlaceholderTag());
               } catch (Throwable var38) {
                  var6 = var38;
                  throw var38;
               } finally {
                  if (stmt != null) {
                     if (var6 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var37) {
                           var6.addSuppressed(var37);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var40) {
               var4 = var40;
               throw var40;
            } finally {
               if (conn != null) {
                  if (var4 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var36) {
                        var4.addSuppressed(var36);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var9;
         } catch (SQLException var42) {
            return 0.0D;
         }
      }
   }

   public String getActivePet(Player p) {
      if (this.activePet.containsKey(p)) {
         return (String)this.activePet.get(p);
      } else {
         try {
            Connection conn = this.connection();
            Throwable var3 = null;

            String var7;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT activePet FROM kojiSBPlayer WHERE uuid = ?;");
               Throwable var5 = null;

               try {
                  stmt.setString(1, p.getUniqueId().toString());
                  ResultSet resultSet = stmt.executeQuery();
                  if (resultSet.next()) {
                     this.activePet.put(p, resultSet.getString("activePet"));
                     var7 = resultSet.getString("activePet");
                     return var7;
                  }

                  var7 = null;
               } catch (Throwable var35) {
                  var5 = var35;
                  throw var35;
               } finally {
                  if (stmt != null) {
                     if (var5 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var34) {
                           var5.addSuppressed(var34);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var37) {
               var3 = var37;
               throw var37;
            } finally {
               if (conn != null) {
                  if (var3 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var33) {
                        var3.addSuppressed(var33);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var7;
         } catch (SQLException var39) {
            return null;
         }
      }
   }

   public void setActivePet(Player p, String pet) {
      try {
         Connection conn = this.connection();
         Throwable var4 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPlayer(uuid, activePet) VALUES(?, ?);");
            Throwable var6 = null;

            try {
               stmt.setString(1, p.getUniqueId().toString());
               stmt.setString(2, pet);
               this.activePet.put(p, pet);
               stmt.execute();
            } catch (Throwable var31) {
               var6 = var31;
               throw var31;
            } finally {
               if (stmt != null) {
                  if (var6 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var33) {
            var4 = var33;
            throw var33;
         } finally {
            if (conn != null) {
               if (var4 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var29) {
                     var4.addSuppressed(var29);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var35) {
         var35.printStackTrace();
      }

   }

   public Boolean canSeePets(Player p) {
      if (this.canSeePets.containsKey(p)) {
         return (Boolean)this.canSeePets.get(p);
      } else {
         try {
            Connection conn = this.connection();
            Throwable var3 = null;

            Boolean var7;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT canSeePets FROM kojiSBPlayer WHERE uuid = ?;");
               Throwable var5 = null;

               try {
                  stmt.setString(1, p.getUniqueId().toString());
                  ResultSet resultSet = stmt.executeQuery();
                  if (!resultSet.next()) {
                     var7 = true;
                     return var7;
                  }

                  this.canSeePets.put(p, resultSet.getBoolean("canSeePets"));
                  var7 = resultSet.getBoolean("canSeePets");
               } catch (Throwable var35) {
                  var5 = var35;
                  throw var35;
               } finally {
                  if (stmt != null) {
                     if (var5 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var34) {
                           var5.addSuppressed(var34);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var37) {
               var3 = var37;
               throw var37;
            } finally {
               if (conn != null) {
                  if (var3 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var33) {
                        var3.addSuppressed(var33);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var7;
         } catch (SQLException var39) {
            return true;
         }
      }
   }

   public void setCanSeePets(Player p, Boolean pet) {
      try {
         Connection conn = this.connection();
         Throwable var4 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPlayer(uuid, canSeePets) VALUES(?, ?);");
            Throwable var6 = null;

            try {
               stmt.setString(1, p.getUniqueId().toString());
               stmt.setBoolean(2, pet);
               this.canSeePets.put(p, pet);
               stmt.execute();
            } catch (Throwable var31) {
               var6 = var31;
               throw var31;
            } finally {
               if (stmt != null) {
                  if (var6 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var33) {
            var4 = var33;
            throw var33;
         } finally {
            if (conn != null) {
               if (var4 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var29) {
                     var4.addSuppressed(var29);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var35) {
         var35.printStackTrace();
      }

   }

   public Boolean getsIt(Player p) {
      if (this.getsIt.containsKey(p)) {
         return (Boolean)this.getsIt.get(p);
      } else {
         try {
            Connection conn = this.connection();
            Throwable var3 = null;

            Boolean var7;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT getsIt FROM kojiSBPlayer WHERE uuid = ?;");
               Throwable var5 = null;

               try {
                  stmt.setString(1, p.getUniqueId().toString());
                  ResultSet resultSet = stmt.executeQuery();
                  if (resultSet.next()) {
                     this.getsIt.put(p, resultSet.getBoolean("getsIt"));
                     var7 = resultSet.getBoolean("getsIt");
                     return var7;
                  }

                  var7 = false;
               } catch (Throwable var35) {
                  var5 = var35;
                  throw var35;
               } finally {
                  if (stmt != null) {
                     if (var5 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var34) {
                           var5.addSuppressed(var34);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var37) {
               var3 = var37;
               throw var37;
            } finally {
               if (conn != null) {
                  if (var3 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var33) {
                        var3.addSuppressed(var33);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var7;
         } catch (SQLException var39) {
            return false;
         }
      }
   }

   public void setGetsIt(Player p, Boolean getsIt) {
      try {
         Connection conn = this.connection();
         Throwable var4 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPlayer(uuid, getsIt) VALUES(?, ?);");
            Throwable var6 = null;

            try {
               stmt.setString(1, p.getUniqueId().toString());
               stmt.setBoolean(2, getsIt);
               this.getsIt.put(p, getsIt);
               stmt.execute();
            } catch (Throwable var31) {
               var6 = var31;
               throw var31;
            } finally {
               if (stmt != null) {
                  if (var6 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var33) {
            var4 = var33;
            throw var33;
         } finally {
            if (conn != null) {
               if (var4 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var29) {
                     var4.addSuppressed(var29);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var35) {
         var35.printStackTrace();
      }

   }

   public Boolean getDropItemAlert(Player p) {
      if (this.dropItemAlert.containsKey(p)) {
         return (Boolean)this.dropItemAlert.get(p);
      } else {
         try {
            Connection conn = this.connection();
            Throwable var3 = null;

            Boolean var7;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT dropItemAlert FROM kojiSBPlayer WHERE uuid = ?;");
               Throwable var5 = null;

               try {
                  stmt.setString(1, p.getUniqueId().toString());
                  ResultSet resultSet = stmt.executeQuery();
                  if (resultSet.next()) {
                     this.dropItemAlert.put(p, resultSet.getBoolean("dropItemAlert"));
                     var7 = resultSet.getBoolean("dropItemAlert");
                     return var7;
                  }

                  var7 = false;
               } catch (Throwable var35) {
                  var5 = var35;
                  throw var35;
               } finally {
                  if (stmt != null) {
                     if (var5 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var34) {
                           var5.addSuppressed(var34);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var37) {
               var3 = var37;
               throw var37;
            } finally {
               if (conn != null) {
                  if (var3 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var33) {
                        var3.addSuppressed(var33);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var7;
         } catch (SQLException var39) {
            return false;
         }
      }
   }

   public void setDropItemAlert(Player p, Boolean boo) {
      try {
         Connection conn = this.connection();
         Throwable var4 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPlayer(uuid, dropItemAlert) VALUES(?, ?);");
            Throwable var6 = null;

            try {
               stmt.setString(1, p.getUniqueId().toString());
               stmt.setBoolean(2, boo);
               this.dropItemAlert.put(p, boo);
               stmt.execute();
            } catch (Throwable var31) {
               var6 = var31;
               throw var31;
            } finally {
               if (stmt != null) {
                  if (var6 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var33) {
            var4 = var33;
            throw var33;
         } finally {
            if (conn != null) {
               if (var4 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var29) {
                     var4.addSuppressed(var29);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var35) {
         var35.printStackTrace();
      }

   }

   public EnchantTableGUI.Sorting getEnchantTableSorting(Player p) {
      if (this.tableSorting.containsKey(p)) {
         return EnchantTableGUI.Sorting.valueOf((String)this.tableSorting.get(p));
      } else {
         try {
            Connection conn = this.connection();
            Throwable var3 = null;

            EnchantTableGUI.Sorting var7;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT eTableSorting FROM kojiSBPlayer WHERE uuid = ?;");
               Throwable var5 = null;

               try {
                  stmt.setString(1, p.getUniqueId().toString());
                  ResultSet resultSet = stmt.executeQuery();
                  if (!resultSet.next()) {
                     var7 = EnchantTableGUI.Sorting.DEFAULT;
                     return var7;
                  }

                  this.tableSorting.put(p, resultSet.getString("eTableSorting"));
                  var7 = EnchantTableGUI.Sorting.valueOf(resultSet.getString("eTableSorting"));
               } catch (Throwable var35) {
                  var5 = var35;
                  throw var35;
               } finally {
                  if (stmt != null) {
                     if (var5 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var34) {
                           var5.addSuppressed(var34);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var37) {
               var3 = var37;
               throw var37;
            } finally {
               if (conn != null) {
                  if (var3 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var33) {
                        var3.addSuppressed(var33);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var7;
         } catch (SQLException var39) {
            return EnchantTableGUI.Sorting.DEFAULT;
         }
      }
   }

   public void setEnchantTableSorting(Player p, EnchantTableGUI.Sorting sorting) {
      try {
         Connection conn = this.connection();
         Throwable var4 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPlayer(uuid, eTableSorting) VALUES(?, ?);");
            Throwable var6 = null;

            try {
               stmt.setString(1, p.getUniqueId().toString());
               stmt.setString(2, sorting.name().toUpperCase());
               this.tableSorting.put(p, sorting.name().toUpperCase());
               stmt.execute();
            } catch (Throwable var31) {
               var6 = var31;
               throw var31;
            } finally {
               if (stmt != null) {
                  if (var6 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var33) {
            var4 = var33;
            throw var33;
         } finally {
            if (conn != null) {
               if (var4 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var29) {
                     var4.addSuppressed(var29);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var35) {
         var35.printStackTrace();
      }

   }

   public List getItemStash(Player p) {
      if (this.itemStash.containsKey(p)) {
         return (List)this.itemStash.get(p);
      } else {
         try {
            Connection conn = this.connection();
            Throwable var3 = null;

            Object stringsAsList;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT itemStash FROM kojiSBPlayer WHERE uuid = ?;");
               Throwable var5 = null;

               try {
                  stmt.setString(1, p.getUniqueId().toString());
                  ResultSet resultSet = stmt.executeQuery();
                  Object result;
                  if (!resultSet.next()) {
                     result = new ArrayList();
                     return (List)result;
                  }

                  result = resultSet.getString("itemStash");
                  if (!((String)result).equalsIgnoreCase("null")) {
                     stringsAsList = Arrays.asList(((String)result).split("~itemstash--item~"));
                     List items = new ArrayList();
                     ((List)stringsAsList).forEach((item) -> {
                        CustomItem ci = ItemStackSerializer.deserialize(item);
                        if (ci != null) {
                           items.add(ci.buildWithAbilities());
                        }
                     });
                     this.itemStash.put(p, items);
                     ArrayList var10 = items;
                     return var10;
                  }

                  stringsAsList = new ArrayList();
               } catch (Throwable var41) {
                  var5 = var41;
                  throw var41;
               } finally {
                  if (stmt != null) {
                     if (var5 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var40) {
                           var5.addSuppressed(var40);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var43) {
               var3 = var43;
               throw var43;
            } finally {
               if (conn != null) {
                  if (var3 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var39) {
                        var3.addSuppressed(var39);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return (List)stringsAsList;
         } catch (SQLException var45) {
            return new ArrayList();
         }
      }
   }

   public void setItemStash(Player p, List items) {
      List strings = new ArrayList();
      items.forEach((item) -> {
         strings.add(ItemStackSerializer.serialize(item));
      });
      StringBuilder sb;
      if (items.isEmpty()) {
         sb = new StringBuilder("null");
      } else {
         sb = new StringBuilder();

         String s;
         for(Iterator var5 = strings.iterator(); var5.hasNext(); sb.append(s)) {
            s = (String)var5.next();
            if (sb.length() > 0) {
               sb.append("~itemstash--item~");
            }
         }
      }

      try {
         Connection conn = this.connection();
         Throwable var39 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPlayer(uuid, itemStash) VALUES(?, ?);");
            Throwable var8 = null;

            try {
               stmt.setString(1, p.getUniqueId().toString());
               stmt.setString(2, sb.toString());
               this.itemStash.put(p, items);
               stmt.execute();
            } catch (Throwable var33) {
               var8 = var33;
               throw var33;
            } finally {
               if (stmt != null) {
                  if (var8 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var32) {
                        var8.addSuppressed(var32);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var35) {
            var39 = var35;
            throw var35;
         } finally {
            if (conn != null) {
               if (var39 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var31) {
                     var39.addSuppressed(var31);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var37) {
         var37.printStackTrace();
      }

   }

   public void createPlayer(Player p) {
      ArrayList keysForTable = arrayList(new String[]{"uuid", "bits", "activePet", "canSeePets", "getsIt", "eTableSorting", "itemStash"});
      ArrayList valuesForTable = arrayList(new Object[]{p.getUniqueId().toString(), 0, null, Config.getPetsVisibleDefault(), Config.getGetsItDefault(), Config.getItemDropAlertDefault(), "DEFAULT", "null"});
      Stats[] var4 = Stats.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Stats stats = var4[var6];
         if (stats != Stats.HEALTH && stats != Stats.MANA) {
            keysForTable.add("base_" + stats.getPlaceholderTag());
         }
      }

      StringBuilder insertString = new StringBuilder();
      keysForTable.forEach((s) -> {
         if (insertString.length() != 0) {
            insertString.append(", ");
         }

         insertString.append(s);
      });
      StringBuilder questionMarks = new StringBuilder();
      keysForTable.forEach((s) -> {
         if (questionMarks.length() != 0) {
            questionMarks.append(",");
         }

         questionMarks.append(" ?");
      });
      String insert = !this.doesPlayerDataExist(p) ? "INSERT INTO" : "REPLACE";

      try {
         Connection conn = this.connection();
         Throwable var8 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement(insert + " kojiSBPlayer(" + insertString + ") VALUES(" + questionMarks + ");");
            Throwable var10 = null;

            try {
               for(int i = 0; i < keysForTable.size(); ++i) {
                  String string = (String)keysForTable.get(i);
                  byte var14 = -1;
                  switch(string.hashCode()) {
                  case -1249320472:
                     if (string.equals("getsIt")) {
                        var14 = 6;
                     }
                     break;
                  case -37920806:
                     if (string.equals("dropItemAlert")) {
                        var14 = 7;
                     }
                     break;
                  case 3024134:
                     if (string.equals("bits")) {
                        var14 = 4;
                     }
                     break;
                  case 3601339:
                     if (string.equals("uuid")) {
                        var14 = 0;
                     }
                     break;
                  case 33706715:
                     if (string.equals("eTableSorting")) {
                        var14 = 2;
                     }
                     break;
                  case 204477497:
                     if (string.equals("activePet")) {
                        var14 = 1;
                     }
                     break;
                  case 566882359:
                     if (string.equals("canSeePets")) {
                        var14 = 5;
                     }
                     break;
                  case 2142719266:
                     if (string.equals("itemStash")) {
                        var14 = 3;
                     }
                  }

                  switch(var14) {
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                     stmt.setString(i + 1, (String)valuesForTable.get(i));
                     break;
                  case 4:
                     stmt.setInt(i + 1, (Integer)valuesForTable.get(i));
                     break;
                  case 5:
                  case 6:
                  case 7:
                     stmt.setBoolean(i + 1, (Boolean)valuesForTable.get(i));
                  }

                  if (string.startsWith("base_")) {
                     Stats stats = Stats.parseFromPlaceholder(string.replace("base_", ""));
                     if (stats != null) {
                        stmt.setDouble(i + 1, Config.getBaseValue(stats));
                     } else {
                        stmt.setDouble(i + 1, 0.0D);
                     }
                  }
               }

               stmt.execute();
            } catch (Throwable var38) {
               var10 = var38;
               throw var38;
            } finally {
               if (stmt != null) {
                  if (var10 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var37) {
                        var10.addSuppressed(var37);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var40) {
            var8 = var40;
            throw var40;
         } finally {
            if (conn != null) {
               if (var8 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var36) {
                     var8.addSuppressed(var36);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var42) {
         var42.printStackTrace();
      }

   }

   public boolean doesPlayerDataExist(Player p) {
      if (this.uuids.containsKey(p)) {
         return true;
      } else {
         try {
            Connection conn = this.connection();
            Throwable var3 = null;

            boolean var7;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT * FROM kojiSBPlayer WHERE uuid = ?;");
               Throwable var5 = null;

               try {
                  stmt.setString(1, p.getUniqueId().toString());
                  ResultSet resultSet = stmt.executeQuery();
                  if (resultSet.next()) {
                     this.uuids.put(p, resultSet.getString("uuid"));
                     var7 = resultSet.getString("uuid") != null;
                     return var7;
                  }

                  var7 = false;
               } catch (Throwable var35) {
                  var5 = var35;
                  throw var35;
               } finally {
                  if (stmt != null) {
                     if (var5 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var34) {
                           var5.addSuppressed(var34);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var37) {
               var3 = var37;
               throw var37;
            } finally {
               if (conn != null) {
                  if (var3 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var33) {
                        var3.addSuppressed(var33);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var7;
         } catch (SQLException var39) {
            return false;
         }
      }
   }

   public Connection connection() {
      try {
         return this.dataSource.getConnection();
      } catch (Throwable var2) {
         throw var2;
      }
   }

   public boolean register() {
      this.dataSource = new MysqlConnectionPoolDataSource();
      this.dataSource.setServerName(Files.getConfig().getString("player-data.database.host").replace("https://", ""));
      this.dataSource.setPortNumber(Files.getConfig().getInt("player-data.database.port"));
      this.dataSource.setDatabaseName(Files.getConfig().getString("player-data.database.database"));
      this.dataSource.setUser(Files.getConfig().getString("player-data.database.user"));
      this.dataSource.setPassword(Files.getConfig().getString("player-data.database.password"));
      if (!this.testMySQLConnection(this.dataSource)) {
         return false;
      } else {
         try {
            Connection conn = this.dataSource.getConnection();
            Throwable var2 = null;

            try {
               PreparedStatement stmt = conn.prepareStatement(this.getTable());
               stmt.execute();
            } catch (Throwable var12) {
               var2 = var12;
               throw var12;
            } finally {
               if (conn != null) {
                  if (var2 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var11) {
                        var2.addSuppressed(var11);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return true;
         } catch (SQLException var14) {
            var14.printStackTrace();
            return false;
         }
      }
   }

   private boolean testMySQLConnection(DataSource dataSource) {
      try {
         Connection conn = dataSource.getConnection();
         Throwable var3 = null;

         boolean var4;
         try {
            if (conn.isValid(1)) {
               return true;
            }

            var4 = false;
         } catch (Throwable var15) {
            var3 = var15;
            throw var15;
         } finally {
            if (conn != null) {
               if (var3 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var14) {
                     var3.addSuppressed(var14);
                  }
               } else {
                  conn.close();
               }
            }

         }

         return var4;
      } catch (SQLException var17) {
         Skyblock.getPlugin().getLogger().log(Level.WARNING, "Unable to connect to database! (maybe URL was invalid?)");
         return false;
      }
   }

   public String getTable() {
      StringBuilder mainTable = new StringBuilder("CREATE TABLE IF NOT EXISTS kojiSBPlayer (uuid CHAR(36) NOT NULL,bits BIGINT DEFAULT 0 NOT NULL,activePet CHAR(36) DEFAULT 'null',canSeePets BOOLEAN DEFAULT 1,getsIt BOOLEAN DEFAULT 0,dropItemAlert BOOLEAN DEFAULT 1,eTableSorting VARCHAR(7) DEFAULT 'DEFAULT',itemStash VARCHAR(65535) DEFAULT 'null'");
      Stats[] var2 = Stats.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Stats stats = var2[var4];
         if (stats != Stats.HEALTH && stats != Stats.MANA) {
            mainTable.append("base_").append(stats.getPlaceholderTag()).append(" FLOAT(25) DEFAULT ").append(Config.getBaseValue(stats)).append(" NOT NULL,");
         }
      }

      mainTable.append("PRIMARY KEY (uuid));");
      return mainTable.toString();
   }
}
