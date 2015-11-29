/* These are implicitly set later by the JSFiber */
var fiber;

importClass(org.maxgamer.rs.model.javascript.JSUtil);

function include(path){
	fiber.include(path);
}

function wait(ticks){
	JSUtil.wait(fiber, ticks);
}

function move(mob, dest, block){
	block = block || true;
	
	JSUtil.move(fiber, mob, dest, block);
}
