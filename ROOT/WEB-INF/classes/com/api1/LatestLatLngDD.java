package com.api1;

import com.bl.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LatestLatLngDD extends HttpServlet
{
   @Override
   public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException
   {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.addHeader("Access-Control-Allow-Origin", "*");
      response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
      response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
      response.addHeader("Access-Control-Max-Age", "1728000");

      String   result      =  "";

      String   uri         =  request.getRequestURI().toString().toLowerCase();
      String   splitURI[]  =  uri.split( "/" );
      if( splitURI != null && splitURI.length > 5 )
      {
         String   sn    =  "";
                  sn    =  splitURI[ 5 ];
         String   limit =  "1";
         if( splitURI.length > 6 )
         {
            limit =  splitURI[ 6 ];
            if( limit != null && !limit.equals( "" ) )
            {
               try
               {
                  int totalRecords = Integer.parseInt( limit );
                  if( totalRecords > 100 )
                  {
                     limit =  "100";
                  }
               }
               catch( Exception e )
               {
                  e.printStackTrace();
                  limit =  "1";
               }
            }
            else
            {
               limit =  "1";
            }
         }
         if( sn != null && !sn.equals( "" ) )
         {
            try
            {
               long              validSn     =  Long.parseLong( sn );
               Vector            records     =  DB.getData( "select latitude_cardinal, latitude_degree, latitude_minutes, latitude_seconds, longitude_cardinal, longitude_degree, longitude_minutes, longitude_seconds, direction from gps_data where serial_number = '" + sn + "' order by location_stamp desc limit " + limit );
               //Vector            records     =  DB.getData( "select latitude_cardinal, latitude_degree, latitude_minutes, latitude_seconds, longitude_cardinal, longitude_degree, longitude_minutes, longitude_seconds, direction from gps_data_test order by stamp desc limit " + limit );
               int               total       =  records.size();
               LocationsLatLngDD locations   =  new LocationsLatLngDD();
               for( int i = 0; i < total; i++ )
               {
                  HashMap           record   =  (HashMap) records.elementAt(i);
                  LocationLatLngDD  location =  new LocationLatLngDD(
                                                record.get("latitude_cardinal").toString(), 
                                                record.get("latitude_degree").toString(), 
                                                record.get("latitude_minutes").toString(), 
                                                record.get("latitude_seconds").toString(), 
                                                record.get("longitude_cardinal").toString(), 
                                                record.get("longitude_degree").toString(), 
                                                record.get("longitude_minutes").toString(), 
                                                record.get("longitude_seconds").toString(), 
                                                record.get("direction").toString()
                                             );
                  locations.addLocation( location );
               }
               Gson gson   =  new Gson();
               result      =  gson.toJson( locations );
               result      =  result.replace( "\\u003d", "=" );
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



