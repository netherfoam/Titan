package org.maxgamer.rs.logonv4.game;

import java.io.ByteArrayInputStream;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.WorldFullException;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.logonv4.LSIncomingPacket;
import org.maxgamer.rs.logonv4.Opcode;
import org.maxgamer.rs.logonv4.OpcodeDecoder;
import org.maxgamer.rs.logonv4.game.LogonAPI.AuthRequest;
import org.maxgamer.rs.logonv4.game.LogonAPI.RemoteWorld;
import org.maxgamer.rs.model.entity.mob.persona.player.NoSuchProtocolException;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.AuthResult;
import org.maxgamer.rs.network.LobbyPlayer;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.rawhandler.GamePacketHandler;
import org.maxgamer.rs.network.io.rawhandler.LobbyHandler;
import org.maxgamer.structure.configs.ConfigSection;

/**
 * @author netherfoam
 */
public class LogonDecoder extends OpcodeDecoder<LSIncomingPacket> {
	private LogonAPI api;
	
	public LogonDecoder(LogonAPI api) {
		this.api = api;
	}
	
	//TODO
	@Opcode(opcode = 1)
	public void decodeClientStatus(LSIncomingPacket in) {
		int state = in.readByte() & 0xFF;
		int world = in.readByte() & 0xFF;
		String name = in.readPJStr1();
		
		RemoteWorld w = api.worlds.get(world);
		assert w != null : "World not found " + world;
		
		if (state == 0) {
			//Player is logging off			
			w.players.remove(name);
			//TODO: Throw event
		}
		else if (state == 1) {
			//Player is logging on
			w.players.add(name);
			//TODO: Throw event
		}
		else {
			throw new IllegalArgumentException("Bad state, given " + state);
		}
	}
	
	@Opcode(opcode = 2)
	public void decodeWorldStatus(LSIncomingPacket in) {
		int state = in.readByte() & 0xFF;
		int world = in.readByte() & 0xFF;
		RemoteWorld w = api.worlds.get(world);
		
		if (state == 0) {
			//World is being removed
			assert w != null : "World is null, but removing it?";
			assert w.players.size() == 0 : "Players are still on world, but world is being destroyed";
			api.worlds.remove(world);
		}
		else if (state == 1) {
			assert w == null : "Adding a world, but it is not null";
			
			w = new RemoteWorld();
			//World is being added
			w.worldId = world;
			w.ip = in.readPJStr1();
			w.name = in.readPJStr1();
			w.activity = in.readPJStr1();
			w.country = in.readByte() & 0xFF;
			w.flags = in.readByte() & 0xFF;
			api.worlds.put(world, w);
		}
		else {
			throw new IllegalArgumentException("Bad state, given " + state);
		}
	}
	
	//TODO
	@Opcode(opcode = 3)
	public void decodeSessionResponse(final LSIncomingPacket in) {
		Core.submit(new Runnable() {
			@Override
			public void run() {
				//???//
				int sessionId = in.readInt();
				Log.debug("Got response for session request for session# " + sessionId);
				AuthResult result = AuthResult.get(in.readByte() & 0xFF);
				
				Session session = Core.getServer().getNetwork().getSessionByID(sessionId);
				if (session == null || session.isConnected() == false) {
					return;
				}
				
				AuthRequest req = api.authRequests.get(sessionId);
				
				String lastIp = null;
				long lastSeen = -1;
				
				do {
					if (result != AuthResult.SUCCESS) {
						break;
					}
					
					ConfigSection config = new ConfigSection();
					lastIp = in.readPJStr1();
					lastSeen = in.readLong();
					
					byte[] payload = new byte[in.readInt()];
					in.read(payload);
					try {
						config = new ConfigSection(new ByteArrayInputStream(payload));
					}
					catch (Exception e) {
						e.printStackTrace();
						result = AuthResult.ERROR_LOADING_PROFILE;
						break;
					}
					
					if (req.lobby) {
						LobbyPlayer player;
						try {
							player = new LobbyPlayer(session, req.name, req.clientUUID);
							session.write(result.getCode());
							
							player.getProtocol().sendAuth(result, lastIp, lastSeen);
							player.deserialize(config);
							session.setHandler(new LobbyHandler(session, player));
							return;
						}
						catch (NoSuchProtocolException e) {
							result = AuthResult.CLIENT_OUT_OF_DATE;
							break;
						}
					}
					else {
						Player player;
						try {
							player = new Player(req.name, session, req.clientUUID);
							session.write(result.getCode());
							
							session.setHandler(new GamePacketHandler(session, player));
							player.deserialize(config);
							player.load(); //Gamepane is sent here, as well as some other interfaces
							return;
						}
						catch (NoSuchProtocolException e) {
							result = AuthResult.CLIENT_OUT_OF_DATE;
							break;
						}
						catch (WorldFullException e) {
							result = AuthResult.WORLD_FULL;
							break;
						}
					}
				} while (false);
				//We failed with some kind of code
				Log.debug("Error logging in: " + result);
				session.write(result.getCode());
			}
		}, false);
	}
	
	@Opcode(opcode = 4)
	public void decodePing(LSIncomingPacket in) {
		//Nothing
	}
}