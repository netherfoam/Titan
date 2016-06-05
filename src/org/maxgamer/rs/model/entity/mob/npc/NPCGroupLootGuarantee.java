package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.item.ItemStack;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "NPCGroupLootGuarantee")
public class NPCGroupLootGuarantee implements Serializable {
    @Id
    @MapsId
    @ManyToOne
    private NPCGroup group;

    @Column
    private int item_id;

    @Column
    private int amount;

    public ItemStack toItem() {
        return ItemStack.create(item_id, amount);
    }

    public NPCGroup getGroup() {
        return group;
    }

    public void setGroup(NPCGroup group) {
        this.group = group;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
