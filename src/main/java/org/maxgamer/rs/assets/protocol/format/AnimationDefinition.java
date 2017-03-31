package org.maxgamer.rs.assets.protocol.format;

import org.maxgamer.rs.assets.protocol.RSInputStream;
import org.maxgamer.rs.core.server.ServerTicker;
import org.maxgamer.rs.util.io.ByteBufferInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class AnimationDefinition {
    public static final int FRAMES_PER_TICK = 24 * 600 / ServerTicker.getTickDuration();
    static int[][] anIntArrayArray814;
    int anInt807;
    int[] anIntArray808;
    int anInt809 = -1;
    int[] anIntArray810;
    /**
     * Possibly the duration of each stage, maybe 24 = 1 second (or 1 tick?) 90
     * frames in 2.28secs larger means longer. Settling on 15 frames per tick or
     * 25 frames per second.
     */
    int[] durations;
    boolean aBoolean812 = false;
    boolean[] aBooleanArray813;
    int[] anIntArray815;
    int anInt816;
    boolean aBoolean817;
    /**
     * Possibly the individual flags or stage ids of the animation. The highest
     * order of bytes (<< 10) are separate values to the lower order (under 10
     * bits)
     */
    int[] components;
    int anInt819;
    int anInt820;
    int anInt821;
    int[][] anIntArrayArray822;
    boolean aBoolean823;
    boolean aBoolean825;
    int animationId;
    int anInt828;
    /**
     * Appears to be '6' for attacking animations, and '0' for defensive ones?
     * '10' for dragon claws? Perhaps this is a weapon wield type, or an Attack
     * Style or WeaponGroup?
     */
    int anInt829;
    private int[] anIntArray827;

    public AnimationDefinition(ByteBuffer file) throws IOException {
        this.load(new RSInputStream(new ByteBufferInputStream(file)));
        if (file.remaining() > 0) {
            throw new IOException("End of File detected, but not End of Stream");
        }
    }

    public void load(RSInputStream bb) throws IOException {
        int opcode;

        while ((opcode = bb.readByte() & 0xFF) != 0) {
            if (opcode == 1) {
                int length = bb.readUnsignedShort();
                this.durations = new int[length];
                for (int i = 0; length > i; i++) {
                    this.durations[i] = bb.readUnsignedShort();
                }
                this.components = new int[length];
                for (int i = 0; i < length; i++) {
                    this.components[i] = bb.readUnsignedShort();
                }
                for (int i = 0; (~length) < (~i); i++) {
                    this.components[i] = ((bb.readUnsignedShort() << 10) - -this.components[i]);
                }
            } else if ((~opcode) == -3) {
                this.anInt828 = bb.readUnsignedShort();
            } else if ((~opcode) != -4) {
                if ((~opcode) == -6) {
                    this.anInt829 = bb.readUnsignedByte();
                } else if ((~opcode) == -7) {
                    this.anInt820 = bb.readUnsignedShort();
                } else if (opcode != 7) {
                    if ((~opcode) != -9) {
                        if ((~opcode) == -10) {
                            this.anInt821 = bb.readUnsignedByte();
                        } else if ((~opcode) == -11) {
                            this.anInt816 = bb.readUnsignedByte();
                        } else if ((~opcode) != -12) {
                            if (opcode != 12) {
                                if ((~opcode) != -14) {
                                    if (opcode != 14) {
                                        if ((~opcode) == -16) {
                                            this.aBoolean825 = true;
                                        } else if ((~opcode) != -17) {
                                            if (opcode != 18) {
                                                if (opcode != 19) {
                                                    if (opcode == 20) {
                                                        if (((this.anIntArray810) == null) || ((this.anIntArray815) == null)) {
                                                            this.anIntArray810 = (new int[(this.anIntArrayArray822).length]);
                                                            this.anIntArray815 = (new int[(this.anIntArrayArray822).length]);
                                                            for (int i_49_ = 0; ((~i_49_) > (~(this.anIntArrayArray822).length)); i_49_++) {
                                                                this.anIntArray810[i_49_] = 256;
                                                                this.anIntArray815[i_49_] = 256;
                                                            }
                                                        }
                                                        final int i_50_ = (bb.readUnsignedByte());
                                                        this.anIntArray810[i_50_] = (bb.readUnsignedShort());
                                                        this.anIntArray815[i_50_] = (bb.readUnsignedShort());
                                                    }
                                                } else {
                                                    if ((this.anIntArray808) == null) {
                                                        this.anIntArray808 = (new int[(this.anIntArrayArray822).length]);
                                                        for (int i_51_ = 0; ((~(this.anIntArrayArray822).length) < (~i_51_)); i_51_++) {
                                                            this.anIntArray808[i_51_] = 255;
                                                        }
                                                    }
                                                    this.anIntArray808[(bb.readUnsignedByte())] = (bb.readUnsignedByte());
                                                }
                                            } else {
                                                this.aBoolean812 = true;
                                            }
                                        } else {
                                            this.aBoolean823 = true;
                                        }
                                    } else {
                                        this.aBoolean817 = true;
                                    }
                                } else {
                                    final int i_52_ = bb.readUnsignedShort();
                                    this.anIntArrayArray822 = new int[i_52_][];
                                    for (int i_53_ = 0; ((~i_53_) > (~i_52_)); i_53_++) {
                                        final int i_54_ = bb.readUnsignedByte();
                                        if (i_54_ > 0) {
                                            this.anIntArrayArray822[i_53_] = new int[i_54_];
                                            this.anIntArrayArray822[i_53_][0] = (bb.read24BitInt());
                                            for (int i_55_ = 1; ((~i_55_) > (~i_54_)); i_55_++) {
                                                this.anIntArrayArray822[i_53_][i_55_] = (bb.readUnsignedShort());
                                            }
                                        }
                                    }
                                }
                            } else {
                                final int i_56_ = bb.readUnsignedByte();
                                this.anIntArray827 = new int[i_56_];
                                for (int i_57_ = 0; i_57_ < i_56_; i_57_++) {
                                    this.anIntArray827[i_57_] = bb.readUnsignedShort();
                                }
                                for (int i_58_ = 0; i_58_ < i_56_; i_58_++) {
                                    this.anIntArray827[i_58_] = ((bb.readUnsignedShort() - -this.anIntArray827[i_58_]));
                                }
                            }
                        } else {
                            this.anInt819 = bb.readUnsignedByte();
                        }
                    } else {
                        this.anInt807 = bb.readUnsignedByte();
                    }
                } else {
                    this.anInt809 = bb.readUnsignedShort();
                }
            } else {
                this.aBooleanArray813 = new boolean[256];
                final int i_59_ = bb.readUnsignedByte();
                for (int i_60_ = 0; i_59_ > i_60_; i_60_++) {
                    this.aBooleanArray813[bb.readUnsignedByte()] = true;
                }
            }
        }
    }

    /**
     * Calculates the duration of this animation. This adds all of the frame
     * durations together, then divides by the number of frames which are sent
     * to the player per tick, rounding up. Frames are not restricted to ticks,
     * but this method is for convenience. There are 25 frames per tick
     * displayed to the client.
     *
     * @return the duration of the animation in ticks
     */
    public int getDuration(boolean ignoreLastFrame) {
        double length = 0;
        for (int duration : this.durations) {
            length += duration;
        }
        if (ignoreLastFrame && durations.length > 0) {
            length -= durations[durations.length - 1];
        }
        return (int) (Math.ceil(length / FRAMES_PER_TICK));
    }
}