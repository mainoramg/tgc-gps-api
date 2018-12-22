<html>
   <body>
   <h1>TEST FILE</h1>
      Request: <%= request.getRequestURI() %><br>
      Protocol: <%= request.getProtocol() %><br>
      Protocol custom: <%= request.getHeader("x-server-proto") %><br>
      <h4>Remote</h4>
      Host: <%= request.getRemoteHost() %><br>
      Address: <%= request.getRemoteAddr() %><br>
      Scheme: <%= request.getScheme() %><br>
      test for deployment script API
   </body>
</html>