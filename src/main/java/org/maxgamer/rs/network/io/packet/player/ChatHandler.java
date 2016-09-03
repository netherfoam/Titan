package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.mob.persona.player.ClanMessageEvent;
import org.maxgamer.rs.network.io.Huffman;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.util.Chat;

/**
 * @author netherfoam
 */
public class ChatHandler implements PacketProcessor<Player> {

    public static final int PUBLIC_CHAT = 16;
    public static final int PRIVATE_CHAT = 41;
    public static final int QUICK_PRIVATE_CHAT = 44;
    public static final int QUICK_PUBLIC_CHAT = 61;

    @Override
    public void process(Player p, RSIncomingPacket in) throws Exception {
        if (in.getOpcode() == PUBLIC_CHAT) {
            //Opcode 16, var byte size.
            int effects = in.readShort() & 0xFFFF;
            int numChars = in.readByte() & 0xFF;

            byte[] text = new byte[in.available()];
            in.read(text);

            String s;
            try {
                s = Huffman.decompressHuffman(text, numChars);
            } catch (Exception e) {
                p.getCheats().log(5, "Bad Huffman Chat Message");
                return;
            }

            if (s.length() > 255) {
                p.getCheats().log(10, "Excessively long chat message (length " + s.length() + ")");
                return;
            }

            if (s.startsWith("::")) {
                //CommandManager handles command events.
                Core.getServer().getCommands().handle(p, s);
                return;
            }

            if (p.getConfig().getBoolean("clan.message", false) == true) {
                ClanMessageEvent e = new ClanMessageEvent(p, s, p.getRights());
                e.call();
                p.getConfig().set("clan.message", false);
                return;
            }

            p.chat(s, effects);
        } else if (in.getOpcode() == PRIVATE_CHAT) {
            String victim = in.readPJStr1().toLowerCase();
            if (victim == null) return;
            int numChars = in.readByte();
            byte[] text = new byte[in.available()];
            in.read(text);

            String s;
            try {
                s = Huffman.decompressHuffman(text, numChars);
            } catch (Exception e) {
                p.getCheats().log(5, "Bad Huffman Private Message");
                return;
            }

            String outMessage = Huffman.decompressHuffman(text, numChars);
            if (outMessage == null) return;

            outMessage = Chat.grammar(s);

            if (outMessage.length() > 255) {
                //Could happen if we set the message badly in the event,
                //or if the grammar/capsBlock calls run foul.
                outMessage = outMessage.substring(0, 255);
            }

            // The sender gets told they're sending the message
            p.getProtocol().sendPrivateMessage(victim, outMessage);

            //TODO: Use Server.getClient()
            Persona pers = Core.getServer().getPersona(victim);
            if (pers instanceof Player) {//TODO: Quite some checks and mute, ipmute in future.
                Player receiver = (Player) pers;
                receiver.getProtocol().receivePrivateMessage(p.getName(), p.getRights(), outMessage);
            }
        }
    }

}