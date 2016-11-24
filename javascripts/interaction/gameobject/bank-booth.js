module.exports = {
    use: function(player, booth) {
        module.exports.useQuickly(player, booth);
    },
    useQuickly: function(player, booth){
        var iface = new org.maxgamer.rs.model.interfaces.impl.primary.BankInterface(player);
        player.getWindow().open(iface);
    },
    collect: function(player, booth){
        player.sendMessage("Collecting items isn't implemented.");
    }
}