module.exports = {
    foo: function() {
        return "foo";
    },
    bar: function() {
        importClass(org.maxgamer.rs.model.interfaces.impl.primary.BankInterface);
    },
    hold: function() {
        pause();

        return "hold";
    },
    baz: function() {
        this.foo();
        this.bar();
    }
}