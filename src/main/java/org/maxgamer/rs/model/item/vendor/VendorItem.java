package org.maxgamer.rs.model.item.vendor;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.ItemType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing an item sold at a vendor
 *
 * @author netherfoam
 */
@Entity
@Table(name = "VendorItem")
public class VendorItem implements Serializable {
    @Id
    @MapsId
    @ManyToOne
    private VendorType vendor;

    @Id
    @MapsId
    @ManyToOne
    private ItemType item;

    @Column
    private long amount;

    public VendorItem() {
    }

    public VendorItem(VendorType vendor, ItemType item, long amount) {
        this.vendor = vendor;
        this.item = item;
        this.amount = amount;
    }

    public ItemType getItem() {
        return item;
    }

    public void setItem(ItemType item) {
        this.item = item;
    }

    public VendorType getVendor() {
        return vendor;
    }

    public void setVendor(VendorType vendor) {
        this.vendor = vendor;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ItemStack toItem() {
        return ItemStack.create(this.item.getId(), this.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendor, item);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final VendorItem other = (VendorItem) obj;

        return Objects.equals(this.vendor, other.vendor)
                && Objects.equals(this.item, other.item);
    }
}
