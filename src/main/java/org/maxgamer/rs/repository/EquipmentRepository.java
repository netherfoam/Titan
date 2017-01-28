package org.maxgamer.rs.repository;

import org.maxgamer.rs.model.item.weapon.EquipmentType;

/**
 * @author netherfoam
 */
public class EquipmentRepository extends AbstractRepository<EquipmentType> {
    public EquipmentRepository() {
        super(EquipmentType.class);
    }
}
