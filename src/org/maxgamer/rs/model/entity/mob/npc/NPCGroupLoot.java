package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.entity.mob.npc.loot.CommonLootItem;
import org.maxgamer.rs.model.entity.mob.npc.loot.LootItem;
import org.maxgamer.rs.model.item.ItemStack;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "NPCGroupLoot")
public class NPCGroupLoot implements Serializable {
    @Id
    @MapsId
    @ManyToOne
    private NPCGroup group;

    @Column
    private int item_id;

    @Column
    private int min;

    @Column
    private int max;

    @Column
    private double chance;

    public LootItem toLoot() {
        // TODO: Rare loot items?
        return new CommonLootItem(ItemStack.create(item_id), chance, min, max);
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
}
