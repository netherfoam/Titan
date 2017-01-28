package org.maxgamer.rs.model.item;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "ItemAmmo")
public class ItemAmmoType implements Serializable {
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

    @Override
    public int hashCode() {
        return Objects.hash(weapon, projectile);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ItemAmmoType other = (ItemAmmoType) obj;

        return Objects.equals(this.weapon, other.weapon)
                && Objects.equals(this.projectile, other.projectile);
    }
}
