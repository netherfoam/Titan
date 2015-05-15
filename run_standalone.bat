:start
java -cp lib/*;bin/ javaagent:lib/quasar-core.jar org.maxgamer.rs.core.RSBootstrap standalone
goto :start