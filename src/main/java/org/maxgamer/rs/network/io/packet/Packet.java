package org.maxgamer.rs.network.io.packet;

/**
 * @author netherfoam
 */
public interface Packet {
    public int getOpcode();

    public int getLength();
}