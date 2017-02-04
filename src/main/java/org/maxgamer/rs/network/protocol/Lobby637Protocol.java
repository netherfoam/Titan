package org.maxgamer.rs.network.protocol;

import java.util.Collection;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.logon.game.LogonAPI.RemoteWorld;
import org.maxgamer.rs.network.AuthResult;
import org.maxgamer.rs.network.EmailStatus;
import org.maxgamer.rs.network.LobbyPlayer;
import org.maxgamer.rs.network.io.packet.PacketManager;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;
import org.maxgamer.rs.network.io.stream.RSOutputStream;
import org.maxgamer.rs.structure.Util;

/**
 * @author netherfoam
 */
public class Lobby637Protocol extends LobbyProtocol {
    public static final PacketManager<LobbyPlayer> PACKET_MANAGER;

    static {
        PACKET_MANAGER = new PacketManager<>();
        PACKET_MANAGER.setHandler(12, new PacketProcessor<LobbyPlayer>() {
            @Override
            public void process(LobbyPlayer c, RSIncomingPacket p) throws Exception {
                //Heartbeat packet.
                c.write(new RSOutgoingPacket(99));
            }
        });

        PACKET_MANAGER.setHandler(84, new PacketProcessor<LobbyPlayer>() {
            @Override
            public void process(LobbyPlayer c, RSIncomingPacket p) throws Exception {
                //World query packet
            	c.getProtocol().sendWorldData();
            }
        });
    }

    public Lobby637Protocol(LobbyPlayer p) {
        super(p);
    }

    @Override
    public int getRevision() {
        return 637;
    }

    @Override
    public PacketManager<LobbyPlayer> getPacketManager() {
        return PACKET_MANAGER;
    }

