package com.api1;

import com.bl.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LocationsRange extends HttpServlet
{
   @Override
   public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException
   {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      String   result      =  "";

      String   uri         =  request.getRequestURI().toString().toLowerCase();
      String   splitURI[]  =  uri.split( "/" );
      if( splitURI != null && splitURI.length > 7 )
      {
         String   sn          =  "";
                  sn          =  splitURI[ 5 ];
         String   startDate   =  "";
                  startDate   =  splitURI[ 6 ];
         String   endDate  =  "";
                  endDate  =  splitURI[ 7 ];

         if(   sn != null && !sn.equals( "" ) && 
               startDate != null && !startDate.equals( "" ) && 
               endDate != null && !endDate.equals( "" ) )
         {
            try
            {
               long           validSn        =  Long.parseLong( sn );
               Date           validStartDate =  new SimpleDateFormat( "yyyy-MM-dd@HH:mm:ss" ).parse( startDate );
               Date           validEndDate   =  new SimpleDateFormat( "yyyy-MM-dd@HH:mm:ss" ).parse( endDate   );
               long diff = validEndDate.getTime() - validStartDate.getTime();
               int diffHours = (int) (diff / (60 * 60 * 1000));
               System.out.println(">>>>>> diffHours="+diffHours);

               if( diffHours <= 6 )
               {
                  StringBuilder  sql            =  new StringBuilder("");
                  sql.append( "select * from gps_data where " );
                  sql.append( "serial_number = '"                 ).append( sn         ).append( "' and " );
                  sql.append( "location_stamp >= CONVERT_TZ(STR_TO_DATE('"   ).append( startDate  ).append( "', '%Y-%m-%d@%H:%i:%s'),'-06:00','+00:00') and " );
                  sql.append( "location_stamp <= CONVERT_TZ(STR_TO_DATE('"   ).append( endDate    ).append( "', '%Y-%m-%d@%H:%i:%s'),'-06:00','+00:00')" );
                  Vector      records  =  DB.getData( sql.toString() );
                  int         total    =  records.size();
                  GPSEvents   events   =  new GPSEvents();
                  for( int i = 0; i < total; i++ )
                  {
                     HashMap record = (HashMap) records.elementAt(i);
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
                     events.addEvent( event );
                  }
                  Gson gson   =  new Gson();
                  result      =  gson.toJson( events );
                  result      =  result.replace( "\\u003d", "=" );
               }
               else
               {
                  result = jsonError( "no more than 6 hours period allowed" );
               }
            }
            catch( Exception e )
            {
               e.printStackTrace();
               result = jsonError( "incorrect data" );
            }            
         }
         else
         {
            result = jsonError( "missing data" );
         }
      }
      else
      {
         result = jsonError( "missing data" );
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



