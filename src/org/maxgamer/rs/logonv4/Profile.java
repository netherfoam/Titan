package org.maxgamer.rs.logonv4;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import net.crackstation.hash.PasswordHash;

import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.structure.dbmodel.Mapping;
import org.maxgamer.rs.structure.dbmodel.Transparent;

/**
 * Represents a user profile, one that may be offline or online.
 * @author netherfoam
 */
public class Profile extends Transparent {
	/**
	 * The prefix we use for passwords in the database. If a password does not
	 * start with this prefix, then we can assume the password is not hashed and
	 * must be hashed at the first available opportunity.
	 */
	protected static final String PASS_HASH_PREFIX = "$PBKDF2$";
	
	/**
	 * Validates the given username. If it is empty or contains letters which
	 * are not allowed, then this method returns false. Otherwise, true.
	 * @param user the username to validate
	 * @return true if the user is valid
	 */
	public static boolean isValidUser(String user) {
		if (user.isEmpty()) return false;
		if (user.matches("[^A-Za-z0-9_]")) return false;
		if (user.length() > 20) return false; //Too long
		return true;
	}
	
	/**
	 * Validates the given password. Currently this just ensures that the
	 * password must not be empty. Otherwise, returns true if it is a valid
	 * password. Passwords are hashed, and thus any character is allowed in the
	 * password and any length
	 * @param pass the password to validate
	 * @return true if the password is valid, false if it is not
	 */
	public static boolean isValidPass(String pass) {
		if (pass.isEmpty()) return false;
		return true;
	}
	
	private ProfileManager manager;
	
	@Mapping
	private String user;
	@Mapping
	private String user_clean;
	@Mapping
	private String pass;
	@Mapping
	private String lastIp;
	@Mapping
	private long lastSeen;
	@Mapping
	private int rights;
	
	/**
	 * Private constructor
	 */
	protected Profile(ProfileManager m, String name) {
		super("profiles", new String[]{"user_clean"}, new Object[]{name.toLowerCase()});
		this.manager = m;
		this.user_clean = name.toLowerCase();
		this.user = name;
	}
	
	public Profile(ProfileManager m, String name, String pass, long lastSeen, String lastIp){
		this(m, name);
		this.setPass(pass);
		this.setLastSeen(lastSeen);
		this.setLastIP(lastIp);
	}
	
	/**
	 * The name of this player, not "clean"
	 * @return the name of this player
	 */
	public String getName() {
		return user;
	}
	
	/**
	 * The clean name, all lowercase with underscores '_' instead of spaces ' '
	 * @return The clean name, all lowercase with underscores '_' instead of
	 *         spaces ' '
	 */
	public String getCleanName() {
		return user_clean;
	}
	
	/**
	 * Returns true if the given password is valid. This uses a hashing
	 * function. If the function is not found, the stacktrace is printed and
	 * this method returns false. This does not update the database, the
	 * autosave does that.
	 * @param raw the raw text password, eg 'alpha123'
	 * @return true if the password is valid
	 */
	public boolean isPass(String raw) {
		String real = this.pass;
		if (real == raw) return true;
		if (raw == null) return false;
		
		try {
			if (real.startsWith(PASS_HASH_PREFIX) == false) {
				//This password is not encrypted
				real = PASS_HASH_PREFIX + PasswordHash.hash(real);
				this.pass = real;
			}
			
			real = real.substring(PASS_HASH_PREFIX.length());
			
			return PasswordHash.isValid(raw, real);
		}
		catch (NoSuchAlgorithmException e) {
			Log.severe("Failed to hash password!");
			e.printStackTrace();
			return false;
		}
		catch (InvalidKeySpecException e) {
			Log.severe("Failed to hash password!");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Sets the password for this profile to the raw text.
	 * @param raw the raw text, this will be hashed
	 */
	public void setPass(String raw) {
		try {
			raw = PASS_HASH_PREFIX + PasswordHash.hash(raw);
		}
		catch (Exception e) {
			Log.debug("Failed to hash password.");
			e.printStackTrace();
			return;
		}
		this.pass = raw;
	}
	
	/**
	 * Returns the last known IP. May be empty.
	 * @return The last known IP, may be an empty String - eg ""
	 */
	public String getLastIP() {
		return this.lastIp;
	}
	
	/**
	 * The last time this player was seen in epoch milliseconds
	 * @return The last time this player was seen in epoch milliseconds
	 */
	public long getLastSeen() {
		return this.lastSeen;
	}
	
	/**
	 * The admin rights of this player. 0 represents ordinary, 1 represents a
	 * playermod, 2 represents an admin
	 * @return the rights level of this player
	 */
	public int getRights() {
		return rights;
	}
	
	/**
	 * Sets the rights of this player
	 * @param rights the new rights level
	 */
	public void setRights(int rights) {
		this.rights = rights;
	}
	
	/**
	 * Sets the last seen time for this player
	 * @param epochMS the last seen time in milliseconds since epoch time, 1970.
	 */
	public void setLastSeen(long epochMS) {
		this.lastSeen = epochMS;
	}
	
	/**
	 * Sets the last known IP for this profile.
	 * @param ip the last known IP
	 */
	public void setLastIP(String ip) {
		this.lastIp = ip;
	}
	
	public void update() throws SQLException {
		this.update(manager.getConnection());
	}
	
	@Override
	public int hashCode() {
		return this.getName().toLowerCase().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && this.getClass() == o.getClass()) {
			Profile p = (Profile) o;
			if (p.getName().equalsIgnoreCase(this.getName())) {
				return true;
			}
		}
		return false;
	}
}