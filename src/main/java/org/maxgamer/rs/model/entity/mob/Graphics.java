package org.maxgamer.rs.model.entity.mob;

/**
 * @author 'Mystic Flow
 */
public class Graphics {

    public static final Graphics STUNNED_GRAPHIC = new Graphics(80, 100 << 16);
    public static final Graphics RESET = new Graphics(-1, 0, 120);

    private final int delay;
    private final int height;
    private final int id;

    public Graphics(int id) {
        this(id, 0);
    }

    public Graphics(int id, int delay) {
        this(id, delay, 0);
    }

    public Graphics(int id, int delay, int height) {
        this.id = id;
        this.delay = delay;
        this.height = height;
    }

    public int getDelay() {
        return delay;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }
}
