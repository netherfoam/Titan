import java.io.File;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.GenericContainer;
import org.maxgamer.rs.model.item.inventory.StackType;

public class ContainerTest{
	@Test
	public void test() {
		Assume.assumeTrue(new File("cache").exists());
		
		Container c = new GenericContainer(10, StackType.NORMAL);
		c.add(ItemStack.create(995, 1));
		c.add(ItemStack.create(995, 1));
		
		Assert.assertTrue("Amount mismatch", c.get(0).getAmount() == 2);
		
		c.remove(ItemStack.create(995, 1));
		Assert.assertTrue("Amount mismatch", c.get(0).getAmount() == 1);
	}
}