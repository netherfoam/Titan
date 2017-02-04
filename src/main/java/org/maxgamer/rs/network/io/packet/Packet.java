package org.maxgamer.rs.network.io.packet;

/**
 * @author netherfoam
 */
public interface Packet {
    int getOpcode();

    int getLength();
}