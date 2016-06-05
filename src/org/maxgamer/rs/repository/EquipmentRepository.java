package org.maxgamer.rs.repository;

import org.maxgamer.rs.model.item.weapon.Equipment;

/**
 * @author netherfoam
 */
public class EquipmentRepository extends Repository<Equipment> {
    public EquipmentRepository() {
        super(Equipment.class);
    }
}
