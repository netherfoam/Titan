importClass(org.maxgamer.rs.model.item.inventory.GenericContainer);
importClass(org.maxgamer.rs.interfaces.impl.primary.TradeInterface);

function trade(p1, p2){
	var mine = new GenericContainer(Inventory.SIZE, StackType.NORMAL);
	var yours = new GenericContainer(Inventory.SIZE, StackType.NORMAL);
	
	var myInterf = new TradeInterface(p1, mine, yours, p2);
	var yourInterf = new TradeInterface(p2, yours, mine, p1);
	
	p1.getWindow().open(myInterf);
	p2.getWindow().open(yourInterf);
}
