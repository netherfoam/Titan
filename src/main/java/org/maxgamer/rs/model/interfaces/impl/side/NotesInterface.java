package org.maxgamer.rs.model.interfaces.impl.side;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.SettingsBuilder;
import org.maxgamer.rs.model.interfaces.SideInterface;
import org.maxgamer.rs.model.interfaces.Window;
import org.maxgamer.rs.model.interfaces.impl.chat.StringRequestInterface;

/**
 * @author netherfoam
 */
public class NotesInterface extends SideInterface {
    private static SettingsBuilder SETTINGS = new SettingsBuilder();

    static {
        SETTINGS.setSecondaryOption(0, true);
        SETTINGS.setSecondaryOption(1, true);
        SETTINGS.setSecondaryOption(2, true);
        SETTINGS.setSecondaryOption(3, true);
        SETTINGS.setUseOnSettings(false, false, false, false, false, false);
        SETTINGS.setInterfaceDepth(2);
    }

    public NotesInterface(Player p) {
        //super(p, (short) 34, (short) 101); //Alva
        super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 217 : 102)); //Netherfoam - Changed childPos ids
        setChildId(34);
        this.setAccessMask(SETTINGS.getValue(), 0, 29, 9);
        player.getProtocol().unlockInterfaceComponent(34, 13, true);//Unlocks the actual Notes display
        player.getProtocol().unlockInterfaceComponent(34, 3, true);//Unlocks the +add notes button
        player.getProtocol().sendConfig(1439, -1);//Unselected as default
        player.getProtocol().sendConfig(1437, 1);//No notes String on Notes display - Unlocks the actual adding of notes.
        player.getNotes().listNotes();
    }

    @Override
    public boolean isMobile() {
        return true;
    }

    @Override
    public void onClick(int option, int buttonId, final int slotId, int itemId) {
        switch (buttonId) {
            case 3:
                getPlayer().getWindow().open(new StringRequestInterface(getPlayer(), "Add your note:") {
                    @Override
                    public void onInput(String text) {
                        player.getNotes().addNote(text);
                    }
                });
                return;
            case 8:
            case 11:
                if (option == 1) {
                    player.getNotes().getNotes().clear();
                    player.getNotes().listNotes();
                } else if (option == 0 && player.getNotes().getSelectedNote() != -1) {
                    player.getNotes().deleteNote(player.getNotes().getSelectedNote());
                    player.getNotes().setSelectedNote(-1);
                } else player.sendMessage("Invalid note selected to delete!");
                break;
            case 9:
                switch (option) {
                    case 0:
                        player.getNotes().switchSelect(slotId);
                        break;
                    case 1:
                        getPlayer().getWindow().open(new StringRequestInterface(getPlayer(), "Edit your note:") {
                            @Override
                            public void onInput(String text) {
                                player.getNotes().editNote(slotId, text);
                            }
                        });
                        return;
                    case 2:
                        player.getNotes().setSelectedNote(slotId);
                        player.getProtocol().unlockInterfaceComponent(34, 16, true);//Shows the colour selection component
                        break;
                    case 3:
                        player.getNotes().deleteNote(slotId);
                        break;
                }
                break;
            case 35:
            case 37:
            case 39:
            case 41:
                int colour = ((buttonId - 35) / 2);
                player.getNotes().recolourNote(colour);
                player.getNotes().setSelectedNote(-1);
                player.getProtocol().unlockInterfaceComponent(34, 16, false);//Hides colour selection component.
                break;
        }
    }

    @Override
    public void onDrag(Window to, int fromItemId, int toItemId, int tabId, int fromSlot, int toSlot) {//TODO: dragging to delete button --> deleting note.
        player.getNotes().dragNote(fromSlot, toSlot);
    }
}