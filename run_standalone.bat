:start
java -Dco.paralleluniverse.fibers.verifyInstrumentation -Xms768m -cp "lib/*;bin/" -javaagent:lib/quasar-core.jar org.maxgamer.rs.core.RSBootstrap standalone
goto :start