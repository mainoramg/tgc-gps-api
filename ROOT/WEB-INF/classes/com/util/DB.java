package com.util;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class DB
{
   public  static ConcurrentHashMap FREE_POOL      =  new ConcurrentHashMap();
   public  static ConcurrentHashMap USED_POOL      =  new ConcurrentHashMap();
   private static int               MAX_AGE        =  1800;   // 30 minutes

   static String            URL       = "jdbc:mysql://localhost:3306/gps?useSSL=false";
   static String            REP_URL   = "jdbc:mysql://localhost:3306/gps?useSSL=false";
   static String            USER      = "gpsuser";
   static String            PASSWORD  = "Luc4s2017";
   
   /*-------------------------------------------------------------------------*/
   /*---   getConnection ...                                               ---*/
   /*-------------------------------------------------------------------------*/
   synchronized public static RoundWheelConnection getConnection( String key )
   {
      return ((RoundWheelConnection) FREE_POOL.get( key ));
   }
   
   synchronized private static RoundWheelConnection getConnection() throws Exception
   {
      Log.log( Log.INFO, "Grab Connection" );
      RoundWheelConnection conn  = null;
      Iterator             it    = FREE_POOL.keySet().iterator();
      if( it.hasNext() )
      {
         conn = (RoundWheelConnection) FREE_POOL.get(it.next());
         if( !conn.isOpen() )
         {
            Log.log( Log.INFO, "Connection was Closed" );
            FREE_POOL.remove( conn.getId() );
            return getConnection();
         }
         else
         if( conn.getAgeAccess() > MAX_AGE )
         {
            Log.log( Log.INFO, "Connection TOO OLD" );
            closeConn( conn, true );
            return getConnection();
         }
         else
         {
            conn.setAccess();
            USED_POOL.put(    conn.getId(), conn );
            FREE_POOL.remove( conn.getId()       );
         }
      }
      else
      {
         try
         {
            Class.forName( "com.mysql.jdbc.Driver" );
            Log.log( Log.INFO, "Hard DB Connection" );
            conn = new RoundWheelConnection( DriverManager.getConnection( URL, USER, PASSWORD ) );
            USED_POOL.put( conn.getId(), conn );
         }
         catch( Exception e )
         {
            //try { Mail.logError( (Exception) e, "RoundWheelConnection" ); } catch( Exception ex ) {}
         }
      }
      return conn;
   }
   
   /*-------------------------------------------------------------------------*/
   /*---   closeConn ...                                                   ---*/
   /*-------------------------------------------------------------------------*/
   synchronized private static void closeConn( RoundWheelConnection conn, boolean forReal )
   {
      if( forReal )
      {
         Log.log( Log.INFO, "Close Hard Connection" );
         try { conn.getConn().close();                } catch( Exception e ) {}
         try { USED_POOL.remove( conn.getId() );      } catch( Exception e ) {}
      }
      else
      {
         try { FREE_POOL.put( conn.getId(), conn );   } catch( Exception e ) {}
         try { USED_POOL.remove( conn.getId() );      } catch( Exception e ) {}
      }
   }

   /*-------------------------------------------------------------------------*/
   /*---   executeSQL ...                                                  ---*/
   /*-------------------------------------------------------------------------*/
   public static int executeSQL( String sql ) throws Exception
   {
      RoundWheelConnection rwConn = null;
      int r = 0;
      try
      {
         rwConn = getConnection();
         Connection c = rwConn.getConn();
         r = executeSQL( c, sql );
      }
      catch( Exception e )
      {
         try { Log.log( Log.ERROR, "executeSQL : " + sql ); } catch( Exception me ) {}
         try { Log.log( e ); }                                catch( Exception me ) {}
         //try { Mail.logError( (Exception) e, sql ); }         catch( Exception le ) {}
         r = -1;
      }
      finally
      {
         closeConn( rwConn, false );
      }
      return r;
   }
   
   /*-------------------------------------------------------------------------*/
   /*---   executeSQL ...                                                  ---*/
   /*-------------------------------------------------------------------------*/
   public static int executeSQL( Connection conn, String sql ) throws Exception
   {
      Statement  stmt = null;
      int        r    = 0;
      try
      {
         stmt  = conn.createStatement();
         Log.log( Log.INFO, (new StringBuffer("SQL : ")).append(sql).toString() );
         stmt.executeUpdate( sql );
      }
      catch( Exception e )
      {
         //Mail.logError( (Exception) e, sql );
         Log.log( e );
      }
      finally
      {
         try { stmt.close(); } catch( Exception e ) { Log.log( e ); }
         return r;
      }
   }

   /*-------------------------------------------------------------------------*/
   /*---   rs2Hash ...                                                     ---*/
   /*-------------------------------------------------------------------------*/
   private static HashMap rs2Hash( ResultSet rs ) throws Exception
   {
      ResultSetMetaData md   = rs.getMetaData();
      int               cols = md.getColumnCount();
      HashMap           hm   = new HashMap();
      for( int j=0; j<cols; j++ )
      {
         String colType = md.getColumnTypeName(j+1);
         String colLbl  = md.getColumnLabel(j+1);
         String colVal  = "";
         
         if( colType.equalsIgnoreCase("date") || colType.equalsIgnoreCase("datetime") )
         {
            StringBuffer tmp = new StringBuffer( "" );
            if( rs.getString( colLbl ) == null )
               colVal = "  /  /    ";
            else
            {
               colVal = (rs.getDate( colLbl )).toString();
               String stk[] = colVal.split( "-" );
               tmp.append(stk[1]).append( "/" ).append( stk[2] ).append( "/" ).append( stk[0] );
            }
            if( colType.equalsIgnoreCase("datetime") )
            {
               try { tmp.append( "@" ).append( rs.getTime( colLbl ).toString() ); } catch( Exception e ) {}
            }
            colVal = tmp.toString();
         }
         else
         {
            colVal  = rs.getString( colLbl );
            colVal  = (colVal==null)?"":colVal.trim();
         }
         hm.put( colLbl, colVal );
      }
      return hm;
   }

   /*-------------------------------------------------------------------------*/
   /*---   getData ...                                                     ---*/
   /*-------------------------------------------------------------------------*/
   public static Vector getData( ResultSet rs ) throws Exception
   {
      Vector  v  = new Vector();
      int   cnt  = 0;
      while( rs.next() && cnt < 2000 )
      {
         v.addElement( rs2Hash(rs) );
         cnt++;
      }
      try { while( rs.next() ) ; } catch( Exception e ) {}
      return v;
   }
   
   /*-------------------------------------------------------------------------*/
   /*---   getData ...                                                     ---*/
   /*-------------------------------------------------------------------------*/
   public static Vector getData( Connection conn, String sql ) throws Exception
   {
      Vector     v = new Vector();
      Statement  stmt = null;
      ResultSet  rs   = null;
      try
      {
         //Log.log( Log.INFO, (new StringBuffer("SQL : ")).append(sql).toString() );
         stmt  = conn.createStatement();
         rs    = stmt.executeQuery( sql );
         v     = getData( rs );
      }
      catch( Exception e )
      {
         //Mail.logError( (Exception) e, sql );
         v = new Vector();
         Log.log( e );
      }
      finally
      {
         try { rs.close();   } catch( Exception e ) { Log.log( e ); }
         try { stmt.close(); } catch( Exception e ) { Log.log( e ); }
      }
      return v;
   }

   /*-------------------------------------------------------------------------*/
   /*---   getData ...                                                     ---*/
   /*-------------------------------------------------------------------------*/
   public static Vector getData( String sql )
   {
      return getData( sql, false );
   }

   public static Vector getData( String sql, boolean fromProd )
   {
      Vector v = null;
      if( fromProd )
      {
         RoundWheelConnection rwConn = null;
         try
         {
            rwConn       = getConnection();
            Connection c = rwConn.getConn();
            v = getData( c, sql );
         }
         catch( Exception e )
         {
            //try { Mail.logError( (Exception) e, sql ); } catch( Exception me ) {}
            try { Log.log( Log.ERROR, sql ); }           catch( Exception le ) {}
            try { Log.log( e ); }                        catch( Exception le ) {}
            v = new Vector();
         }
         finally
         {
            closeConn( rwConn, false );
         }
      }
      else
      {
         Connection conn = null;
         try
         {
            Class.forName( "com.mysql.jdbc.Driver" );
            conn = DriverManager.getConnection( REP_URL, USER, PASSWORD );
            v    = getData( conn, sql );
         }
         catch( Exception e )
         {
            System.out.println( "-----------------\n--- SQL ERROR ---\n-----------------" + sql + "\n" );
         }
         finally
         {
            try { conn.close(); } catch( Exception ignore ) {}
         }
      }
      return v;
   }


   /*-------------------------------------------------------------------------*/
   /*---   getData ...                                                     ---*/
   /*-------------------------------------------------------------------------*/
   public static void getData( Page pg )
   {
      RoundWheelConnection rwConn   = null;
      Statement            stmt     = null;
      ResultSet            rs       = null;
      pg.data = new Vector();

      try
      {
         Log.log( Log.INFO, (new StringBuffer("SQL : ")).append(pg.sql).toString() );
         
         rwConn       = getConnection();
         Connection c = rwConn.getConn();
         stmt  = c.createStatement();
         rs    = stmt.executeQuery( pg.sql );
         
         rs.last();
         pg.count    = rs.getRow();
         pg.nbPage   = (int) (pg.count/pg.perPage);
         int jump    = (pg.pageNum-1)*pg.perPage + 1;
         int cnt     = 0;
         rs.absolute( jump );
         while( cnt < pg.perPage )
         {
            HashMap record = rs2Hash( rs );
            pg.data.addElement( record );
            if( !rs.next() )
               break;
            cnt++;
         }
      }
      catch( Exception e )
      {
         //Mail.logError( (Exception) e, pg.sql );
         Log.log( e );
      }
      finally
      {
         try { rs.close();   } catch( Exception e ) { Log.log( e ); }
         try { stmt.close(); } catch( Exception e ) { Log.log( e ); }
         closeConn( rwConn, false );
      }
   }
   
   /*-------------------------------------------------------------------------*/
   /*---   getRecord ...                                                   ---*/
   /*-------------------------------------------------------------------------*/
   public static HashMap getRecord( String sql )
   {
      return  getRecord( sql, false );
   }
   public static HashMap getRecord( String sql, boolean fromProd )
   {
      try
      {
         Vector data = getData(sql, fromProd);
         if( data.isEmpty() )
         return (new HashMap());
         else
         return (HashMap) data.elementAt(0);
      }
      catch( Exception e )
      {
         //Mail.logError( (Exception) e, sql );
         Log.log( e );
         return (new HashMap());
      }
   }
   
   /*-------------------------------------------------------------------------*/
   /*---   currentMySQLDB ...                                              ---*/
   /*-------------------------------------------------------------------------*/
   public static String currentMySQLDB()
   {
      try
      {
         return (String) DB.getRecord( "select DATABASE() db" ).get( "db" );
      }
      catch( Exception e )
      {
         Log.log( e );
         return "";
      }
   }
   
   public static String showMe()
   {
      String   rF  = "";
      String   rU  = "";
      Iterator it  = null;
      
      it = FREE_POOL.keySet().iterator();
         while( it.hasNext() )
      rF += FREE_POOL.get(it.next()).toString();
      
      
      it = USED_POOL.keySet().iterator();
      while( it.hasNext() )
         rU += USED_POOL.get(it.next()).toString();
      String txt = "";
      txt += "<table border=1><tr><td nowrap valign='top'>POOL</td><td nowrap valign='top'>IN USE</td></tr>";
      txt += "<tr><td nowrap valign='top'>"+rF+"&nbsp;</td><td nowrap valign='top'>"+rU+"&nbsp;</td></tr>";
      txt += "</table>";
      return txt;
   }

   public static void reset()
   {
      FREE_POOL = new ConcurrentHashMap();
      USED_POOL = new ConcurrentHashMap();
   }

   public static HashMap getDataHash( String sql, String key, String val )
   {
      Vector  v = DB.getData(sql);
      HashMap h = new HashMap();
      if( v.size()==0 )
         return h;
      for( int i=0; i<v.size(); i++ )
      {
         HashMap hm = (HashMap) v.elementAt(i);
         h.put( hm.get(key), hm.get(val) );
      }
      return h;
   }
   
   /*-------------------------------------------------------------------------*/
   /*---   getHashData ...                                                 ---*/
   /*-------------------------------------------------------------------------*/
   public static HashMap getHashData( String sql, String key, String val )
   {
      Vector  v = getData(sql);
      HashMap h = new HashMap();
      if( v.size()==0 )
         return h;
      for( int i=0; i<v.size(); i++ )
      {
         HashMap hm = (HashMap) v.elementAt(i);
         h.put( hm.get(key), hm.get(val) );
      }
      return h;
   }
}
