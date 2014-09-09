<!DOCTYPE html>

<html xml:lang="${cmsfn.language()}" lang="${cmsfn.language()}" class="no-js">

<head>
   <!--[if IE]>
   <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=IE7" />
   <![endif]-->
   [@cms.init /]
   [@cms.area name="headerScripts"/]
</head>

<body>
    [@cms.area name="bodyBeginScripts"/]
    <div id="wrapper">

        [@cms.area name="main"/]
    </div><!-- end wrapper -->
    [@cms.area name="bodyEndScripts"/]
</body>
</html>