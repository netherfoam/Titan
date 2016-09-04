package org.maxgamer.rs;

import org.junit.Before;
import org.maxgamer.rs.core.Core;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author netherfoam
 */
public class TitanTest {
    private static boolean initialized = false;

    @Before
    public void init() throws IOException, SQLException, InterruptedException {
        if(!initialized) {
            initialized = true;
            Core.start();

            while(Core.getServer().getTicks() <= 0) {
                Thread.sleep(10);
            }
        }
    }
}
