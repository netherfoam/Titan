package org.maxgamer.rs.network.io.packet.player;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.ItemType;
import org.maxgamer.rs.model.item.ground.GroundItemStack;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.repository.ItemTypeRepository;

/**
 * @author netherfoam
 */
public class GroundItemOptionsHandler implements PacketProcessor<Player> {
    //Not sure what order these should be in.
    public static final int FIRST_OPTION = 22;
    public static final int SECOND_OPTION = 54; //Duplicate OPCODE with THIRD_OPTION
    public static final int THIRD_OPTION = 54; //Eg GroundItem 4653 has option[2]=Study and GroundItem 0 has option[1]=Take
    public static final int FOURTH_OPTION = 38;
    public static final int FIFTH_OPTION = 71;
    public static final int EXAMINE = 69; //EXAMINE

	/*
     * Sample Data: Item 8334(Oak lectern) has option 0(Study) opcode 22, Size:
	 * 7Data: 0x80 C 16 8E 20 15 C Item 0(Dwarf remains) has option 1(Pickup)
	 * opcode 54, Size: 7Data: 0x96 C 0 0 C 15 0 Item 4653(Fire) has option
	 * 2(Study) option 1, [null] opcode 54, Size: 7Data: 0x96 C 2D 12 C 15 0
	 * Item 1511(Logs) has option 3(Light) option 3, [null] Item 3006(Firework)
	 * has option 4(Light) opcode: 71, Size: 7Data: 0x16 C 3E B 15 C 0
	 */

    @Override
    public void process(final Player player, RSIncomingPacket packet) throws Exception {
        GroundItemStack target = null;
        int option = -1;
        @SuppressWarnings("unused")
        int x = 0, y = 0, z = 0, itemId = 0;
        @SuppressWarnings("unused")
        boolean ctrl;

        switch (packet.getOpcode()) {
            case FIRST_OPTION:
                ctrl = (packet.readByteA() != 0);
                x = packet.readLEShortA();
                itemId = packet.readLEShort();
                y = packet.readLEShortA();
                option = 0;
                break;
            case SECOND_OPTION://Pickup
                x = packet.readLEShort();
                itemId = packet.readLEShort();
                y = packet.readShortA();
                ctrl = (packet.readByteC() != 0);
                option = 1;
                break;
            //Appears to be the same OPCode as option two, even in the client :(
            //case THIRD_OPTION:
            //	break;
            case FOURTH_OPTION:
                y = packet.readLEShort();
                itemId = packet.readLEShortA();
                x = packet.readLEShort();
                ctrl = (packet.readByteA() != 0);
                option = 3;
                break;
            case FIFTH_OPTION:
                x = packet.readLEShortA();
                itemId = packet.readLEShortA();
                y = packet.readLEShortA();
                ctrl = (packet.readByte() != 0);
                option = 4;
                break;
            case EXAMINE: //TODO: Should we move this to a different handler?
                itemId = packet.readShort();
                ItemType proto = Core.getServer().getDatabase().getRepository(ItemTypeRepository.class).find(itemId);
                player.sendMessage(proto.getExamine());

                return; //Return, not break
            default:
                return;
        }

        Location l = new Location(player.getLocation().getMap(), x, y, player.getLocation().z);
        for (GroundItemStack near : l.getNearby(GroundItemStack.class, 0)) {
            if (near.isDestroyed()) continue;

            ItemStack stack = near.getItem();
            if (stack.getId() == itemId) {
                //Correct ID.
                if (near.isPublic() || near.getOwner() == player) {
                    target = near;
                    break;
                }
            }
        }

        if (target == null) {
            player.getCheats().log(10, "Player attempted to interact with a NULL GroundItemStack");
            return;
        }

        final GroundItemStack item = target;

        String name = target.getItem().getGroundOptions()[option];
        if (name == null) {
            player.getCheats().log(30, "Player attempted to use a NULL option on GroundItemStack (ID " + target.getItem().getId() + ")");
            return;
        }

        final int opt = option;

        player.getActions().clear();
        AStar finder = new AStar(10);
        Path path = finder.findPath(player.getLocation(), target.getLocation(), target.getLocation(), player.getSizeX(), player.getSizeY());
        if (!path.isEmpty()) {
            WalkAction walk = new WalkAction(player, path) {
                @Override
                public void run() throws SuspendExecution {
                    super.run();

                    // Then use the object
                    player.use(item, item.getItem().getGroundOption(opt));
                }
            };
            player.getActions().queue(walk);
        } else {
            player.use(target, target.getItem().getGroundOption(option));
        }
    }
}