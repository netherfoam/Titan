package org.maxgamer.rs.model.item.vendor;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.ItemType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "VendorItem")
public class VendorItem implements Serializable {
    @Id
    @ManyToOne
    private Vendor vendor;

    @Id
    @ManyToOne
    private ItemType item;

    @Column
    private long amount;

    public VendorItem() {
    }

    public VendorItem(Vendor vendor, ItemType item, long amount) {
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

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
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
}
