/* shutdown.js
 * 
 * This script is run when the server has been requested to be shut down, but before
 * any players have been kicked.  It might allow some cleanup, and is a counterpart
 * to startup.js.  If a suspension is thrown here, the script may or may not continue.
 * That is undefined behaviour.  This is run once. It should not suspend.
 * 
 * javascripts/lib/core.js is automatically imported in the source.
 */


importClass(org.maxgamer.rs.util.log.Log);

Log.info("shutdown.js running!");