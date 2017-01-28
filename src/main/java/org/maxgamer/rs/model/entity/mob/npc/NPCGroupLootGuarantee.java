package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.ItemType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an item that is guaranteed to drop from a group of NPC's.
 *
 * @author netherfoam
 */
@Entity
@Table(name = "NPCGroupLootGuarantee")
public class NPCGroupLootGuarantee implements Serializable {
    @Id
    @MapsId
    @ManyToOne
    @JoinColumn(name = "group_id")
    private NPCGroup group;

    @Id
    @MapsId
    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemType item;

    @Column
    private int amount;

    public ItemStack toItem() {
        return item.toItem(amount);
    }

    public NPCGroup getGroup() {
        return group;
    }

    public void setGroup(NPCGroup group) {
        this.group = group;
    }

    public ItemType getItem() {
        return item;
    }

    public void setItem(ItemType item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, item);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final NPCGroupLootGuarantee other = (NPCGroupLootGuarantee) obj;

        return Objects.equals(this.group, other.group)
                && Objects.equals(this.item, other.item);
    }
}
