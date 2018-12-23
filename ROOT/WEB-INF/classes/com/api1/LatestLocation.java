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

public class LatestLocation extends HttpServlet
{
   @Override
   public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException
   {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      StringBuilder result = new StringBuilder("");
      result.append( "{" );

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
               result.append( "\"maker\":"               ).append( "\""  ).append( record.get("maker").toString()             ).append( "\"," );
               result.append( "\"serial_number\":"       ).append( "\""  ).append( record.get("serial_number").toString()     ).append( "\"," );
               result.append( "\"command\":"             ).append( "\""  ).append( record.get("command").toString()           ).append( "\"," );
               result.append( "\"location_stamp\":"      ).append( "\""  ).append( record.get("location_stamp").toString()    ).append( "\"," );
               result.append( "\"status\":"              ).append( "\""  ).append( record.get("status").toString()            ).append( "\"," );
               result.append( "\"latitude\":"            ).append( "\""  ).append( record.get("latitude").toString()          ).append( "\"," );
               result.append( "\"latitude_cardinal\":"   ).append( "\""  ).append( record.get("latitude_cardinal").toString() ).append( "\"," );
               result.append( "\"latitude_degree\":"     ).append( "\""  ).append( record.get("latitude_degree").toString()   ).append( "\"," );
               result.append( "\"latitude_minutes\":"    ).append( "\""  ).append( record.get("latitude_minutes").toString()  ).append( "\"," );
               result.append( "\"latitude_seconds\":"    ).append( "\""  ).append( record.get("latitude_seconds").toString()  ).append( "\"," );
               result.append( "\"longitude\":"           ).append( "\""  ).append( record.get("longitude").toString()         ).append( "\"," );
               result.append( "\"longitude_cardinal\":"  ).append( "\""  ).append( record.get("longitude_cardinal").toString()).append( "\"," );
               result.append( "\"longitude_degree\":"    ).append( "\""  ).append( record.get("longitude_degree").toString()  ).append( "\"," );
               result.append( "\"longitude_minutes\":"   ).append( "\""  ).append( record.get("longitude_minutes").toString() ).append( "\"," );
               result.append( "\"longitude_seconds\":"   ).append( "\""  ).append( record.get("longitude_seconds").toString() ).append( "\"," );
               result.append( "\"speed\":"               ).append( "\""  ).append( record.get("speed").toString()             ).append( "\"," );
               result.append( "\"direction\":"           ).append( "\""  ).append( record.get("direction").toString()         ).append( "\"," );
               result.append( "\"vehicle_status\":"      ).append( "\""  ).append( record.get("vehicle_status").toString()    ).append( "\"," );
               result.append( "\"net_mcc\":"             ).append( "\""  ).append( record.get("net_mcc").toString()           ).append( "\"," );
               result.append( "\"net_mnc\":"             ).append( "\""  ).append( record.get("net_mnc").toString()           ).append( "\"," );
               result.append( "\"net_lac\":"             ).append( "\""  ).append( record.get("net_lac").toString()           ).append( "\"," );
               result.append( "\"net_cell_id\":"         ).append( "\""  ).append( record.get("net_cell_id").toString()       ).append( "\"," );

               //google maps link:
               result.append( "\"gmaps_link\":"          ).append( "\""  ).append( "http://maps.google.com/maps?q=" );
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
               result.append( "\"" );
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
      result.append( "}" );

      PrintWriter out = response.getWriter();
      out.print( result.toString() );
      //out.println("</html>");
      out.flush();
   }
}



