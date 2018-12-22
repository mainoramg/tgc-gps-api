<%@page import="com.util.*, java.util.*"%>
<html>
   <body>
   <%
      HashMap record = DB.getRecord( "select * from gps_data order by stamp desc limit 1" );
   %>
maker: <%=record.get("maker").toString() %><br>
serial_number: <%=record.get("serial_number").toString() %><br>
command: <%=record.get("command").toString() %><br>
location_stamp: <%=record.get("location_stamp").toString() %><br>
status: <%=record.get("status").toString() %><br>
latitude: <%=record.get("latitude").toString() %><br>
latitude_cardinal: <%=record.get("latitude_cardinal").toString() %><br>
latitude_degree: <%=record.get("latitude_degree").toString() %><br>
latitude_minutes: <%=record.get("latitude_minutes").toString() %><br>
latitude_seconds: <%=record.get("latitude_seconds").toString() %><br>
longitude: <%=record.get("longitude").toString() %><br>
longitude_cardinal: <%=record.get("longitude_cardinal").toString() %><br>
longitude_degree: <%=record.get("longitude_degree").toString() %><br>
longitude_minutes: <%=record.get("longitude_minutes").toString() %><br>
longitude_seconds: <%=record.get("longitude_seconds").toString() %><br>
speed: <%=record.get("speed").toString() %><br>
direction: <%=record.get("direction").toString() %><br>
vehicle_status: <%=record.get("vehicle_status").toString() %><br>
net_mcc: <%=record.get("net_mcc").toString() %><br>
net_mnc: <%=record.get("net_mnc").toString() %><br>
net_lac: <%=record.get("net_lac").toString() %><br>
net_cell_id: <%=record.get("net_cell_id").toString() %><br>
   </body>
</html>
