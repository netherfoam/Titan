package org.maxgamer.rs;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.*;

/**
 * @author netherfoam
 */
public class ContainerTest extends TitanTest {
    private final ItemStack SHARK = ItemStack.create(385);

    @Test
    public void testAdd() throws Exception {
        Container c = new GenericContainer(1, StackType.NORMAL);
        c.add(ItemStack.COINS);
        c.add(ItemStack.COINS);
        Assert.assertTrue("container must be full", c.isFull());
        Assert.assertEquals("container must contain 2x coins", c.getNumberOf(ItemStack.COINS), 2);
    }

    @Test
    public void testWithdraw() {
        Container c = new BankContainer();

        c.set(0, ItemStack.COINS.setAmount(2));
        c.set(1, ItemStack.COINS.setAmount(2));
        c.add(SHARK);

        c.remove(ItemStack.COINS.setAmount(4));
        c.add(ItemStack.COINS.setAmount(4));

        Assert.assertEquals("Expect 1 shark", 1, c.getNumberOf(SHARK));
        Assert.assertEquals("Expect 4 coins", 4, c.getNumberOf(ItemStack.COINS));
    }

    @Test
    public void testShiftWithDuplicatedStacks() {
        Container c = new BankContainer();

        c.set(0, ItemStack.COINS.setAmount(2));
        c.set(1, ItemStack.COINS.setAmount(2));
        c.add(SHARK);

        c.shift();

        Assert.assertEquals("Expect 1 shark", 1, c.getNumberOf(SHARK));
        Assert.assertEquals("Expect 4 coins", 4, c.getNumberOf(ItemStack.COINS));
    }

    @Test
    public void testShift() {
        Container c = new BankContainer();

        c.set(0, ItemStack.COINS.setAmount(2));
        c.set(1, ItemStack.COINS.setAmount(2));
        c.add(SHARK);

        c.remove(ItemStack.COINS.setAmount(4));
        c.shift();
        c.add(ItemStack.COINS.setAmount(4));
        c.shift();

        Assert.assertEquals("Expect 1 shark", 1, c.getNumberOf(SHARK));
        Assert.assertEquals("Expect 4 coins", 4, c.getNumberOf(ItemStack.COINS));
    }
}
