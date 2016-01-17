importClass(org.maxgamer.rs.module.people.select.GroundSelector);
importClass(org.maxgamer.rs.module.people.select.ItemSelector);
importClass(org.maxgamer.rs.module.people.select.NPCSelector);
importClass(org.maxgamer.rs.module.people.select.ObjectSelector);

var body;
var selected;

function objects(){
	selected = new ObjectSelector(body, 20);
	return selected;
}

function items(){
	selected = new ItemSelector(body.getInventory());
	return selected;
}

function equipment(){
	selected = new ItemSelector(body.getEquipment());
	return selected;
}

function bank(){
	selected = new ItemSelector(body.getBank());
	return selected;
}

function npcs(){
	selected = new NPCSelector(body, 20);
	return selected;
}

function ground(){
	selected = new GroundSelector(body, 20);
	return selected;
}

function selected(){
	return selected;
}