package org.maxgamer.rs.model.entity.mob.persona.player;

import java.util.ArrayList;

import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * @author netherfoam, alva
 */
public class Notes implements YMLSerializable {
	public static final int MAX_NOTES = 30;
	/**
	 * ArrayList of the notes, with a maximum capacity of 30.
	 */
	private ArrayList<Note> notes = new ArrayList<Note>(MAX_NOTES);
	
	/**
	 * The owner of the notes.
	 */
	protected final Player p;
	
	/**
	 * The note which the player selected.
	 */
	private int selectedNoteId = -1;
	
	/**
	 * A constructor for a new Notes for the given player
	 * @param p the player; the owner of the notes
	 */
	public Notes(Player p) {
		this.p = p;
	}
	
	/**
	 * Gets the value of the selected note, if no note is selected then it's
	 * default value is -1.
	 * @return value of the selected note.
	 */
	public int getSelectedNote() {
		return selectedNoteId;
	}
	
	/**
	 * Sets the value of the selected note, to the slotId.
	 * @param slotId the slotId of the note chosen to select.
	 */
	public void setSelectedNote(int slotId) {
		if (slotId >= MAX_NOTES) {
			slotId = -1;
		}
		selectedNoteId = slotId;
		p.getProtocol().sendConfig(1439, selectedNoteId);
	}
	
	/**
	 * Fetches the notes stored in the list which can't be instantiated.
	 * @return Return a deep copy of the notes stored in the list. It can be
	 *         empty.
	 */
	public ArrayList<Note> getNotes() {
		return (ArrayList<Note>) this.notes;
	}
	
	/**
	 * Deletes the note, corresponding the slot - note in ArrayList.
	 * @param slotId the slotId clicked ingame, is always a note.
	 */
	public void deleteNote(int slotId) {
		notes.remove(slotId);
		setSelectedNote(-1);//We don't want any note to be selected, so the value is -1
		listNotes();
	}
	
	/**
	 * Recolouring the note with a specific colour.
	 * @param colour the colour
	 */
	public void recolourNote(int colour) {
		if (selectedNoteId == -1 || selectedNoteId >= 30) return;
		notes.get(getSelectedNote()).setColour(colour);
		setSelectedNote(-1);//We don't want any note to be selected, so the value is -1
		listNotes();
	}
	
	/**
	 * Editing the note given the slotId corresponding the note in the arraylist
	 * and given the text.
	 * @param slotId the note in the list
	 * @param text the text of the note
	 */
	public void editNote(int slotId, String text) {
		if (text.length() > 50) {
			p.getProtocol().sendMessage("Your note may not exceed 50 characters.");
			return;
		}
		notes.get(slotId).setText(text);//Replaces only the text of the note with the input text.
		listNotes();
	}
	
	/**
	 * Select or unselect the Note according to it's slotId.
	 * @param slotId the slotId of the note.
	 */
	public void switchSelect(int slotId) {
		if (slotId >= 30 || slotId == -1) //slotId would be invalid, nothing will happen.
		return;
		if (getSelectedNote() == -1) //the selected note is empty/-1 and selectedNoteId can become the slotId 
		setSelectedNote(slotId);
		else //selectedNoteId is not -1 or invalid, so normally we would have to set it -1 (since we are unselecting it, but we could also select another slotId)
		setSelectedNote(slotId == getSelectedNote() ? -1 : slotId);//if the slotId == selectedNoteId, then we unselect it, if not then we select the other note/slot
	}
	
	/**
	 * Adding a note to the notes ArrayList
	 * @param text the text of the note
	 */
	public void addNote(String text) {
		if (text.length() > 50) {
			p.getProtocol().sendMessage("Your note may not exceed 50 characters.");
			return;
		}
		if (notes.size() < 30) notes.add(new Note(0, text));//Adding a note to the list with the input text and default colour.
		else {
			p.getProtocol().sendMessage("You can't add any more notes.");
			return;
		}
		listNotes();
	}
	
	/**
	 * Goes through the entire notes Arraylist and displays the content of it
	 * ingame in the notesinterface. Also displays the colour for every note
	 */
	public void listNotes() {
		for (int i = 0; i < 30; i++)
			p.getProtocol().sendGlobalString(149 + i, notes.size() <= i ? "" : notes.get(i).getText());
		p.getProtocol().sendConfig(1440, getNotesPrimaryColour());//First 15 notes of the notes interface. 
		p.getProtocol().sendConfig(1441, getNotesSecondaryColour());//Second 15 notes of the notes interface. 
	}
	
	/**
	 * The colour of every note for the first 15 notes. Credits to Dementhium
	 * team; also for getNotesSecondaryColour() and colourize()
	 * @return colour value.
	 */
	public int getNotesPrimaryColour() {
		int colour = 0;
		for (int i = 0; i < 16; i++) {
			if (getNotes().size() <= i) break;
			colour += colourizeNotes(getNotes().get(i).getColour(), i);
		}
		return colour;
	}
	
	/**
	 * The colour of every note for the second 15 notes.
	 * @return colour value.
	 */
	public int getNotesSecondaryColour() {
		int colour = 0;
		for (int i = 0; i < 14; i++) {
			if (getNotes().size() - 16 <= i) break;
			colour += colourizeNotes(getNotes().get(i + 16).getColour(), i);
		}
		return colour;
	}
	
	/**
	 * Colourize the note.
	 * @param colour the colour of the note.
	 * @param noteId the slot of the note
	 * @return colour of the note.
	 */
	public int colourizeNotes(int colour, int slotId) {
		return (1 << (2 * slotId)) * colour;
	}
	
	/**
	 * Drags the note from one slot to another.
	 * @param fromSlot orginal slot
	 * @param toSlot new slot
	 */
	public void dragNote(int fromSlot, int toSlot) {
		if (notes.size() <= fromSlot || notes.size() <= toSlot) {
			p.getProtocol().unlockInterfaceComponent(34, 44, false);//Prevents the Notes Interface from freezing.
			return;
		}
		notes.set(toSlot, notes.set(fromSlot, notes.get(toSlot)));
		listNotes();
	}
	
	/**
	 * Note class containing the constructor for a note.
	 */
	public class Note implements YMLSerializable {
		
		private int colour = 0;
		private String text = "";
		
		private Note() {
			
		}
		
		private Note(int colour, String text) {
			this();
			this.setColour(colour);
			this.setText(text);
		}
		
		public void setText(String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
		
		public void setColour(int colour) {
			this.colour = colour;
		}
		
		public int getColour() {
			return colour;
		}
		
		@Override
		public ConfigSection serialize() {
			ConfigSection s = new ConfigSection();
			s.set("colour", this.colour);
			s.set("text", this.text);
			return s;
		}
		
		@Override
		public void deserialize(ConfigSection map) {
			this.colour = map.getInt("colour", this.colour); //Default to current colour
			this.text = map.getString("text", this.text); //Default to current text
		}
		
		@Override
		public String toString() {
			return text;
		}
	}
	
	/**
	 * Saves the notes to YML file.
	 */
	@Override
	public ConfigSection serialize() {
		ConfigSection map = new ConfigSection();
		for (int i = 0; i < notes.size(); i++) {
			map.set("" + i, notes.get(i).serialize());
		}
		return map;
	}
	
	/**
	 * Loading the notes from YML file.
	 */
	@Override
	public void deserialize(ConfigSection map) {
		for (String key : map.getKeys()) {
			Note note = new Note();
			note.deserialize(map.getSection(key));
			notes.add(note);
		}
		listNotes();
	}
}
