package org.maxgamer.rs.logonv4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.crackstation.hash.PasswordHash;

import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.structure.sql.Database;

/**
 * @author netherfoam
 */
public class ProfileManager {
	private Database db;
	
	public ProfileManager(Database db) {
		this.db = db;
	}
	
	/**
	 * Fetches the profile for the given username from the database. This method
	 * queries the database in the current thread and returns the value, and
	 * thus could take a long time to complete. You should avoid this in the
	 * main thread. This will return a different Profile object for the same
	 * user name if the method is invoked twice.
	 * @param user The name of the user, case insensitive.
	 * @return The profile, or null if none was found
	 * @throws SQLException If the database could not be accessed.
	 */
	public Profile get(String user) throws SQLException {
		Profile p = null;
		
		Connection con = db.getConnection();
		PreparedStatement ps = con.prepareStatement("SELECT * FROM profiles WHERE user_clean = ? LIMIT 0,1");
		ps.setString(1, user.toLowerCase());
		ResultSet rs = ps.executeQuery();
		
		if (rs.next()) {
			p = new Profile(this);
			p.load(rs);
		}
		rs.close();
		ps.close();
		con.close();
		return p; //Possibly null
	}
	
	/**
	 * Creates a new profile with the given username and password and inserts
	 * them into the database. This query is performed on the current thread and
	 * returns a value, and therefore may take a long time to complete. You
	 * should avoid calling this in the main thread.
	 * @param user The username for the profile
	 * @param pass The password for the profile (raw)
	 * @return the profile, not null.
	 * @throws SQLException If the account could not be created, eg user exists.
	 *         or the database is unavailable
	 */
	public Profile create(String user, String pass, String ip) throws SQLException {
		try {
			pass = Profile.PASS_HASH_PREFIX + PasswordHash.hash(pass);
		}
		catch (Exception e) {
			//In this case, pass has not changed and will be used later.
			Log.severe("Failed to hash password");
			e.printStackTrace();
		}
		
		Profile p = new Profile(this);
		p.setField("user", user);
		p.setField("user_clean", user.toLowerCase());
		p.setField("pass", pass);
		p.setField("lastSeen", System.currentTimeMillis());
		p.setField("lastIp", ip);
		
		Connection con = db.getConnection();
		p.insert(con);
		con.close();
		return p; //Not null
	}
	
	public Connection getConnection() throws SQLException {
		return db.getConnection();
	}
}