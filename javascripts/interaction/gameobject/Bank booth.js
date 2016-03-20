function use(player, booth){
	useQuickly(player, booth);
}

function useQuickly(player, booth){
	var iface = new org.maxgamer.rs.interfaces.impl.primary.BankInterface(player);
	player.getWindow().open(iface);
}

function collect(player, booth){
	player.sendMessage("Collecting items isn't implemented.");
}