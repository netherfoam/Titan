import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.maxgamer.rs.io.CircularBuffer;

public class CircularBufferTest {
	@Test
	public void test() {
		byte[] data = new byte[] { 5, 6, 7, 8, 9 };
		CircularBuffer cb = new CircularBuffer(data.length - 1);
		
		Random r = new Random();
		while (true) {
			cb.write(data);
			byte[] e = new byte[data.length];
			cb.read(e);
			for (int i = 0; i < data.length; i++) {
				assertEquals(e[i], data[i]);
			}
			
			if (r.nextInt(10) <= 0) {
				cb.mark();
				
				if (r.nextBoolean()) {
					cb.write(data);
					for (int i = 0; i < data.length; i++) {
						assertEquals(e[i], data[i]);
					}
					
					cb.reset();
				}
			}
		}
	}
}