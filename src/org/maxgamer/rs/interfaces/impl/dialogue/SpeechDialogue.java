package org.maxgamer.rs.interfaces.impl.dialogue;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

public abstract class SpeechDialogue extends ThoughtDialogue {
	private static final int BIG_FACE = 1;
	private static final int SMALL_FACE = 2;
	
	/**
	 * Represents a special value that indicates the player's face should be used, instead
	 * of an NPC's face.
	 */
	public static final int PLAYER_FACE = -1;
	
	public static final int REALLY_SAD = 9760;
	public static final int SAD = 9765;
	public static final int DEPRESSED = 9770;
	public static final int WORRIED = 9775;
	public static final int SCARED = 9780;
	public static final int MEAN_FACE = 9785;
	public static final int MEAN_HEAD_BANG = 9790;
	public static final int EVIL = 9795;
	public static final int WHAT_THE_CRAP = 9800;
	public static final int CALM = 9805;
	public static final int CALM_TALK = 9810;
	public static final int TOUGH = 9815;
	public static final int SNOBBY = 9820;
	public static final int SNOBBY_HEAD_MOVE = 9825;
	public static final int CONFUSED = 9830;
	public static final int DRUNK_HAPPY_TIRED = 9835;
	public static final int TALKING_ALOT = 9845;
	public static final int HAPPY_TALKING = 9850;
	public static final int BAD_ASS = 9855;
	public static final int THINKING = 9860;
	public static final int COOL_YES = 9864;
	public static final int SECRELTY_TALKING = 9838;
	
	/**
	 * The ID for the speaker's face.
	 */
	private int face;
	
	/**
	 * The emote that the speaker when talking
	 */
	private int emote = CALM_TALK;
	
	/**
	 * True will make the speakers face close up and larger
	 */
	private boolean big = false;
	
	/**
	 * The name of the person chatting
	 */
	private String chatter;
	
	/**
	 * Constructs a new DialogueSpeech.  This dialogue represents when a Mob is talking, and allows
	 * the placement of a face, the name of the speaker, and an animation to be applied to the speaker's
	 * head on the interface.  This class extends DialogueThought.  This, naturally, only has one option,
	 * to continue - and is therefore not a fork.
	 * @param p the player
	 */
	public SpeechDialogue(Player p) {
		super(p);
		chatter = p.getName();
		face = PLAYER_FACE;
	}
	
	/**
	 * Sets the face of the speaker that appears on this dialogue
	 * @param npcId the ID of the speaker, use DialogueSpeech.PLAYER_FACE to indicate that the current player's face should be used instead
	 * @param chatter the name of the chatter, eg. the player's name or the NPC's name.
	 * @param emote the emote for the face to use while talking - See DialogueSpeech constants for appropriate emotes.
	 */
	public void setFace(int npcId, String chatter, int emote){
		this.face = npcId;
		this.emote = emote;
		
		if(chatter == null && npcId == PLAYER_FACE){
			chatter = getPlayer().getName();
		}
		else{
			this.chatter = chatter;
		}
		
		
		setChildId();
	}
	
	/**
	 * Setting this to true will enlarge the speaker on the interface
	 * @param big true to make the speaker larger
	 */
	public void setBigFace(boolean big){
		this.big = big;
	}
	
	/**
	 * Returns true if the speakers face will be large
	 * @return true if the speakers face will be large
	 */
	public boolean isBigFace(){
		return this.big;
	}
	
	/**
	 * Returns the ID of the speaker. May return DialogueSpeech.PLAYER_FACE (-1) to indicate the player is speaking.
	 * Otherwise, this is the ID of a NPC definition. Defaults to the player's face.
	 * @return the speaker ID
	 */
	public int getSpeakerID(){
		return this.face;
	}
	
	/**
	 * Sets the emote that will be used by the speaker when talking.  This can be happy, sad, etc. See DialogueSpeech constants
	 * for a list of appropriate emotes.
	 * @return the emote the speaker will use.
	 */
	public int getSpeakerEmote(){
		return this.emote;
	}
	
	@Override
	protected void setChildId(){
		if(this.text != null){
			int baseId;
			
			if(face == -1){
				baseId = 63; //Displaying the player's face
			}
			else{
				baseId = 240; //Displaying a NPC's face 
			}
		
			this.setChildId(baseId + text.length);
		}
	}
	
	@Override
	public void onOpen(){
		this.setString(3, chatter);
		for(int i = 0; i < text.length; i++){
			this.setString(i + 4, this.text[i]);
		}
		
		//The different component determines how big the speakers face will appear
		int component = big ? BIG_FACE : SMALL_FACE; 
		
		//Renders the speakers head
		if(face == PLAYER_FACE){
			getPlayer().getProtocol().sendPlayerOnInterface(this.getChildId(), component);
		}
		else{
			getPlayer().getProtocol().sendNPCOnInterface(this.getChildId(), component, face);
		}
		
		//Animates the speakers head
		getPlayer().getProtocol().sendInterAnimation(emote, this.getChildId(), component);
	}
}
