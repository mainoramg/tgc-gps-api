package com.bl;

import java.util.ArrayList;
import java.util.List;

public class GPSEvents
{
   private List<GPSEvent> events;

   public GPSEvents()
   {
      this.events =  new ArrayList<GPSEvent>();
   }
   
   public void addEvent( GPSEvent event )
   {
      this.events.add( event );
   }
}