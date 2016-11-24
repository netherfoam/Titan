module.exports = {
    climbUp: function(player, object) {
        var src = player.getLocation();
        if (src.y >= 6400) {
            dest = src.add(0, -6400, 0);
        } else {
            dest = src.add(0, 0, 1);
        }

        animate(player, 828, 5);
        player.teleport(dest);
    }
}