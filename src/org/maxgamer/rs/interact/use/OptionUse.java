package org.maxgamer.rs.interact.use;

public class OptionUse implements Use {
    private String option;

    public OptionUse(String option) {
        if(option == null) throw new NullPointerException("Option may not be null");
        this.option = option;
    }

    public String getOption() {
        return this.option;
    }
}
