module.exports = {
    climbDown: function(player, object) {
        var src = player.getLocation();
        if (src.z == 0) {
            dest = src.add(0, 6400, 0);
        } else {
            dest = src.add(0, 0, -1);
        }

        animate(player, 828, 5);
        player.teleport(dest);
    }
}