package org.maxgamer.rs;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.GenericContainer;
import org.maxgamer.rs.model.item.inventory.StackType;

/**
 * @author netherfoam
 */
public class ContainerTest extends TitanTest {
    @Test
    public void testAdd() throws Exception {
        Container c = new GenericContainer(1, StackType.NORMAL);
        c.add(ItemStack.COINS);
        c.add(ItemStack.COINS);
        Assert.assertTrue("container must be full", c.isFull());
        Assert.assertEquals("container must contain 2x coins", c.getNumberOf(ItemStack.COINS), 2);
    }
}
