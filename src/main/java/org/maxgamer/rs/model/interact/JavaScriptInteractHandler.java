package org.maxgamer.rs.model.interact;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.interact.use.OptionUse;
import org.maxgamer.rs.model.javascript.DialogueUtil;
import org.maxgamer.rs.model.javascript.JavaScriptCallFiber;
import org.maxgamer.rs.util.Chat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class JavaScriptInteractHandler implements InteractionHandler {
    /**
     * The folder where interaction javascripts are stored
     */
    private static final File INTERACTION_FOLDER = new File(JavaScriptCallFiber.SCRIPT_FOLDER, "interaction");

    /**
     * Convert the given entity and option into a file, or null if there's no handler for that entity/file
     *
     * @param entity the entity, the name and class type is used
     * @param option the option that was clicked
     * @return the file or null if no good file was found
     */
    private ArrayList<File> get(Interactable entity, String option) {
        Class<?> clazz = entity.getClass();

        String entityName = entity.getName().toLowerCase();
        entityName = entityName.replaceAll(" ", "-");

        if (!entityName.matches("[A-Za-z0-9 \\-_]*$")) {
            // Eg.  An 'Amulet of glory (4)' becomes 'Amulet of glory', with the special characters trimmed, and excess spaces removed.
            entityName = entityName.replaceAll("[^A-Za-z0-9\\-_ ].*", "").trim();
        }

        option = option.replaceAll(" ", "-");
        option = option.toLowerCase();

        ArrayList<File> files = new ArrayList<File>();

        // We exhaust all superclass options as well as the base class
        while (clazz != Object.class) {
            String className = clazz.getSimpleName().toLowerCase();

            File f = new File(INTERACTION_FOLDER + File.separator + className, entityName + ".js");
            if (f.exists()) {
                files.add(f);
            }

            f = new File(INTERACTION_FOLDER + File.separator + className, option + ".js");
            if (f.exists()) {
                files.add(f);
            }

            clazz = clazz.getSuperclass();
        }

        return files;
    }

    /**
     * Converts a given string into a camel-cased function name. Eg "Chop-down" becomes "chopDown", or "Rub Amulet" becomes "rubAmulet"
     *
     * @param option the option, eg "Search"
     * @return the function name, eg "search"
     */
    private String toFunction(String option) {
        StringBuilder sb = new StringBuilder(option.length());
        option = option.toLowerCase();

        for (int i = 0; i < option.length(); i++) {
            char c = option.charAt(i);

            if (c == ' ' || c == '-' || c == '_') {
                i++;
                if (option.length() <= i) break;
                c = option.charAt(i);
                sb.append(Character.toUpperCase(c));
                continue;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Interact
    public void javascript(Mob source, Interactable target, OptionUse use) throws SuspendExecution, NotHandledException {
        this.javascript(source, target, use.getOption());
    }

    /**
     * Handles when we receive an interaction
     */
    public void javascript(Mob source, Interactable target, String option) throws SuspendExecution, NotHandledException {
        ArrayList<File> files = this.get(target, option);
        String function = this.toFunction(option);

        if (target instanceof Mob) {
            Mob t = (Mob) target;
            if (t.getFacing() == null) {
                // Turn the target around so they respond to the interaction
                t.setFacing(Facing.face(source.getCenter()));
            }
        }

        // Camel case via any spaces or dashes
        option = option.replaceAll("-", " ");
        option = Chat.toLowerCamelCase(option, ' ');

        for (File f : files) {
            if(!f.exists()) continue;

            String module = f.toURI().toString();
            module = module.substring(0, module.length() - 3); // Drop the '.js' extension

            JavaScriptCallFiber call = new JavaScriptCallFiber(Core.getServer().getScriptEnvironment(), module, option, source, target);
            if(!call.hasFunction()) continue;

            if (target instanceof Entity) {
                source.face((Entity) target);
            }

            DialogueUtil.setCause(call, source);
            call.start();

            try {
                call.join();

                return;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            return;
        }

        if (!files.isEmpty()) {
            System.out.println("Files " + Arrays.toString(files.toArray(new File[files.size()])) + " exists, but the function " + function + "() does not.");
        }

        throw new NotHandledException();
    }
}