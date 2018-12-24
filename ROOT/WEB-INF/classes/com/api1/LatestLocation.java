package com.api1;

import com.bl.GPSEvent;
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

      String   result      =  "";

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
               GPSEvent event = new GPSEvent(
                                             record.get("id").toString(), 
                                             record.get("maker").toString(), 
                                             record.get("serial_number").toString(), 
                                             record.get("command").toString(), 
                                             record.get("location_stamp").toString(), 
                                             record.get("status").toString(), 
                                             record.get("latitude").toString(), 
                                             record.get("latitude_cardinal").toString(), 
                                             record.get("latitude_degree").toString(), 
                                             record.get("latitude_minutes").toString(), 
                                             record.get("latitude_seconds").toString(), 
                                             record.get("longitude").toString(), 
                                             record.get("longitude_cardinal").toString(), 
                                             record.get("longitude_degree").toString(), 
                                             record.get("longitude_minutes").toString(), 
                                             record.get("longitude_seconds").toString(), 
                                             record.get("speed").toString(), 
                                             record.get("direction").toString(), 
                                             record.get("vehicle_status").toString(), 
                                             record.get("net_mcc").toString(), 
                                             record.get("net_mnc").toString(), 
                                             record.get("net_lac").toString(), 
                                             record.get("net_cell_id").toString()
                                          );
               result = event.toJson();
            }
            catch( Exception e )
            {
               e.printStackTrace();
               result = jsonError( "incorrect format for SN" );
            }            
         }
         else
         {
            result = jsonError( "missing SN" );
         }
      }
      else
      {
         result = jsonError( "missing SN" );
      }

      PrintWriter out = response.getWriter();
      out.print( result );
      out.flush();
   }
   
   protected String jsonError( String error )
   {
      StringBuilder result = new StringBuilder("");
      result.append( "{" );
      result.append( "\"error\":\"" ).append( error ).append( "\"" );
      result.append( "}" );
      return result.toString();
   }
}



