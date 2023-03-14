package koji.skyblock.files.pets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import koji.skyblock.item.Rarity;
import org.bukkit.entity.Player;

public class SQLPetData extends PetData {
   Connection connection;
   HashMap types = new HashMap();
   HashMap levels = new HashMap();
   HashMap currentXP = new HashMap();
   HashMap skins = new HashMap();
   HashMap rarity = new HashMap();
   private final HashMap uuids = new HashMap();
   HashMap pets = new HashMap();

   public SQLPetData(Connection data) {
      this.connection = data;
      String table = "CREATE TABLE IF NOT EXISTS kojiSBPets (uuid CHAR(36) NOT NULL,type VARCHAR(36) DEFAULT 'null',level BIGINT DEFAULT 1 NOT NULL,currentXP FLOAT(25) DEFAULT 0.0 NOT NULL,skin VARCHAR(36) DEFAULT 'null',rarity VARCHAR(12) DEFAULT 'COMMON',player CHAR(36) DEFAULT 'null',PRIMARY KEY (uuid));";

      try {
         Connection conn = this.connection;
         Throwable var4 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement(table);
            stmt.execute();
            stmt.close();
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (conn != null) {
               if (var4 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var16) {
         var16.printStackTrace();
      }

   }

   public String getType(Player p, String uuid) {
      if (this.types.containsKey(uuid)) {
         return (String)this.types.get(uuid);
      } else {
         try {
            Connection conn = this.connection;
            Throwable var4 = null;

            String var8;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT type FROM kojiSBPets WHERE uuid = ?;");
               Throwable var6 = null;

               try {
                  stmt.setString(1, uuid);
                  ResultSet resultSet = stmt.executeQuery();
                  if (resultSet.next()) {
                     this.types.put(uuid, resultSet.getString("type"));
                     var8 = resultSet.getString("type");
                     return var8;
                  }

                  conn.close();
                  var8 = null;
               } catch (Throwable var36) {
                  var6 = var36;
                  throw var36;
               } finally {
                  if (stmt != null) {
                     if (var6 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var35) {
                           var6.addSuppressed(var35);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var38) {
               var4 = var38;
               throw var38;
            } finally {
               if (conn != null) {
                  if (var4 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var34) {
                        var4.addSuppressed(var34);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var8;
         } catch (SQLException var40) {
            return null;
         }
      }
   }

   public void setType(Player p, String uuid, String type) {
      try {
         Connection conn = this.connection;
         Throwable var5 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPets(uuid, type) VALUES(?, ?);");
            Throwable var7 = null;

            try {
               stmt.setString(1, uuid);
               stmt.setString(2, type);
               this.types.put(uuid, type);
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

   public int getLevel(Player p, String uuid) {
      if (this.levels.containsKey(uuid)) {
         return (Integer)this.levels.get(uuid);
      } else {
         try {
            Connection conn = this.connection;
            Throwable var4 = null;

            int var8;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT level FROM kojiSBPets WHERE uuid = ?;");
               Throwable var6 = null;

               try {
                  stmt.setString(1, uuid);
                  ResultSet resultSet = stmt.executeQuery();
                  if (!resultSet.next()) {
                     conn.close();
                     var8 = 0;
                     return var8;
                  }

                  this.levels.put(uuid, resultSet.getInt("level"));
                  var8 = resultSet.getInt("level");
               } catch (Throwable var36) {
                  var6 = var36;
                  throw var36;
               } finally {
                  if (stmt != null) {
                     if (var6 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var35) {
                           var6.addSuppressed(var35);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var38) {
               var4 = var38;
               throw var38;
            } finally {
               if (conn != null) {
                  if (var4 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var34) {
                        var4.addSuppressed(var34);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var8;
         } catch (SQLException var40) {
            return 0;
         }
      }
   }

   public void setLevel(Player p, String uuid, int type) {
      try {
         Connection conn = this.connection;
         Throwable var5 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPets(uuid, level) VALUES(?, ?);");
            Throwable var7 = null;

            try {
               stmt.setString(1, uuid);
               stmt.setInt(2, type);
               this.levels.put(uuid, type);
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

   public double getCurrentExp(Player p, String uuid) {
      if (this.currentXP.containsKey(uuid)) {
         return (Double)this.currentXP.get(uuid);
      } else {
         try {
            Connection conn = this.connection;
            Throwable var4 = null;

            double var8;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT currentXP FROM kojiSBPets WHERE uuid = ?;");
               Throwable var6 = null;

               try {
                  stmt.setString(1, uuid);
                  ResultSet resultSet = stmt.executeQuery();
                  if (!resultSet.next()) {
                     conn.close();
                     var8 = 0.0D;
                     return var8;
                  }

                  this.currentXP.put(uuid, resultSet.getDouble("currentXP"));
                  var8 = resultSet.getDouble("currentXP");
               } catch (Throwable var37) {
                  var6 = var37;
                  throw var37;
               } finally {
                  if (stmt != null) {
                     if (var6 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var36) {
                           var6.addSuppressed(var36);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var39) {
               var4 = var39;
               throw var39;
            } finally {
               if (conn != null) {
                  if (var4 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var35) {
                        var4.addSuppressed(var35);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var8;
         } catch (SQLException var41) {
            return 0.0D;
         }
      }
   }

   public void setCurrentExp(Player p, String uuid, double type) {
      try {
         Connection conn = this.connection;
         Throwable var6 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPets(uuid, currentXP) VALUES(?, ?);");
            Throwable var8 = null;

            try {
               stmt.setString(1, uuid);
               stmt.setDouble(2, type);
               this.currentXP.put(uuid, type);
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
            var6 = var35;
            throw var35;
         } finally {
            if (conn != null) {
               if (var6 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var31) {
                     var6.addSuppressed(var31);
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

   public String getSkin(Player p, String uuid) {
      if (this.skins.containsKey(uuid)) {
         return (String)this.skins.get(uuid);
      } else {
         try {
            Connection conn = this.connection;
            Throwable var4 = null;

            String var8;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT skin FROM kojiSBPets WHERE uuid = ?;");
               Throwable var6 = null;

               try {
                  stmt.setString(1, uuid);
                  ResultSet resultSet = stmt.executeQuery();
                  if (resultSet.next()) {
                     this.skins.put(uuid, resultSet.getString("skin"));
                     var8 = resultSet.getString("skin");
                     return var8;
                  }

                  conn.close();
                  var8 = null;
               } catch (Throwable var36) {
                  var6 = var36;
                  throw var36;
               } finally {
                  if (stmt != null) {
                     if (var6 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var35) {
                           var6.addSuppressed(var35);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var38) {
               var4 = var38;
               throw var38;
            } finally {
               if (conn != null) {
                  if (var4 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var34) {
                        var4.addSuppressed(var34);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var8;
         } catch (SQLException var40) {
            return null;
         }
      }
   }

   public void setSkin(Player p, String uuid, String type) {
      try {
         Connection conn = this.connection;
         Throwable var5 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPets(uuid, skin) VALUES(?, ?);");
            Throwable var7 = null;

            try {
               stmt.setString(1, uuid);
               stmt.setString(2, type);
               this.skins.put(uuid, type);
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

   public Rarity getRarity(Player p, String uuid) {
      if (this.rarity.containsKey(uuid)) {
         return (Rarity)this.rarity.get(uuid);
      } else {
         try {
            Connection conn = this.connection;
            Throwable var4 = null;

            Rarity var8;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT rarity FROM kojiSBPets WHERE uuid = ?;");
               Throwable var6 = null;

               try {
                  stmt.setString(1, uuid);
                  ResultSet resultSet = stmt.executeQuery();
                  if (!resultSet.next()) {
                     conn.close();
                     var8 = null;
                     return var8;
                  }

                  this.skins.put(uuid, resultSet.getString("rarity"));
                  var8 = Rarity.valueOf(resultSet.getString("rarity").toUpperCase());
               } catch (Throwable var36) {
                  var6 = var36;
                  throw var36;
               } finally {
                  if (stmt != null) {
                     if (var6 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var35) {
                           var6.addSuppressed(var35);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var38) {
               var4 = var38;
               throw var38;
            } finally {
               if (conn != null) {
                  if (var4 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var34) {
                        var4.addSuppressed(var34);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var8;
         } catch (SQLException var40) {
            return null;
         }
      }
   }

   public void setRarity(Player p, String uuid, Rarity type) {
      try {
         Connection conn = this.connection;
         Throwable var5 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("REPLACE kojiSBPets(uuid, rarity) VALUES(?, ?);");
            Throwable var7 = null;

            try {
               stmt.setString(1, uuid);
               stmt.setString(2, type.getName());
               this.rarity.put(uuid, type);
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

   public boolean getPetExists(Player p, String uuid) {
      if (this.uuids.containsKey(uuid)) {
         return true;
      } else {
         try {
            Connection conn = this.connection;
            Throwable var4 = null;

            boolean var8;
            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT * FROM kojiSBPets WHERE uuid = ?;");
               Throwable var6 = null;

               try {
                  stmt.setString(1, uuid);
                  ResultSet resultSet = stmt.executeQuery();
                  if (resultSet.next()) {
                     this.uuids.put(uuid, resultSet.getString("uuid"));
                     var8 = resultSet.getString("uuid") != null;
                     return var8;
                  }

                  conn.close();
                  var8 = false;
               } catch (Throwable var36) {
                  var6 = var36;
                  throw var36;
               } finally {
                  if (stmt != null) {
                     if (var6 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var35) {
                           var6.addSuppressed(var35);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var38) {
               var4 = var38;
               throw var38;
            } finally {
               if (conn != null) {
                  if (var4 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var34) {
                        var4.addSuppressed(var34);
                     }
                  } else {
                     conn.close();
                  }
               }

            }

            return var8;
         } catch (SQLException var40) {
            return false;
         }
      }
   }

   public void createPet(Player p, String uuid, String type, int level, double currentXP, String skin, Rarity rarity) {
      try {
         Connection conn = this.connection;
         Throwable var10 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO kojiSBPets(uuid, type, level, currentXP, skin, rarity, player) VALUES(?, ?, ?, ?, ?, ?, ?);");
            Throwable var12 = null;

            try {
               stmt.setString(1, uuid);
               stmt.setString(2, type);
               stmt.setInt(3, level);
               stmt.setDouble(4, currentXP);
               stmt.setString(5, skin);
               stmt.setString(6, rarity.getName());
               stmt.setString(7, p.getUniqueId().toString());
               stmt.execute();
            } catch (Throwable var37) {
               var12 = var37;
               throw var37;
            } finally {
               if (stmt != null) {
                  if (var12 != null) {
                     try {
                        stmt.close();
                     } catch (Throwable var36) {
                        var12.addSuppressed(var36);
                     }
                  } else {
                     stmt.close();
                  }
               }

            }
         } catch (Throwable var39) {
            var10 = var39;
            throw var39;
         } finally {
            if (conn != null) {
               if (var10 != null) {
                  try {
                     conn.close();
                  } catch (Throwable var35) {
                     var10.addSuppressed(var35);
                  }
               } else {
                  conn.close();
               }
            }

         }
      } catch (SQLException var41) {
         var41.printStackTrace();
      }

   }

   public boolean getPlayerExists(Player p) {
      return true;
   }

   public void createPlayer(Player p) {
   }

   public void addPet(Player p, String uuid) {
      List list = (List)this.pets.getOrDefault(p, new ArrayList());
      list.add(uuid);
      this.pets.put(p, list);
   }

   public List getPets(Player p) {
      if (this.pets.containsKey(p)) {
         return (List)this.pets.get(p);
      } else {
         try {
            Connection conn = this.connection;
            Throwable var3 = null;

            try {
               PreparedStatement stmt = conn.prepareStatement("SELECT uuid FROM kojiSBPets WHERE player = ?;");
               Throwable var5 = null;

               try {
                  stmt.setString(1, p.getUniqueId().toString());
                  ResultSet resultSet = stmt.executeQuery();
                  ArrayList strings = new ArrayList();

                  while(resultSet.next()) {
                     strings.add(resultSet.getString("uuid"));
                  }

                  this.pets.put(p, strings);
                  ArrayList var8 = strings;
                  return var8;
               } catch (Throwable var33) {
                  var5 = var33;
                  throw var33;
               } finally {
                  if (stmt != null) {
                     if (var5 != null) {
                        try {
                           stmt.close();
                        } catch (Throwable var32) {
                           var5.addSuppressed(var32);
                        }
                     } else {
                        stmt.close();
                     }
                  }

               }
            } catch (Throwable var35) {
               var3 = var35;
               throw var35;
            } finally {
               if (conn != null) {
                  if (var3 != null) {
                     try {
                        conn.close();
                     } catch (Throwable var31) {
                        var3.addSuppressed(var31);
                     }
                  } else {
                     conn.close();
                  }
               }

            }
         } catch (SQLException var37) {
            return new ArrayList();
         }
      }
   }

   public void erasePetData(Player p, String uuid) {
      try {
         Connection conn = this.connection;
         Throwable var4 = null;

         try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM kojiSBPets WHERE uuid = ?;");
            Throwable var6 = null;

            try {
               stmt.setString(1, uuid);
               List list = (List)this.pets.getOrDefault(p, new ArrayList());
               list.remove(uuid);
               this.pets.put(p, list);
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
}
