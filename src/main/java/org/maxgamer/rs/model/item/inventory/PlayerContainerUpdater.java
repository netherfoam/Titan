package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class PlayerContainerUpdater implements ContainerListener {
    private Player player;
    private int containerId;
    private boolean split = false;

    public PlayerContainerUpdater(Player p, int containerId, boolean split) {
        if (p == null) throw new NullPointerException("Player may not be null");
        if (containerId < 0) throw new IllegalArgumentException("ContainerId must be >= 0");

        this.player = p;
        this.containerId = containerId;
        this.split = split;
    }

    @Override
    public void onSet(Container c, int slot, ItemStack old) {
        player.getProtocol().setItem(containerId, split, c.get(slot), slot);
    }
}