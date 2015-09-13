import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.maxgamer.rs.cache.RSCompression;

public class RSCompressionTest{
	@Test
	public void test() throws IOException{
		byte[] data = "Hello World!".getBytes();
		ByteBuffer bb = ByteBuffer.wrap(data);
		
		for(RSCompression c : RSCompression.values()){
			ByteBuffer encoded = c.encode(bb, null);
			ByteBuffer decoded = c.decode(encoded, null);
			
			int pos = 0;
			while(pos < data.length){
				byte b = (byte) decoded.get();
				assertEquals(data[pos], b);
				pos++;
			}
		}
	}
}