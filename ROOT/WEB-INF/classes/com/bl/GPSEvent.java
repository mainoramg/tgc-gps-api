package com.bl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class GPSEvent
{
   private  String   id;
   private  String   maker;
   private  String   serialNumber;
   private  String   command;
   private  Date     timeStamp;
   private  String   status; //Effective mark of data, 'A' stand of effective, 'V' stand of invalid.
   private  String   latitude; //DDFF.FFFF, DD : Degree(00 ~ 90), FF.FFFF : minute (00.0000 ~ 59.9999), keep four decimal places.
   private  String   latitudeCardinal; //N:north, S:south
   private  String   latitudeDegree; // 00 ~ 90
   private  String   latitudeMinutes; // 00 ~ 59
   private  String   latitudeSeconds; // 00.00 ~ 59.99 
   private  String   longitude; //DDDFF.FFFF, DDD : Degree(000 ~ 180), FF.FFFF : minute
   private  String   longitudeCardinal; //E:east, W:west
   private  String   longitudeDegree; // 000 ~ 180
   private  String   longitudeMinutes; // 00 ~ 59
   private  String   longitudeSeconds; // 00.00 ~ 59.99 
   private  String   speed;   //Range of 000.00 ~ 999.99 knots, Keep two decimal places.
   private  String   direction;  //Azimuth, north to 0 degrees, resolution 1 degrees, clockwise direction.
   private  String   vehicleStatus;
   private  String   netMcc;
   private  String   netMnc;
   private  String   netLac;
   private  String   netCellId;
   private  String   googleMapsLink;

   public GPSEvent()
   {
      this.id                 =  UUID.randomUUID().toString();
      this.maker              =  "";
      this.serialNumber       =  "";
      this.command            =  "";
      this.timeStamp          =  new Date();
      this.status             =  "E";
      this.latitude           =  "";
      this.latitudeCardinal   =  "";
      this.latitudeDegree     =  "";
      this.latitudeMinutes    =  "";
      this.latitudeSeconds    =  "";
      this.longitude          =  "";
      this.longitudeCardinal  =  "";
      this.longitudeDegree    =  "";
      this.longitudeMinutes   =  "";
      this.longitudeSeconds   =  "";
      this.speed              =  "";
      this.direction          =  "";
      this.vehicleStatus      =  "";
      this.netMcc             =  "";
      this.netMnc             =  "";
      this.netLac             =  "";
      this.netCellId          =  "";
      this.googleMapsLink     =  "";
   }
   public GPSEvent( String data )
   {
      this();
      if( data != null && data.length() >= 2 )
      {
         String[] parts =  data.substring( 1, data.length()-1 ).split(",");
         if( parts.length == 17 )
         {
            this.maker              =  parts[0];   //*HQ
            this.serialNumber       =  parts[1];   //9170670374
            this.command            =  parts[2];   //V1
            this.timeStamp          =  getDateFromString( parts[11] + parts[3] );   //  111218 + 233457
            this.status             =  parts[4];   //A
            this.latitude           =  parts[5];   //1000.4295
            this.latitudeCardinal   =  parts[6];   //N
            this.latitudeDegree     =  getLatitudeDegree(   parts[5], parts[6] );
            this.latitudeMinutes    =  getLatitudeMinutes(  parts[5] );
            this.latitudeSeconds    =  getSeconds( parts[5] );
            this.longitude          =  parts[7];   //08416.2627
            this.longitudeCardinal  =  parts[8];   //W
            this.longitudeDegree    =  getLongitudeDegree(  parts[7], parts[8] );
            this.longitudeMinutes   =  getLongitudeMinutes( parts[7] );
            this.longitudeSeconds   =  getSeconds( parts[7] );
            this.speed              =  parts[9];   //000.39
            this.direction          =  parts[10];  //000
            this.vehicleStatus      =  parts[12];  //FFF7BBFF
            this.netMcc             =  parts[13];  //712
            this.netMnc             =  parts[14];  //04
            this.netLac             =  parts[15];  //42003
            this.netCellId          =  parts[16];  //12801#
            this.googleMapsLink     =  getGoogleMapsLink(); //http://maps.google.com/maps?q=+10.00717,-084.27095
         }
      }
   }
   public GPSEvent(  String id, String maker, String serialNumber, String command, String timeStamp, String status, 
                     String latitude, String latitudeCardinal, String latitudeDegree, String latitudeMinutes,
                     String latitudeSeconds, String longitude, String longitudeCardinal, String longitudeDegree, 
                     String longitudeMinutes, String longitudeSeconds, String speed, String direction,
                     String vehicleStatus, String netMcc, String netMnc, String netLac, String netCellId )
   {
      this.id                 =  id;
      this.maker              =  maker;
      this.serialNumber       =  serialNumber;
      this.command            =  command;
      this.timeStamp          =  getDateFromStringDB( timeStamp );
      this.status             =  status;
      this.latitude           =  latitude;
      this.latitudeCardinal   =  latitudeCardinal;
      this.latitudeDegree     =  latitudeDegree;
      this.latitudeMinutes    =  latitudeMinutes;
      this.latitudeSeconds    =  latitudeSeconds;
      this.longitude          =  longitude;
      this.longitudeCardinal  =  longitudeCardinal;
      this.longitudeDegree    =  longitudeDegree;
      this.longitudeMinutes   =  longitudeMinutes;
      this.longitudeSeconds   =  longitudeSeconds;
      this.speed              =  speed;
      this.direction          =  direction;
      this.vehicleStatus      =  vehicleStatus;
      this.netMcc             =  netMcc;
      this.netMnc             =  netMnc;
      this.netLac             =  netLac;
      this.netCellId          =  netCellId;
      this.googleMapsLink     =  getGoogleMapsLink();
   }

   protected String getLatitudeDegree( String latitude, String cardinal )
   {
      String   result   =  "";
      String[] parts    =  latitude.split( "\\." );
      if( parts.length == 2 )
      {
         if( parts[0].length() == 4 )
         {
            result = parts[0].substring( 0, 2 );
            if( cardinal.toUpperCase().equals( "S" ) )
            {
               result = "-" + result;
            }
         }
      }
      return result;
   }

   protected String getLatitudeMinutes( String latitude )
   {
      String   result   =  "";
      String[] parts    =  latitude.split( "\\." );
      if( parts.length == 2 )
      {
         if( parts[0].length() == 4 )
         {
            result = parts[0].substring( 2, 4 );
         }
      }
      return result;
   }

   protected String getSeconds( String decimal )
   {
      String   result   =  "0";
      String[] parts    =  decimal.split( "\\." );
      if( parts.length == 2 )
      {
         if( parts[1].length() == 4 )
         {
            double seconds = 0.0d;
            try
            {
               DecimalFormat  df =  new DecimalFormat("##.#");
               seconds  =  Double.parseDouble( "0." + parts[1] + "d" );
               result   =  df.format( seconds * 60 );
            }
            catch(Exception e){}
         }
      }
      return result;
   }

   protected String getLongitudeDegree( String longitude, String cardinal )
   {
      String   result   =  "";
      String[] parts    =  longitude.split( "\\." );
      if( parts.length == 2 )
      {
         if( parts[0].length() == 5 )
         {
            result = parts[0].substring( 0, 3 );
            if( cardinal.toUpperCase().equals( "W" ) )
            {
               result = "-" + result;
            }
         }
      }
      return result;
   }

   protected String getLongitudeMinutes( String longitude )
   {
      String   result   =  "";
      String[] parts    =  longitude.split( "\\." );
      if( parts.length == 2 )
      {
         if( parts[0].length() == 5 )
         {
            result = parts[0].substring( 3, 5 );
         }
      }
      return result;
   }


   public String getStatus()
   {
      return this.status;
   }

   public String getQueryToSaveIntoDB( String rawId )
   {
      StringBuilder result = new StringBuilder("");
      result.append( "call gps.parse_raw_data (" );
      result.append( "\""                 ).append( id                                          ).append( "\"," );
      result.append( "\""                 ).append( rawId                                       ).append( "\"," );
      result.append( "\""                 ).append( maker.replace( "\"", "\\\"" )               ).append( "\"," );
      result.append( "\""                 ).append( serialNumber.replace( "\"", "\\\"" )        ).append( "\"," );
      result.append( "\""                 ).append( command.replace( "\"", "\\\"" )             ).append( "\"," );
      result.append( "STR_TO_DATE('"      ).append( dateToMysqlTimestampString( timeStamp )     ).append( "','%Y-%m-%d %H:%i:%s'),");
      result.append( "\""                 ).append( status.replace( "\"", "\\\"" )              ).append( "\"," );
      result.append( "\""                 ).append( latitude.replace( "\"", "\\\"" )            ).append( "\"," );
      result.append( "\""                 ).append( latitudeCardinal.replace( "\"", "\\\"" )    ).append( "\"," );
      result.append( ""                   ).append( latitudeDegree                              ).append( "," );
      result.append( ""                   ).append( latitudeMinutes                             ).append( "," );
      result.append( ""                   ).append( latitudeSeconds                             ).append( "," );
      result.append( "\""                 ).append( longitude.replace( "\"", "\\\"" )           ).append( "\"," );
      result.append( "\""                 ).append( longitudeCardinal.replace( "\"", "\\\"" )   ).append( "\"," );
      result.append( ""                   ).append( longitudeDegree                             ).append( "," );
      result.append( ""                   ).append( longitudeMinutes                            ).append( "," );
      result.append( ""                   ).append( longitudeSeconds                            ).append( "," );
      result.append( "\""                 ).append( speed.replace( "\"", "\\\"" )               ).append( "\"," );
      result.append( "\""                 ).append( direction.replace( "\"", "\\\"" )           ).append( "\"," );
      result.append( "\""                 ).append( vehicleStatus.replace( "\"", "\\\"" )       ).append( "\"," );
      result.append( "\""                 ).append( netMcc.replace( "\"", "\\\"" )              ).append( "\"," );
      result.append( "\""                 ).append( netMnc.replace( "\"", "\\\"" )              ).append( "\"," );
      result.append( "\""                 ).append( netLac.replace( "\"", "\\\"" )              ).append( "\"," );
      result.append( "\""                 ).append( netCellId.replace( "\"", "\\\"" )           ).append( "\"" );
      result.append( ")"                  );
      return result.toString(); 
   }

   public String getQueryToRemoveFromDB( String rawId )
   {
      StringBuilder result = new StringBuilder("");
      result.append( "call gps.discard_raw_data (" );
      result.append( "\""  ).append( rawId   ).append( "\"" );
      result.append( ")"   );
      return result.toString(); 
   }

   public String toString()
   {
      StringBuilder result = new StringBuilder("");
      result.append( "{" );
      result.append( "\"id\":\""                ).append( id                     ).append( "\"," );
      result.append( "\"maker\":\""             ).append( maker                  ).append( "\"," );
      result.append( "\"serialNumber\":\""      ).append( serialNumber           ).append( "\"," );
      result.append( "\"command\":\""           ).append( command                ).append( "\"," );
      result.append( "\"timeStamp\":\""         ).append( dateToMysqlTimestampString( timeStamp ) ).append( "\"," );
      result.append( "\"status\":\""            ).append( status                 ).append( "\"," );
      result.append( "\"latitude\":\""          ).append( latitude               ).append( "\"," );
      result.append( "\"latitudeCardinal\":\""  ).append( latitudeCardinal       ).append( "\"," );
      result.append( "\"latitudeDegree\":\""    ).append( latitudeDegree         ).append( "\"," );
      result.append( "\"latitudeMinutes\":\""   ).append( latitudeMinutes        ).append( "\"," );
      result.append( "\"latitudeSeconds\":\""   ).append( latitudeSeconds        ).append( "\"," );
      result.append( "\"longitude\":\""         ).append( longitude              ).append( "\"," );
      result.append( "\"longitudeCardinal\":\"" ).append( longitudeCardinal      ).append( "\"," );
      result.append( "\"longitudeDegree\":\""   ).append( longitudeDegree        ).append( "\"," );
      result.append( "\"longitudeMinutes\":\""  ).append( longitudeMinutes       ).append( "\"," );
      result.append( "\"longitudeSeconds\":\""  ).append( longitudeSeconds       ).append( "\"," );
      result.append( "\"speed\":\""             ).append( speed                  ).append( "\"," );
      result.append( "\"direction\":\""         ).append( direction              ).append( "\"," );
      result.append( "\"vehicleStatus\":\""     ).append( vehicleStatus          ).append( "\"," );
      result.append( "\"netMcc\":\""            ).append( netMcc                 ).append( "\"," );
      result.append( "\"netMnc\":\""            ).append( netMnc                 ).append( "\"," );
      result.append( "\"netLac\":\""            ).append( netLac                 ).append( "\"," );
      result.append( "\"netCellId\":\""         ).append( netCellId              ).append( "\"" );
      result.append( "}" );
      return result.toString();   
   }

   public String toJson()
   {
      StringBuilder result = new StringBuilder("");
      result.append( "{" );
      result.append( "\"id\":\""                ).append( id                     ).append( "\"," );
      result.append( "\"maker\":\""             ).append( maker                  ).append( "\"," );
      result.append( "\"serialNumber\":\""      ).append( serialNumber           ).append( "\"," );
      result.append( "\"command\":\""           ).append( command                ).append( "\"," );
      result.append( "\"timeStamp\":\""         ).append( dateToMysqlTimestampString( timeStamp ) ).append( "\"," );
      result.append( "\"status\":\""            ).append( status                 ).append( "\"," );
      result.append( "\"latitude\":\""          ).append( latitude               ).append( "\"," );
      result.append( "\"latitudeCardinal\":\""  ).append( latitudeCardinal       ).append( "\"," );
      result.append( "\"latitudeDegree\":\""    ).append( latitudeDegree         ).append( "\"," );
      result.append( "\"latitudeMinutes\":\""   ).append( latitudeMinutes        ).append( "\"," );
      result.append( "\"latitudeSeconds\":\""   ).append( latitudeSeconds        ).append( "\"," );
      result.append( "\"longitude\":\""         ).append( longitude              ).append( "\"," );
      result.append( "\"longitudeCardinal\":\"" ).append( longitudeCardinal      ).append( "\"," );
      result.append( "\"longitudeDegree\":\""   ).append( longitudeDegree        ).append( "\"," );
      result.append( "\"longitudeMinutes\":\""  ).append( longitudeMinutes       ).append( "\"," );
      result.append( "\"longitudeSeconds\":\""  ).append( longitudeSeconds       ).append( "\"," );
      result.append( "\"speed\":\""             ).append( speed                  ).append( "\"," );
      result.append( "\"direction\":\""         ).append( direction              ).append( "\"," );
      result.append( "\"vehicleStatus\":\""     ).append( vehicleStatus          ).append( "\"," );
      result.append( "\"netMcc\":\""            ).append( netMcc                 ).append( "\"," );
      result.append( "\"netMnc\":\""            ).append( netMnc                 ).append( "\"," );
      result.append( "\"netLac\":\""            ).append( netLac                 ).append( "\"," );
      result.append( "\"netCellId\":\""         ).append( netCellId              ).append( "\"," );
      result.append( "\"gmaps_link\":\""        ).append( getGoogleMapsLink()    ).append( "\"" );
      result.append( "}" );
      return result.toString();   
   }

   public String getGoogleMapsLink()
   {
      //http://maps.google.com/maps?q=+10.00717,-084.27095
      StringBuilder result = new StringBuilder("");
      result.append( "http://maps.google.com/maps?q=" );

      if( this.latitudeCardinal.equals( "N" ) )
      {
         result.append( "+" );
      }
      else if( this.latitudeCardinal.equals( "S" ) )
      {
         result.append( "-" );
      }

      if( latitude.length() >= 2 )
      {
         result.append( latitude.substring( 0, 2 ) ).append( " " ).append( latitude.substring( 2 ) );
      }
   
      result.append( ","      );

      if( this.longitudeCardinal.equals( "E" ) )
      {
         result.append( "+" );
      }
      else if( this.longitudeCardinal.equals( "W" ) )
      {
         result.append( "-" );
      }

      if( longitude.length() >= 2 )
      {
         result.append( longitude.substring( 0, 3 ) ).append( " " ).append( longitude.substring( 3 ) );
      }

      return result.toString();
   }

   protected String dateToMysqlTimestampString( Date date )
   {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      return df.format(date).toString();
   }

   protected Date getDateFromString( String date )
   {
      try
      {
         return new SimpleDateFormat( "ddMMyyHHmmss" ).parse( date );
      }
      catch ( Exception e )
      {
         return null;
      }
   }

   protected Date getDateFromStringDB( String date )
   {
      try
      {
         return new SimpleDateFormat( "MM/dd/yyyy@HH:mm:ss" ).parse( date );
      }
      catch ( Exception e )
      {
         return null;
      }
   }
}