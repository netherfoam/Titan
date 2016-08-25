package org.maxgamer.rs.model.item;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "ItemAmmo")
public class ItemAmmo implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemType weapon;

    @Id
    @ManyToOne
    @JoinColumn(name = "ammo_id")
    private ItemType projectile;

    @Column
    private int quantity = 1;

    public ItemType getWeapon() {
        return weapon;
    }

    public void setWeapon(ItemType weapon) {
        this.weapon = weapon;
    }

    public ItemType getProjectile() {
        return projectile;
    }

    public void setProjectile(ItemType projectile) {
        this.projectile = projectile;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ItemStack toItem() {
        return ItemStack.create(projectile.getId(), quantity);
    }
}
