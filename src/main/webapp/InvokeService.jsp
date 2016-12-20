<html>
<head><title>Rest Client</title>
<script>

function callService(){
document.getElementsByName("restinvoke").submit(); 

}
</script>

</head>
<body>

  <form  name="restinvoke" method="post" action="RestClient" onsubmit="callService()" >
   </br>URL: <input id="resourceAddress" name="resourceAddress" type="text" value=""/>
   </br>Input File Path: <input id="filePath" name="filePath" type="text">    
    </br> Content-Type: <input id="contentType" name="contentType" type="text">
    <input type="submit" value="Call Rest API"/>
  </form>
</body>
</html>