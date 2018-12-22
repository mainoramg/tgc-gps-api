package com.util;


import java.io.*;
import java.util.*;

public class Log
{
   public final static int CURRENT_SEVERITY     = 0;
   
   public final static int EXCEPTION            = 2;
   public final static int ERROR                = 1;
   public final static int INFO                 = 0;
   
   private static String[] errMessage = { "INFO", "ERROR", "EXCEPTION" };
   
   public static void log( int severity, String content )
   {
      if( CURRENT_SEVERITY > severity )
         return;
      System.out.print( errMessage[severity] );
      System.out.print( " | "  );
      System.out.print( new Date()  );
      System.out.print( " | "  );
      System.out.print( content     );
      System.out.println();
   }
   
   public static void log( Exception e )
   {
      log( EXCEPTION, printStackTrace(e) );
   }
   
   public static String printStackTrace( Exception e )
   {
      StringWriter sw = new StringWriter();
      e.printStackTrace( new PrintWriter(sw) );
      return sw.toString();
   }
}
