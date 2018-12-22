package com.util;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class RoundWheelConnection
{
   private String      id;
   private Connection  conn;
   private long        access;
   private long        created;
   
   RoundWheelConnection( Connection conn )
   {
      this.id       = "" + System.currentTimeMillis();
      this.conn     = conn;
      this.created  = System.currentTimeMillis();
      this.access   = System.currentTimeMillis();
   }
   public String      getId()         { return id;   }
   public Connection  getConn()       { return conn; }
   public void        setAccess()     { this.access = System.currentTimeMillis(); }
   public int         getAgeCreated() { return (int)((System.currentTimeMillis() - created)/1000); }
   public int         getAgeAccess()  { return (int)((System.currentTimeMillis() - access)/1000);  }
   
   
   public String toString()
   {
      return "<span style='color:"+ (isOpen()?"blue":"red") +"'>" + getAgeCreated() + " / " + getAgeAccess() + "</span>";
   }
   
   
   public void ghostClose() throws Exception   {      conn.close();   }
   public boolean isOpen()
   {
      try
      {
         return !conn.isClosed();
      }
      catch( Exception e )
      {
         return false;
      }
   }
}
