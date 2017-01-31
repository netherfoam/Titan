package org.maxgamer.rs.network.io.rawhandler;

import org.maxgamer.rs.cache.XTEAKey;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.ScreenSettings;
import org.maxgamer.rs.network.AuthResult;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;
import org.maxgamer.rs.util.Log;
import org.maxgamer.rs.util.io.InputStreamWrapper;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class LoginRequestHandler extends RawHandler {
    private static BigInteger RSA_MODULUS;
    private static BigInteger RSA_EXPONENT;

    static {
        String priv = Core.getServer().getConfig().getString("rsa.private-key");
        String exp = Core.getServer().getConfig().getString("rsa.private-exponent");

        if (priv == null || exp == null) {
            Log.warning("world.yml >> rsa.private-key or rsa.private-exponent are null. Please correctly fill in the fields to use RSA.");
            Log.warning("To silence this message, set their values to 0");
            RSA_MODULUS = new BigInteger("0");
            RSA_EXPONENT = new BigInteger("0");
        } else {
            RSA_MODULUS = new BigInteger(priv, 16);
            RSA_EXPONENT = new BigInteger(exp, 16);
        }

        if (RSA_MODULUS.intValue() == 0) {
            RSA_MODULUS = null;
        }
        if (RSA_EXPONENT.intValue() == 0) {
            RSA_EXPONENT = null;
        }

        if (RSA_MODULUS == null || RSA_EXPONENT == null) {
            Log.debug("There is no RSA enabled on this server.");
        } else {
            Log.debug("RSA is enabled");
        }
    }

    public LoginRequestHandler(Session s) {
        super(s);
    }

    private void logon() throws AuthenticationException {
        if (Core.getServer().getLogon().isConnected() == false) {
            throw new AuthenticationException("LoginServer is offline, so login request has been declined.", AuthResult.ACCOUNT_INACCESSIBLE.LOGIN_SERVER_OFFLINE);
        }
    }

    private RSByteBuffer decodeRSA(byte[] rsaPayload) throws AuthenticationException {
        RSByteBuffer rsaEncrypted;
        if (RSA_EXPONENT != null && RSA_MODULUS != null) {
            rsaEncrypted = new RSByteBuffer(ByteBuffer.wrap(new BigInteger(rsaPayload).modPow(RSA_EXPONENT, RSA_MODULUS).toByteArray()));

            int rsaHeader = rsaEncrypted.readByte();
            if (rsaHeader == 10) {
                Log.debug("Client using correct RSA key");
                return rsaEncrypted;
            } else {
                Log.debug("Client doesn't appear to be using our RSA key.");
            }
        }

        rsaEncrypted = new RSByteBuffer(ByteBuffer.wrap(rsaPayload));
        int header = rsaEncrypted.readByte();
        if (header != 10) {
            Log.warning("Invalid RSA Header: " + header + ".");
            Log.warning("This may indicate that the client is using a different RSA key, or the protocol handling is incorrect");
            Log.warning("Dropping connection from " + getSession().getIP().getHostName());

            throw new AuthenticationException("RSA header mismatch, expected 10, got " + header, AuthResult.MALFORMED_PACKET);
        }

        Log.debug("Client is not using a RSA key");

        return rsaEncrypted;
    }

    @Override
    public void handle(RSByteBuffer buffer) {
        try {
            logon();

            int opcode = buffer.readByte() & 0xFF;

            //Length of data available. (~280 ish) - Packet size.
            int packetLength = buffer.readShort(); //Number of bytes remaining
            getSession().setRevision(buffer.readInt()); // Client version

            byte[] rsaPayload = new byte[(buffer.readShort() & 0xFFFF)];
            buffer.read(rsaPayload);
            RSByteBuffer rsaEncrypted = decodeRSA(rsaPayload);

            //Client seed?
            int[] keys = new int[4];
            for (int i = 0; i < keys.length; i++) {
                keys[i] = rsaEncrypted.readInt();
            }
            XTEAKey key = new XTEAKey(keys);

            rsaEncrypted.readLong(); //Appears to be zero always

            String pass = rsaEncrypted.readPJStr1();

            //Client UUID
            rsaEncrypted.readLong(); // client key, appears to be 0 always
            long uuid = rsaEncrypted.readLong(); // other client key, randomly generated every time client starts
            
            //The rest of the packet is encrypted
            byte[] block = new byte[packetLength - rsaPayload.length - 6];
            buffer.read(block);

            //Decrypt it
            ByteBuffer bb = ByteBuffer.wrap(block);
            key.decipher(bb, 0, block.length);

            //A nice way of reading.
            InputStreamWrapper in = new InputStreamWrapper(block);
            String name = in.readString();

            if (name.matches("[A-Za-z0-9_\\- ]{1,20}") == false) {
                Log.debug("User supplied invalid username: " + name);
                getSession().write(AuthResult.CHANGE_NAME.getCode());

                return;
            }

            if (opcode == 16 || opcode == 18) {
                // Game world login or rejoin request
                in.readByte(); //Unknown..

                //Screen settings
                int mode = in.read();
                int width = in.readShort();
                int height = in.readShort();
                boolean active = in.readByte() != 0; //is window selected, I assume.

                ScreenSettings ss = getSession().getScreenSettings();
                ss.setDisplayMode(mode);
                ss.setWidth(width);
                ss.setHeight(height);
                ss.setWindowActive(active);

                for (int i = 0; i < 24; i++) {
                    in.readByte();
                }
                in.readString(); //Settings

                in.readInt();
                for (int i = 0; i < 34; i++) {
                    in.readInt();
                }
                Core.getServer().getLogon().getAPI().authenticate(getSession(), name, pass, uuid, false);
            } else if (opcode == 19) {
                // Lobby login
                in.readByte(); // screen settings?
                in.readByte();
                for (int i = 0; i < 24; i++) {
                    in.readByte();
                }

                in.readInt();
                for (int i = 0; i < 34; i++) {
                    in.readInt();
                }

                //We are left with 4 unknown bytes. On my client they are (in hex) (0x24, 0x57, 0x42, 0x5C)
                while (in.available() > 0) {
                    in.readByte();
                }
                Core.getServer().getLogon().getAPI().authenticate(getSession(), name, pass, uuid, true);
            } else {
                throw new AuthenticationException("Unsupported opcode, expected 16/18/19, got " + opcode, AuthResult.MALFORMED_PACKET);
            }
        } catch (IOException | BufferUnderflowException e) {
            Log.warning("Failed to read login request: " + e.getMessage());
            Log.warning("Client is being kicked.");
            getSession().write(AuthResult.MALFORMED_PACKET.getCode());
            getSession().close(true);

            return;
        } catch (AuthenticationException e) {
            Log.debug(e.getMessage());
            getSession().write(e.getCode().getCode());
            getSession().close(true);

            return;
        }
    }

    private static class AuthenticationException extends Exception {
        private AuthResult code;

        public AuthenticationException(String message, AuthResult code) {
            this.code = code;
        }

        public AuthResult getCode() {
            return code;
        }
    }
}