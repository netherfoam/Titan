importClass(org.maxgamer.rs.module.people.select.GroundSelector);
importClass(org.maxgamer.rs.module.people.select.ItemSelector);
importClass(org.maxgamer.rs.module.people.select.NPCSelector);
importClass(org.maxgamer.rs.module.people.select.ObjectSelector);

var body;
var selected;

function objects(radius){
	radius = radius || 5;
	selected = new ObjectSelector(body, radius);
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
	radius = radius || 5;
	selected = new ItemSelector(body.getBank());
	return selected;
}

function npcs(radius){
	radius = radius || 5;
	selected = new NPCSelector(body, radius);
	return selected;
}

function grounds(radius){
	radius = radius || 5;
	selected = new GroundSelector(body, radius);
	return selected;
}

function selected(){
	return selected;
}