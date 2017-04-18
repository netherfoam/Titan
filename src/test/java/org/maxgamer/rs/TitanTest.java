package org.maxgamer.rs;

import org.junit.BeforeClass;
import org.maxgamer.rs.core.Core;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author netherfoam
 */
public class TitanTest {
    private static boolean initialized = false;

    @BeforeClass
    public static void init() throws IOException, SQLException, InterruptedException {
        if(!initialized) {
            initialized = true;
            Core.start();

            while(Core.getServer().getTicks() <= 0) {
                Thread.sleep(1);
            }
        }
    }
}