    @Override
    public void sendUpdates() {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendMessage(int type, String userFrom, String text) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendMessage(String text) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendFriend(String name, boolean victimOnline, String world) {
        RSOutgoingPacket out = new RSOutgoingPacket(10);
        out.writeByte(0); //boolean 1 = true, else false
        out.writePJStr1(name);//Username
        out.writePJStr1(""); //Display name maybe? Uhm, I can't see anywhere where the client uses this but it reads it and saves it?

        out.writeShort(victimOnline ? 1 : 0);//Checks whether online or not.// World ID?
        out.writeByte(0); //byte
        if (victimOnline) {//<col=00FF00> is not necessary here
            //out.writePJStr1(Core.getServer().getDefinition().getWorldId() + ": " + Core.getServer().getDefinition().getText()); //World name
            out.writePJStr1(world);
            out.writeByte(0); //Some boolean, 1=true, else false
        }
        getPlayer().write(out);
    }

    @Override
    public void sendUnlockFriendsList() {
        RSOutgoingPacket out = new RSOutgoingPacket(10);
        getPlayer().write(out);
    }

    @Override
    public void sendIgnores(String ignore) {
        RSOutgoingPacket out = new RSOutgoingPacket(85);

        //Some bool, if last bit is not set, the name is added to the end of the list.
        //Otherwise it replaces the old name which .equals it?
        //The second last bit, is also some boolean value, which is only used if the
        //last bit is not set.
        out.writeByte(0);

        out.writePJStr1(ignore);
        out.writePJStr1(""); //Display name, or empty for same as ignore
        out.writePJStr1(ignore); //Last known as name
        out.writePJStr1(""); //Last known display name or empty for same as last known name
        getPlayer().write(out);
    }

    /**
     * Unlocks/resets the ignores list.
     */
    @Override
    public void sendUnlockIgnores() {
        getPlayer().write(new RSOutgoingPacket(12));
    }

    /**
     * Runs the client script to remove the given friend
     *
     * @param name the name of the friend case insensitive
     */
    @Override
    public void removeFriend(String name) {
        //invoke(removeFriendScript, -1, 5, "", name);
        //TODO
    }

    /**
     * Runs the client script to remove the given ignore
     *
     * @param name the name of the ignore case insensitive
     */
    @Override
    public void removeIgnore(String name) {
        //invoke(removeIgnoreScript, name);
        //TODO
    }

    @Override
    public void sendAuth(AuthResult result, String lastIp, long lastSeen) {
        RSOutputStream bb = new RSOutputStream(36 + lastIp.length() + getPlayer().getName().length());
        bb.write((byte) 0);
        bb.write((byte) 0);
        bb.write((byte) 0);
        bb.write((byte) 0);
        bb.write((byte) 0);
        bb.writeShort((short) 30); //Membership Days
        bb.writeShort((short) 1); //Recovery Questions
        bb.writeShort((short) 0); //Unread messages

        //RuneScape 2 started on day 11745 days after 1970.
        bb.writeShort((short) ((lastSeen / 86400000) - 11745)); //Time since last online

        if (Util.isIP(lastIp)) {
            bb.writeInt(Util.IPAddressToNumber(lastIp));
        } else {
            bb.writeInt(Util.IPAddressToNumber("0.0.0.0")); //First join
        }
        bb.write(EmailStatus.NO_EMAIL.getNetworkId()); //Email Status
        bb.writeShort((short) 0);
        bb.writeShort((short) 0);
        bb.write((byte) 0);

        bb.writePJStr2(getPlayer().getName());
        bb.writeByte((byte) 0);
        bb.writeInt(1);
        bb.writeShort((short) Core.getServer().getLogon().getWorldId());
        bb.writePJStr2(lastIp);

        byte[] data = bb.getPayload();
        getPlayer().getSession().write((byte) data.length);
        getPlayer().getSession().write(data);
    }

    @Override
    public void sendWorldData() {
        RSOutgoingPacket out = new RSOutgoingPacket(98);

        boolean sendWorldConfig = true;
        
        out.writeByte((byte) 1);
        out.writeByte((byte) 2);
        out.writeByte((byte) (sendWorldConfig ? 1 : 0));

        Collection<RemoteWorld> worlds = Core.getServer().getLogon().getAPI().getWorlds();

        if(sendWorldConfig) {
	        out.writeSmart(worlds.size());
	
	        for (RemoteWorld w : worlds) {
	            /**
	             * The country code for this world. This is used for the flag icon
	             * next to the Location. If this world has an activity (not empty)
	             * this will be ignored.
	             */
	            out.writeSmart(w.getCountry());
	            /**
	             * The location for the world. If this world has an activity, that
	             * activity will override this.
	             */
	            out.writePJStr2(w.getName());
	        }
	        
	        //Maybe one of these are currently selected world?
	        //I'm not terribly sure about either of these three.
	        out.writeSmart(0); //WorldStart
	        out.writeSmart(worlds.size() + 1); //WorldEnd
	        out.writeSmart(worlds.size()); //WorldCount
	
	        for (RemoteWorld w : worlds) {
	
	            out.writeSmart(w.getWorldId());
	            /**
	             * The location for the world. If we send a location (Say 0), with
	             * region "Australia" then we send another world with location 0 and
	             * region "America", then both worlds will appear as "Australia".
	             *
	             * This means we need to send a unique location for each unique
	             * world.
	             */
	            out.writeByte((byte) (w.getWorldId() - 1));
	            out.writeInt(w.getFlags());
	
	            /**
	             * If we send a non-empty string to the client, then it will display
	             * the activity as the primary text field, instead of displaying the
	             * country flag.
	             */
	            out.writePJStr2(w.getActivity());
	            out.writePJStr2(w.getIP());
	        }
	        out.writeInt(0x94DA4A87); // != 0
        }
        
        for (RemoteWorld remote : Core.getServer().getLogon().getAPI().getWorlds()) {
            out.writeSmart(remote.getWorldId());
            out.writeShort((short) remote.size());
        }
        
        getPlayer().write(out);
    }

    @Override
    public void sendSound(int i, int j, int k) {
        throw new RuntimeException("Not implemented");
    }
}
