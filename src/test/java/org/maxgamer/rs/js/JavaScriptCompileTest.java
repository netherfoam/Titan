package org.maxgamer.rs.js;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.model.javascript.JavaScriptCallFiber;
import org.maxgamer.rs.model.javascript.ScriptEnvironment;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author netherfoam
 */
public class JavaScriptCompileTest {
    private ScriptEnvironment scope;

    @Test
    public void compileAll() throws IOException {
        Files.walkFileTree(JavaScriptCallFiber.SCRIPT_FOLDER.toPath(), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path directions, BasicFileAttributes basicFileAttributes) throws IOException {
                // Just don't parse r.js
                if(directions.getFileName().endsWith("lib")) return FileVisitResult.SKIP_SUBTREE;

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path directions, BasicFileAttributes basicFileAttributes) throws IOException {
                compile(directions.toFile(), new ScriptEnvironment(JavaScriptCallFiber.SCRIPT_FOLDER));

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path directions, IOException e) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path directions, IOException e) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void compile(File f, ScriptEnvironment scope) {
        try {
            String moduleName = f.getAbsolutePath().substring(JavaScriptCallFiber.SCRIPT_FOLDER.getAbsolutePath().length() + 1);
            moduleName = moduleName.substring(0, moduleName.length() - 3); // Drop the '.js' extension

            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Require require = (Require) scope.get("require", scope);

            ScriptableObject module = (ScriptableObject) require.call(context, scope, require, new String[]{moduleName});
        }
        catch(Exception e) {
            Assert.fail("Failed to parse " + f.getPath() + ": " + e.getMessage());
        }
    }
}
