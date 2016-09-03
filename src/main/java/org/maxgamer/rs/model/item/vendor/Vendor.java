package org.maxgamer.rs.model.item.vendor;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.ItemType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "Vendor")
public class Vendor implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private int flags;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "currency")
    private ItemType currency = null;

    @OneToMany(mappedBy = "vendor")
    private List<VendorItem> items = new LinkedList<VendorItem>();

    private transient VendorContainer container;

    /**
     * Sets up the VendorContainer based on the current values
     */
    @PostLoad
    public void reset() {
        if (this.currency == null) {
            this.currency = ItemStack.COINS.getDefinition();
        }
        this.container = new VendorContainer(this);
    }

    public VendorContainer getContainer() {
        return container;
    }

    public void setContainer(VendorContainer container) {
        this.container = container;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemType getCurrency() {
        return currency;
    }

    public void setCurrency(ItemType currency) {
        this.currency = currency;
    }

    public List<VendorItem> getItems() {
        return items;
    }

    public void setItems(List<VendorItem> items) {
        this.items = items;
    }
}
