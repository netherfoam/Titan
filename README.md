# Titan
Emulates a RuneScape 637 Server using a slightly modified protocol

# Setup
<pre>
Download or clone the project
Install MySQL5 - http://dev.mysql.com/downloads/mysql/
Download a R637 cache - Search Rune-Server.org
</pre>

<pre>
-- Cache --
Place the cache in Project_Root/cache/* such that you have:
Project_Root/
    cache/
        main_cache_file.dat2
        main_cache_file.idx0
        main_cache_file.idx1
        main_cache_file.idx2
        ..etc

The XTEA keys for maps are stored in Project_Root/cache/xteas.xstore2. See the XTEAStore class in the project
for their format.
</pre>

<pre>
-- MySQL --
Install MySQL5 (Other versions may work fine)

I am using Navicat, but if you wish to use another program feel free (Ubuntu: "Emma", Mac/Windows: "MySQL workbench"). They used to have a free MySQL version,
but it is no longer available unless you download it from an "alternative source". There are free alternatives,
such as SQLYog and HeidiSQL.

Via Navicat:
- Download & Install Navicat
- Run Navicat
- File -> New Connection -> MySQL
- Host: localhost
- Port: 3306 (or port selected during MySQL install)
- User: root
- Pass: password selected during MySQL install
- Hit OK
- Open the connection on the left hand side (double click)
- Right click the connection -> New Database
- Name the database, eg 'titan', then OK
- Open the database (double click)
- Ensure 'multiple queries in each execution' is checked
- Right click database -> execute batch file -> select file from Project_Root\sql\dumps\mysql_world.sql -> Start -> Wait
- Repeat above step for file Project_Root\dumps\mysql_logon.sql
- When viewing the database there should be several tables in there, including 'profiles'.
</pre>

<pre>
-- Configuring --
The project is configured using YML files. YML files do not accept tabs, instead use spaces. If a tab is found when running
the server, the config file reader will throw an exception.  

When I refer to '.' in field names for YML files, it means 'the subsection called'. For example, if I had:
formula:
    melee:
        hit:   "some formula"
        power: "some formula"
    range:
        hit:   "some formula"
        power: "some formula"

to refer to 'melee hit formula' I would say 'formula.melee.hit'. Or formula.range.power for range power, etc.


Go to Project_Root\config\ and rename:
logon.yml.dist -> logon.yml
world.yml.dist -> world.sample
default_player.yml.dist -> default_player.yml
(This process will be done automatically if you start the server before hand, but will not have reasonable values for the database config!)

Now you should edit logon.yml change:
- pass to a new password, this is your logon password not your database password. If this is found out by someone, they can modify anyones profile in any manner they like. (Add items, corrupt it, reset it, change rights to admin, set skills to any level, set health, location, equipment) - Basically, make it a strong password!
- database.user to your mysql user (probably root)
- database.pass to your mysql password
- database.database to your database name (eg 'titan')
- database.port to your mysql port (probably 3306)
Save, close.

Now edit world.yml and change:
- database.user to your mysql user (probably root)
- database.pass to your mysql password
- database.database to your database name (eg 'titan')
- database.port to your mysql port (3306 by default)
Save, close.
</pre>

<pre>
-- Compiling --
The project is compiled in accordance with Java/JDK 7.  
Yyou could import the project into Eclipse or some other IDE.
If you wish to import it into Eclipse, you will need to add all .jar files from lib/ to your classpath, if
you don't know how to do this I suggest googling your IDE name with 'classpath setup' to add the .jar files from
Project_Root/lib/.

The project comes with an ANT build.xml install file, so if you have ANT installed, you can compile it through
the command line / terminal interface using:
	$ ant clean (Removes bin/* classes, not always necessary)
	$ ant compile (Builds bin/* classes)
	$ ant jar (Builds titan.jar)
</pre>

<pre>
-- Running --
Two options: 
  - Standalone (Built in logon server)
    Simply run the program like:
      Open command line
      $ cd Project_Root/ (Or whatever location your project is in)
      If you're running from bin/ (Eg not a jar)
      	$ java -cp "lib/*;bin/" -javaagent:lib/quasar-core.jar -Xms768m org.maxgamer.rs.core.RSBootstrap
      Else
      	$ java -jar Titan.jar -javaagent:lib/quasar-core.jar -Xms768m 
      	
      Doing this with the argument 'standalone' will cause the program to start an internal logon server for itself.
      
  - Game server + Logon server
      Open comamnd line
      $ cd Project_Root/ (Or whateverlocation your project is in)
      
      If you're running from bin/ (Eg not a jar)
      	$ java -cp "lib/*;bin/" -javaagent:lib/quasar-core.jar -Xms768m org.maxgamer.rs.core.RSBootstrap game-only
      Else
      	$ java -javaagent:lib/quasar-core.jar -Xms768m -jar Titan.jar game-only
      
      Open comamnd line (A new one)
      $ cd Project_Root/ (Or whateverlocation your project is in)
      
      If you're running from bin/ (Eg not a jar)
      	$ java -cp "lib/*;bin/" -javaagent:lib/quasar-core.jar -Xms768m org.maxgamer.rs.logon.logon.LogonServer
      Else
      	$ java -javaagent:lib/quasar-core.jar -Xms768m -cp "Titan.jar;lib/*" org.maxgamer.rs.logon.logon.LogonServer
      
      You should now see the logon server successfully connect to the game server and visa versa (You should have two
      command windows open)
</pre>

<pre>
-- Connection --
Download a client and connect to localhost. The servers port is available in the config file in world.yml under
world.port: xxxx where the default is 43594. The cache will download automatically. The server does handle clients with RSA enabled (if using the correct keys/tokens obviously). Also if RSA is disabled the server will work. 
</pre>
