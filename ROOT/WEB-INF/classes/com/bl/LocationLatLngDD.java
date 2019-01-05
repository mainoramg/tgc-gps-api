package com.bl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class LocationLatLngDD
{
   private  String   latitude;
   private  String   longitude;
   private  String   angle;

   public LocationLatLngDD(   String latitudeCardinal, String latitudeDegree, String latitudeMinutes, String latitudeSeconds, 
                              String longitudeCardinal, String longitudeDegree, String longitudeMinutes, String longitudeSeconds, 
                              String angle )
   {
      this.latitude  =  getLatLonDD( latitudeCardinal, latitudeDegree, latitudeMinutes, latitudeSeconds );
      this.longitude =  getLatLonDD( longitudeCardinal, longitudeDegree, longitudeMinutes, longitudeSeconds );
      this.angle     =  angle;
   }

   protected String getLatLonDD( String cardinal, String degree, String minutes, String seconds )
   {
      String   result   =  "";
      try
      {
         double dd = Double.parseDouble(degree.replace("-","")) + Double.parseDouble(minutes)/60 + Double.parseDouble(seconds)/(60*60);
         DecimalFormat  df =  new DecimalFormat("###.######");
         result   =  df.format( dd );
         if( cardinal.toUpperCase().equals("S") || cardinal.toUpperCase().equals("W") )
         {
            result = "-" + result;
         }
      }
      catch ( Exception e )
      {
         result   =  "";
      }
      return result;
   }
}