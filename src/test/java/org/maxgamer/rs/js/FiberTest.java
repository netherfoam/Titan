package org.maxgamer.rs.js;

import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.model.javascript.JavaScriptCallFiber;
import org.maxgamer.rs.model.javascript.ScriptEnvironment;
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

    @Before
    public void init() {
        scope = new ScriptEnvironment(folder);
    }

    @Test(timeout = 1000L)
    public void runScript() throws ExecutionException, InterruptedException {
        JavaScriptCallFiber fiber = new JavaScriptCallFiber(scope, "test.js", "foo");
        fiber.start();
        fiber.get();
    }

    @Test(timeout = 1000L)
    public void importClass() throws ExecutionException, InterruptedException {
        JavaScriptCallFiber fiber = new JavaScriptCallFiber(scope, "test.js", "bar");
        fiber.start();
        fiber.get();
    }

    @Test(timeout = 1000L)
    public void thisCall() throws ExecutionException, InterruptedException {
        JavaScriptCallFiber fiber = new JavaScriptCallFiber(scope, "test.js", "baz");
        fiber.start();
        fiber.get();
    }
}
