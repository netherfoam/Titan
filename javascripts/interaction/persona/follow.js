module.exports = {
    follow: function(source, target) {
        source.getActions().clear();

        var f = new org.maxgamer.rs.model.action.FriendFollow(source, target, 1, 12, new org.maxgamer.rs.model.map.path.AStar(4));
        source.getActions().queue(f);
    }
}