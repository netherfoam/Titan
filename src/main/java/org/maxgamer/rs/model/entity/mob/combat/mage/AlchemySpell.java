package org.maxgamer.rs.model.entity.mob.combat.mage;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerState;

/**
 * @author netherfoam
 */
public class AlchemySpell extends ItemSpell {
    private double multiplier;

    public AlchemySpell(int level, int gfx, int anim, int castTime, double multiplier, ItemStack... runes) {
        super(level, gfx, anim, castTime, runes);
        if (multiplier <= 0) {
            throw new IllegalArgumentException("Multiplier must be > 0 and is usually 0.6 or 0.9 but less than 1");
        }
        this.multiplier = multiplier;
    }

    @Override
    public void cast(final Mob source, final Container c, final int slot) {
        //Item guaranteed not to be null by caller
        final ItemStack item = c.get(slot);

        final int coins = (int) (item.getDefinition().getValue() * multiplier);
        if (coins <= 0) { //Would destroy item for no reward
            if (source instanceof Player) {
                source.sendMessage("You can't cast that spell on that item.");
            }
            return;
        }

        if (!this.hasRequirements(source) || !takeConsumables(source)) { //This checks runes as well
            return;
        }

        source.getActions().clear();
        source.getActions().queue(new Action(source) {
            @Override
            protected void run() throws SuspendExecution {
                displayCast(source);

                wait(4);

                ContainerState state = c.getState();
                try {
                    state.remove(slot, item.setAmount(1));
                } catch (ContainerException e) {
                    return; //The item could not be removed
                }

                try {
                    state.add(ItemStack.create(995, coins));
                } catch (ContainerException e) {
                    if (source instanceof Persona) {
                        ((Persona) source).getLostAndFound().add(ItemStack.create(995, coins));
                    }
                }

                state.apply();

                /*if (tick == 0) {
                    displayCast(source);
                }

                tick++;

                if (tick >= 4) {
                    ContainerState state = c.getState();
                    try {
                        state.remove(slot, item.setAmount(1));
                    }
                    catch (ContainerException e) {
                        return true; //The item could not be removed
                    }

                    try {
                        state.add(ItemStack.create(995, coins));
                    }
                    catch (ContainerException e) {
                        if (source instanceof Persona) {
                            ((Persona) source).getLostAndFound().add(ItemStack.create(995, coins));
                        }
                    }

                    state.apply();
                    return true;
                }
                return false;*/
            }

            @Override
            protected void onCancel() {

            }

            @Override
            protected boolean isCancellable() {
                return false;
            }
        });

    }
}