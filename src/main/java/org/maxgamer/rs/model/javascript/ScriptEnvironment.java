package org.maxgamer.rs.model.javascript;

import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author netherfoam
 */
public class ScriptEnvironment extends ScriptableObject {
    public ScriptEnvironment(File folder) {
        Context c = Context.enter();
        c.setOptimizationLevel(-1);

        //Scriptable parent = c.initStandardObjects();
        c.initStandardObjects();
        final ImporterTopLevel topLevel = new ImporterTopLevel(c);
        setParentScope(topLevel);

        register(StaticFunctions.class);

        RequireBuilder builder = new RequireBuilder();
        Iterable<URI> paths = Collections.singletonList(folder.toURI());
        ModuleSourceProvider moduleSourceProvider = new UrlModuleSourceProvider(paths, null);
        ModuleScriptProvider moduleScriptProvider = new SoftCachingModuleScriptProvider(moduleSourceProvider);
        builder.setModuleScriptProvider(moduleScriptProvider);
        builder.setSandboxed(false);
        Require require = builder.createRequire(c, this.getParentScope());
        require.install(this);
        builder.setPreExec(new Script() {
            @Override
            public Object exec(Context cx, Scriptable scope) {
                Scriptable parent = scope.getParentScope();

                assert parent.getParentScope() == null : "expected no grandparent scope";
                parent.setParentScope(topLevel);

                return null;
            }
        });

        Context.exit();
    }

    public void register(Class<?> nativeMethods) {
        ((ImporterTopLevel) getParentScope()).defineFunctionProperties(getMethods(nativeMethods), nativeMethods, ScriptableObject.DONTENUM);
    }

    private static String[] getMethods(Class<?> clazz) {
        ArrayList<String> methods = new ArrayList<>();
        for(Method m : clazz.getMethods()) {
            if((m.getModifiers() & (Modifier.STATIC | Modifier.PUBLIC)) == 0) continue;
            if(m.getParameterTypes().length != 4) continue;
            if(!m.getParameterTypes()[0].equals(Context.class)) continue;
            if(!m.getParameterTypes()[1].equals(Scriptable.class)) continue;
            if(!m.getParameterTypes()[2].equals(Object[].class)) continue;
            if(!m.getParameterTypes()[3].equals(Function.class)) continue;

            methods.add(m.getName());
        }

        return methods.toArray(new String[methods.size()]);
    }

    @Override
    public String getClassName() {
        return "Root";
    }
}
