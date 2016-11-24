package org.maxgamer.rs.model.javascript;

import org.maxgamer.rs.core.Core;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author netherfoam
 */
public class JavaScriptInvocation implements java.util.concurrent.Callable<Object> {
    public static final File SCRIPT_FOLDER = new File("javascripts");

    private static Logger logger = LoggerFactory.getLogger("JavaScript");

    static {
        if (!SCRIPT_FOLDER.isDirectory()) {
            SCRIPT_FOLDER.mkdirs();
        }
    }

    private ClassLoader classLoader;

    /**
     * The scope of this script. This contains all functions and variables
     * in the script.
     */
    private Scriptable scope;

    private String module;

    private String function;

    private Object[] args;

    /**
     * Constructs a new JavaScriptFiber, loading the given file
     * when execution starts. This also loads the core.js library
     * file raising a RuntimeException if the file can't be loaded.
     * can't be loaded.  This loads the Core.CLASS_LOADER instead of
     * allowing for a custom one.
     */
    public JavaScriptInvocation(ScriptableObject scope, String module, String function, Object... args) {
        this(scope, Core.CLASS_LOADER, module, function, args);
    }

    /**
     * Constructs a new JavaScriptFiber, loading the given file
     * when execution starts. This also loads the core.js library
     * file raising a RuntimeException if the file can't be loaded.
     * can't be loaded.
     *
     * @param loader the classloader to use when entering the context
     */
    public JavaScriptInvocation(ScriptableObject scope, ClassLoader loader, String module, String function, Object... args) {
        this.scope = scope;
        this.classLoader = loader;
        this.module = module;
        this.function = function;
        this.args = args;
    }

    @Override
    public Object call() {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        Require require = (Require) scope.get("require", scope);

        // TODO: Type safety check (can't be a function etc)
        // Invoke require("my/module.js")
        ScriptableObject module = (ScriptableObject) require.call(context, scope, require, new String[]{this.module});

        if(module == null || module == Undefined.instance) {
            logger.debug("module " + this.module + " not found");

            return null;
        }

        // TODO: Check a Function is returned (not a variable etc)
        Function f = (Function) module.get(this.function);

        if(f == null || f == Undefined.instance) {
            logger.debug("function " + this.function + " not found in module " + this.module);

            return null;
        }

        // TODO: thisObj
        return f.call(context, scope, null, args);
    }
}
