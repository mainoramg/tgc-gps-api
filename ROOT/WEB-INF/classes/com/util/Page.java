package com.util;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class Page
{
   public Vector   data     = null;
   public String   sql      =  null;
   public int      count    =  0;
   public int      nbPage   =  0;
   public int      perPage  =  0;
   public int      pageNum  =  0;
   
   
   public Page( String sql, int perPage, int pageNum )
   {
      this.sql       = sql;
      this.perPage   = perPage;
      this.pageNum   = pageNum;
      DB.getData( this );
   }
}
 
