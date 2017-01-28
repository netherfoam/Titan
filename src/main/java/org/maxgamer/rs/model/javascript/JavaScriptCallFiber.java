package org.maxgamer.rs.model.javascript;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;

public class JavaScriptCallFiber extends Fiber<Object> {
    public static final File SCRIPT_FOLDER = new File("javascripts");

    private static Logger logger = LoggerFactory.getLogger("JavaScript");

    static {
        if (!SCRIPT_FOLDER.isDirectory()) {
            SCRIPT_FOLDER.mkdirs();
        }
    }

    /**
     * The scope of this script. This contains all functions and variables
     * in the script.
     */
    private ScriptableObject scope;

    private String module;

    private String function;

    private Object[] args;

    private Object result;

    /**
     * Constructs a new JavaScriptFiber, loading the given file
     * when execution starts. This also loads the core.js library
     * file raising a RuntimeException if the file can't be loaded.
     * can't be loaded.
     */
    public JavaScriptCallFiber(ScriptableObject scope, String module, String function, Object... args) {
        this.scope = scope;
        this.module = module;
        this.function = function;
        this.args = args;
    }

    public void resume(Object result) {
        this.result = result;

        super.unpark();
    }

    public boolean hasFunction() {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        Require require = (Require) scope.get("require", scope);

        try {
            // TODO: Type safety check (can't be a function etc)
            // Invoke require("my/module.js")
            ScriptableObject module;
            try {
                module = (ScriptableObject) require.call(context, scope, require, new String[]{this.module});
            } catch (JavaScriptException e) {
                // If the module was not found, we skip safely
                if (e.getMessage().startsWith("Error: Module ") && e.getMessage().endsWith(" not found.")) {
                    return false;
                }
                throw e;
            }

            if (module == null || module == Undefined.instance) {
                logger.debug("module " + this.module + " not found");

                return false;
            }

            // TODO: Check a Function is returned (not a variable etc)
            Function f = (Function) module.get(this.function);

            if (f == null || f == Undefined.instance) {
                logger.debug("function " + this.function + " not found in module " + this.module);

                return false;
            }

            return true;
        } finally {
            Context.exit();
        }
    }

    @Override
    public Object run() throws SuspendExecution {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        Require require = (Require) scope.get("require", scope);

        // TODO: Type safety check (can't be a function etc)
        // Invoke require("my/module.js")
        ScriptableObject module;
        try {
            module = (ScriptableObject) require.call(context, scope, require, new String[]{this.module});
        } catch (JavaScriptException e) {
            // If the module was not found, we skip safely
            if (e.getMessage().startsWith("Error: Module ") && e.getMessage().endsWith(" not found.")) {
                return null;
            }
            throw e;
        }

        if (module == null || module == Undefined.instance) {
            logger.debug("module " + this.module + " not found");

            return null;
        }

        // TODO: Check a Function is returned (not a variable etc)
        Function f = (Function) module.get(this.function);

        if (f == null || f == Undefined.instance) {
            logger.debug("function " + this.function + " not found in module " + this.module);

            return null;
        }

        try {
            return startContinuation(f, context, scope, module, args);
        } catch (ContinuationPending pending) {
            while (true) {
                // We pause and wait for a resume() call
                Fiber.park();

                try {
                    return context.resumeContinuation(pending.getContinuation(), scope, result);
                } catch (ContinuationPending next) {
                    pending = next;
                }
            }
        }
    }

    private Object startContinuation(Function f, Context context, Scriptable scope, ScriptableObject module, Object[] args) {
        // Instead of calling context.callFunctionWithContinuations(..), we call this method.
        // As it allows us to specify 'thisObj' as the loaded module. Eg, can reference methods
        // inside the module using this.foo() instead of module.exports.foo().

        try {
            Field field = Context.class.getDeclaredField("isContinuationsTopCall");
            field.setAccessible(true);
            field.set(context, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return ScriptRuntime.doTopCall(f, context, scope, module, args);
    }
}
