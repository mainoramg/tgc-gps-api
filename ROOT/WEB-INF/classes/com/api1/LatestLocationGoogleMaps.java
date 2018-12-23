package com.api1;

import com.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LatestLocationGoogleMaps extends HttpServlet
{
   @Override
   public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException
   {
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      StringBuilder result = new StringBuilder("");
      result.append( "<html><head></head><body>" );

      String   uri         =  request.getRequestURI().toString().toLowerCase();
      String   splitURI[]  =  uri.split( "/" );
      if( splitURI != null && splitURI.length > 4 )
      {
         String   sn =  "";
                  sn =  splitURI[ 4 ];
         if( sn != null && !sn.equals( "" ) )
         {
            try
            {
               long validSn = Long.parseLong( sn );
               HashMap record = DB.getRecord( "select * from gps_data where serial_number = '" + sn + "' order by stamp desc limit 1" );
               //google maps link:
               result.append( "<h1><a target=\"_blank\" href=\"" ).append( "http://maps.google.com/maps?q=" );
               if( record.get("latitude_cardinal").toString().equals( "N" ) )
               {
                  result.append( "+" );
               }
               else if( record.get("latitude_cardinal").toString().equals( "S" ) )
               {
                  result.append( "-" );
               }

               if( record.get("latitude").toString().length() >= 2 )
               {
                  result.append( record.get("latitude").toString().substring( 0, 2 ) ).append( " " ).append( record.get("latitude").toString().substring( 2 ) );
               }
            
               result.append( ","      );

               if( record.get("longitude_cardinal").toString().equals( "E" ) )
               {
                  result.append( "+" );
               }
               else if( record.get("longitude_cardinal").toString().equals( "W" ) )
               {
                  result.append( "-" );
               }

               if( record.get("longitude").toString().length() >= 2 )
               {
                  result.append( record.get("longitude").toString().substring( 0, 3 ) ).append( " " ).append( record.get("longitude").toString().substring( 3 ) );
               }
               result.append( "\">Google Maps Link</a></h1>" );
            }
            catch( Exception e )
            {
               e.printStackTrace();
               result.append( "\"error\":" ).append( "\""  ).append( "incorrect format for SN" ).append( "\"" );
            }            
         }
         else
         {
            result.append( "\"error\":" ).append( "\""  ).append( "missing SN" ).append( "\"" );
         }
         //System.out.println("URI length:" + splitURI.length );
      }
      else
      {
         result.append( "\"error\":" ).append( "\""  ).append( "missing SN" ).append( "\"" );
      }      
      result.append( "</body></html>" );

      PrintWriter out = response.getWriter();
      out.print( result.toString() );
      //out.println("</html>");
      out.flush();
   }
}



