package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;

/**
 * @author netherfoam
 */
public class PersonaEquipment extends Equipment {
    public PersonaEquipment(Persona owner) {
        super(owner);
    }

    public Persona getOwner() {
        return (Persona) super.getOwner();
    }

    @Override
    protected void setItem(int slot, ItemStack item) {
        super.setItem(slot, item);

        WieldType t = WieldType.forSlot(slot);

        switch (t) {
            case HAT:
            case CAPE:
            case AMULET:
            case WEAPON:
            case BODY:
            case SHIELD:
            case LEGS:
            case GLOVES:
            case BOOTS:
                //Visible
                getOwner().getModel().setChanged(true);
                break;
            case RING:
            case ARROWS:
                break; //Not visible
        }
    }
}