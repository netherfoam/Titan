/* This is implicitly set later by the JSFiber */
var fiber;

/* These are very common classes which are imported automatically */
importClass(org.maxgamer.rs.core.Core);
importClass(org.maxgamer.rs.model.map.Location);
importClass(org.maxgamer.rs.model.item.ItemStack);
importClass(org.maxgamer.rs.model.skill.SkillType);
importClass(org.maxgamer.rs.model.action.Action);

// /* These imports are required by this file *
importClass(org.maxgamer.rs.model.javascript.JSUtil);
importClass(org.maxgamer.rs.lib.Erratic);

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

function animate(mob, anim, priority){
	priority = priority || 5;

	JSUtil.animate(fiber, mob, anim, priority);
}

function random(max){
	return Erratic.nextInt(max);
}