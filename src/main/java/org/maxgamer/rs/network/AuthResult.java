package org.maxgamer.rs.network;

/**
 * @author netherfoam
 */
public enum AuthResult {
    //Many of these are taken from <a href="http://rsps.wikia.com/wiki/317_Protocol#Regarding_response_code_15>317 Protocol Docs</a>.
    //They may be inacurate for this version, but they're our best guess.

    /**
     * Could not display video advertising logging in
     */
    ADVERT_FAILED(1),

    /**
     * Successful login request
     */
    SUCCESS(2),

    /**
     * Invalid password supplied
     */
    INVALID_PASSWORD(3),

    /**
     * user is banned
     */
    BANNED(4),

    /**
     * User is already online
     */
    ALREADY_ONLINE(5),

    /**
     * Client out of date
     */
    CLIENT_OUT_OF_DATE(6),

    /**
     * World is full
     */
    WORLD_FULL(7),

    /**
     * Couldnt contact login server
     */
    LOGIN_SERVER_OFFLINE(8),

    /**
     * Too many connections from that IP
     */
    IP_LIMIT(9),

    /**
     * Unknown
     */
    BAD_SESSION_ID(10),

    /**
     * Something went wrong, try again
     */
    TRY_AGAIN(11),

    /**
     * World is members only, but you're f2p
     */
    MEMBER_ONLY_WORLD(12),

    /**
     * Could not complete login. Please try using a different world.
     */
    TRY_ANOTHER_WORLD(13),

    /**
     * "The server is being updated. Please wait 1 minute and try again."
     */
    SERVER_IS_BEING_UPDATED(14),

    /**
     * UNexpected server response
     */
    UNKNOWN(15),

    /**
     * "Login attempts exceeded. Please wait 1 minute and try again."
     */
    LOCKED_OUT(16),

    /**
     * "You are standing in a members-only area. To play on this world move to a free area first."
     */
    MEMBERS_ONLY_AREA(17),

    /**
     * Your account has been locked
     */
    ACCOUNT_LOCKED(18),

    /**
     * Fullscreen is currently a member only feature
     */
    FULLSCREEN_IS_MEMBERS(19),

    /**
     * "Invalid loginserver requested. Please try using a different world."
     */
    INVALID_LOGINSERVER_REQUESTED(20),

    /**
     * "You have only just left another world. Your profile will be transferred in: (number) seconds."
     */
    AWAITING_TRANFER(21),

    /**
     * Malformed login packet
     */
    MALFORMED_PACKET(22),

    /**
     * No reply from login server
     */
    NO_LOGIN_REPLY(23),

    /**
     * Profile failed to load (Exception during loading?)
     */
    ERROR_LOADING_PROFILE(24),

    /**
     * This computers address was used to break our rules
     */
    BANNED_IP(26),

    /**
     * Service unavailable
     */
    SERVICE_UNAVAILABLE(27),

    /**
     * This is not a members account, please choose a free world
     */
    MEMBERS_ONLY_WORLD(30),

    //See here for 718+
    //http://www.rune-server.org/runescape-development/rs-503-client-server/configuration/539162-718-client-packets.html
    /**
     * You must change your accounts display name before you can login
     */
    CHANGE_NAME(31),

    /**
     * Your account has negative membership credit. Please log into the billing
     * system to add credit to your account.
     */
    MEMBERSHIP_EXPIRED(32),

    /**
     * Your session has expired. Please click "back" in your browser to renew
     * it.
     */
    SESSION_EXPIRED(35),

    /**
     * Unable to connect: authentication server offline.
     */
    AUTH_SERVER_OFFLINE(36),

    /**
     * Your account is currently inaccessible. Please try again in a few minutes
     */
    ACCOUNT_INACCESSIBLE(37),

    /**
     * The instance you tried to join no longer exists. Please try using a
     * different world
     */
    INSTANCE_DELETED(39),

    /**
     * You need a members account to log in to this world. Please subscribe or
     * use a different world
     */
    MEMBERS_ACC_REQUIRED(40),

    /**
     * The instance you tried to join is full. Please try back later or try
     * using a different world.
     */
    INSTANCE_FULL(41),

    /**
     * Error connecting to server.
     */
    ERROR_CONNECTING(42),

    /**
     * Our systems are currently unavailble. Please try again in a few minutes
     */
    SYSTEM_UNAVAILABLE(44),

    /**
     * This instance is marked for deletion/rebuild. Please try using a
     * different world
     */
    INSTANCE_MARKED_FOR_DELETION(46),

    /**
     * You need to validate your email address to login.
     */
    EMAIL_NOT_VALIDATED(47),

    /**
     * Your game session has now ended. To play again please close your browser
     * tab/window and wait 5 minutes before reloading the game
     */
    SESSION_ENDED(48);

    /**
     * The network code
     */
    private byte code;

    AuthResult(int code) {
        this.code = (byte) code;
    }

    public static AuthResult get(int code) {
        for (AuthResult result : values()) {
            if (result.code == code) {
                return result;
            }
        }
        return null;
    }

    /**
     * The byte value of this result, which can be sent to the client.
     *
     * @return The byte value of this result, which can be sent to the client.
     */
    public byte getCode() {
        return code;
    }
}