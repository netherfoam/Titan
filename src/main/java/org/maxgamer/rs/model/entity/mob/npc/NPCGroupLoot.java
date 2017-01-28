package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.entity.mob.npc.loot.CommonLootItem;
import org.maxgamer.rs.model.entity.mob.npc.loot.LootItem;
import org.maxgamer.rs.model.item.ItemType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an item that can be dropped by a group of NPC's. This item has a chance, and is not always
 * guaranteed - it's picked from the pool of items which can be dropped at random.
 *
 * @author netherfoam
 */
@Entity
@Table(name = "NPCGroupLoot")
public class NPCGroupLoot implements Serializable {
    @Id
    @MapsId
    @ManyToOne
    private NPCGroup group;

    @Id
    @MapsId
    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemType item;

    @Column
    private int min;

    @Column
    private int max;

    @Column
    private double chance;

    public LootItem toLoot() {
        // TODO: Rare loot items?
        return new CommonLootItem(item.toItem(), chance, min, max);
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

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
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

        final NPCGroupLoot other = (NPCGroupLoot) obj;

        return Objects.equals(this.group, other.group)
                && Objects.equals(this.item, other.item);
    }
}
