/* startup.js
 * 
 * This script is run once the server has been fully initialized, but before
 * any players have joined.  It might allow a restart to be scheduled at a
 * specific time, or the addition of a custom world without requiring a full
 * module to do so. It is run once. It may suspend.
 * 
 * javascripts/lib/core.js is automatically imported in the source.
 */

importClass(org.maxgamer.rs.util.Log);
importClass(org.maxgamer.rs.core.Core);

//Core.getServer().getCommands().handle(Core.getConsole(), "::jaiarmy 200");
//Core.getServer().getCommands().handle(Core.getConsole(), "::ff");
