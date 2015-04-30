import *;

/**
 * This is a test command script which can be run like a command.
 * The benefit of these types of commands is that they can be loaded or unloaded
 * via the reload command.
 */

public void execute(Player p, String[] args) {
	p.getWindow().open(new ItemPickerInterface(p, new ItemStack[]{ItemStack.create(995))});
}

public int getRankRequired(){
	return 0;
}