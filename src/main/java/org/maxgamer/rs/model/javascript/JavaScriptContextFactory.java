package org.maxgamer.rs.model.javascript;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import java.io.File;

public abstract class JavaScriptContextFactory extends ContextFactory {
    private ClassLoader loader;
    private File folder;

    public JavaScriptContextFactory(ClassLoader loader, File folder) {
        super();
        this.loader = loader;
        this.folder = folder;
    }

    public abstract long credits();

    public abstract void pay(long cost);

    @Override
    protected JavaScriptContext makeContext() {
        JavaScriptContext c = new JavaScriptContext(this);
        c.setOptimizationLevel(-1);
        c.setInstructionObserverThreshold(1000);

        return c;
    }

    @Override
    public JavaScriptContext enterContext() {
        JavaScriptContext context = (JavaScriptContext) super.enterContext();
        context.setApplicationClassLoader(loader);
        return context;
    }

    @Override
    protected void observeInstructionCount(Context cx, int instructionCount) {
        JavaScriptContext c = (JavaScriptContext) cx;

        long cost = c.getRuntime();
        pay(cost);
        if (credits() < 0) {
            throw new TimeoutError("Script has surpassed maximum time.  Runtime: " + c.getRuntime() + "ms, bytecode instructions: " + instructionCount);
        }

        // Restart the cost counter
        c.start();
    }

    @Override
    public void onContextCreated(Context cx) {
        JavaScriptContext c = (JavaScriptContext) cx;
        c.start();
    }

    @Override
    public void onContextReleased(Context cx) {
        JavaScriptContext c = (JavaScriptContext) cx;
        long cost = c.getRuntime();
        pay(cost);
        if (credits() < 0) {
            throw new TimeoutError("Script has surpassed maximum time.  Runtime: " + c.getRuntime() + "ms");
        }

        // Restart the cost counter
        c.start();
    }

    @Override
    protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        JavaScriptContext c = (JavaScriptContext) cx;
        c.start();
        return super.doTopCall(callable, cx, scope, thisObj, args);
    }
}