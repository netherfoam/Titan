package org.maxgamer.rs.network;

/**
 * @author netherfoam
 */
public enum EmailStatus {
	NO_EMAIL((byte) 0), PENDING_PARENTAL_CONFIRMATION((byte) 1), PENDING_CONFIRMATION((byte) 2), REGISTERED((byte) 3);
	
	private byte networkId;
	
	private EmailStatus(byte networkId) {
		this.networkId = networkId;
	}
	
	public byte getNetworkId() {
		return networkId;
	}
}