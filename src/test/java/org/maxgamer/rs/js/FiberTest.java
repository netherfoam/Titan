package org.maxgamer.rs.js;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.model.javascript.JavaScriptCallFiber;
import org.maxgamer.rs.model.javascript.RootScope;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * @author netherfoam
 */
public class FiberTest {
    private File folder = new File("javascripts");
    private ScriptableObject scope;
    private Logger logger = LoggerFactory.getLogger("test");

    @Before
    public void init() {
        scope = new RootScope(folder);
    }

    @Test
    public void runScript() throws ExecutionException, InterruptedException {
        JavaScriptCallFiber fiber = new JavaScriptCallFiber(scope, "test.js", "foo");
        fiber.start();
        long start = System.nanoTime();
        fiber.get();
        logger.warn("Joined in " + (System.nanoTime() - start) / 1000000.0 + "ms");
    }

    @Test
    public void importClass() throws ExecutionException, InterruptedException {
        JavaScriptCallFiber fiber = new JavaScriptCallFiber(scope, "test.js", "bar");
        fiber.start();
        long start = System.nanoTime();
        fiber.get();
        logger.warn("Joined in " + (System.nanoTime() - start) / 1000000.0 + "ms");
    }

    @Test
    public void thisCall() throws ExecutionException, InterruptedException {
        JavaScriptCallFiber fiber = new JavaScriptCallFiber(scope, "test.js", "baz");
        fiber.start();
        long start = System.nanoTime();
        fiber.get();
        logger.warn("Joined in " + (System.nanoTime() - start) / 1000000.0 + "ms");
    }

    @Test
    public void hold() throws ExecutionException, InterruptedException {
        JavaScriptCallFiber fiber = new JavaScriptCallFiber(scope, "test.js", "hold");
        fiber.start();
        long start = System.nanoTime();

        Thread.sleep(500);
        fiber.unpark();

        Assert.assertEquals("hold", fiber.get());

        logger.warn("Joined in " + (System.nanoTime() - start) / 1000000.0 + "ms");
    }
}
