package org.maxgamer.rs.model.interfaces.impl.primary;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.PrimaryInterface;

/**
 * @author netherfoam
 */
public class BookInterface extends PrimaryInterface {
    public static final short INTERFACE_ID = 960;
    //Component ID's of the lines (in order, 1-15) of left page
    private static final int[] LEFT_LINES = new int[]{49, 56, 61, 62, 54, 63, 55, 51, 60, 58, 53, 50, 57, 59, 52,};
    //Component ID's of the lines (in order, 1-15) of right page
    private static final int[] RIGHT_LINES = new int[]{33, 39, 36, 44, 37, 46, 40, 42, 34, 35, 38, 43, 47, 45, 41,};
    private int page = 1;

    public BookInterface(Player p) {
        super(p);
        setChildId(INTERFACE_ID);
    }

    @Override
    public void onOpen() {
        //Set left / right strings
        setPage(1);
        for (int line : LEFT_LINES) {
            setString(line, "");
        }
        for (int line : RIGHT_LINES) {
            setString(line, "");
        }
        super.onOpen();
    }

    public void setTitle(String text) {
        setString(69, text);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if (page <= 0) page = 1;
        if (page > getSize()) page = getSize();
        this.page = page;
        setString(70, "Page " + (getPage() * 2 - 1)); //Left page number
        setString(71, "Page " + (getPage() * 2)); //Right page number
    }

    public int getSize() {
        return 1;
    }

    public void setText(String text) {
        String[] lines = text.split("\n");

        for (int line = 0; line < 30; line++) {
            if (line < lines.length) {
                setLine(line, lines[line]);
            } else {
                setLine(line, "");
            }
        }
    }

    public void setLine(int line, String text) {
        if (line >= 30) {
            throw new IllegalArgumentException("Bad line given. Values are 0-29 inclusive, given " + line);
        }

        if (line < 15) {
            setString(LEFT_LINES[line], text);
        } else {
            setString(RIGHT_LINES[line - 15], text);
        }
    }

    @Override
    public boolean isMobile() {
        return false;
    }

    @Override
    public void onClick(int option, int buttonId, int slotId, int itemId) {
        if (option == 0) {
            if (buttonId == 74) { //'X' in top right
                getPlayer().getWindow().close(this);
            } else if (buttonId == 73) {
                if (getPage() < getSize()) {
                    setPage(getPage() + 1);
                    next();
                }
                player.getProtocol().sendInterface(this.isServerSidedClose(), this.getParent().getChildId(), this.childPos, this.getChildId());
            } else if (buttonId == 72) {
                if (getPage() > 1) {
                    setPage(getPage() - 1);
                    previous();
                }
                player.getProtocol().sendInterface(this.isServerSidedClose(), this.getParent().getChildId(), this.childPos, this.getChildId());
            }
        }
    }

    public void next() {
    }

    public void previous() {
    }
}