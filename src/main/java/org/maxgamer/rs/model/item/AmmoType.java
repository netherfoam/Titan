package org.maxgamer.rs.model.item;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "Ammo")
public class AmmoType implements Serializable {
    @Id
    @OneToOne
    @JoinColumn(name = "item_id")
    private ItemType item;

    @Column
    private int graphics;

    @Column
    private int height;

    @Column
    private int projectile;

    public ItemType getItem() {
        return item;
    }

    public void setItem(ItemType item) {
        this.item = item;
    }

    public int getGraphics() {
        return graphics;
    }

    public void setGraphics(int graphics) {
        this.graphics = graphics;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getProjectile() {
        return projectile;
    }

    public void setProjectile(int projectile) {
        this.projectile = projectile;
    }
}
