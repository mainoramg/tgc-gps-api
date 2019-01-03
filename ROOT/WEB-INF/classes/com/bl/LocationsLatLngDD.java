package com.bl;

import java.util.ArrayList;
import java.util.List;

public class LocationsLatLngDD
{
   private List<LocationLatLngDD> locations;

   public LocationsLatLngDD()
   {
      this.locations =  new ArrayList<LocationLatLngDD>();
   }
   
   public void addLocation( LocationLatLngDD location )
   {
      this.locations.add( location );
   }
}