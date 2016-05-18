function follow(source, target) {
	source.getActions().clear();
			
	var f = new org.maxgamer.rs.model.action.FriendFollow(clicker, target, 1, 12, new AStar(4));
	clicker.getActions().queue(f);
}