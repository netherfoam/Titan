package org.maxgamer.rs.model.events.session;

import org.maxgamer.rs.logon.logon.LSEvent;
import org.maxgamer.rs.network.AuthResult;

/**
 * @author netherfoam
 */
public class AuthRequestEvent extends LSEvent {
    private AuthResult result;
    private String ip;
    private String name;
    private long uuid;

    public AuthRequestEvent(AuthResult result, String ip, String name, long clientUUID) {
        this.result = result;
        this.ip = ip;
        this.name = name;
        this.uuid = clientUUID;
    }

    public String getName() {
        return name;
    }

    public long getClientUUID() {
        return uuid;
    }

    public String getIP() {
        return ip;
    }

    public AuthResult getResult() {
        return result;
    }

    public void setResult(AuthResult result) {
        this.result = result;
    }
}