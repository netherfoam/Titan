package org.maxgamer.rs.network.io.packet;

import org.maxgamer.rs.network.Client;

/**
 * @author netherfoam
 */
public interface PacketProcessor<T extends Client> {
    void process(T c, RSIncomingPacket in) throws Exception;
}