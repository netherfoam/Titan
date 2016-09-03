package org.maxgamer.rs.cache.format;

import org.maxgamer.rs.cache.RSInputStream;
import org.maxgamer.rs.util.io.ByteBufferInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;

/* Class197 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */

/**
 * @author netherfoam
 */
public final class RSFont {
    //	private int anInt1514;
    //	private int anInt1517;
    //	private int anInt1518;
    private byte[] charWidths;
    private byte[][] sizes;

    public RSFont(ByteBuffer bb) throws IOException {
        RSInputStream buffer = new RSInputStream(new ByteBufferInputStream(bb));
        try {
            int i = buffer.readUnsignedByte();
            if ((i ^ 0xffffffff) != -1) {
                throw new RuntimeException("");
            }
            boolean bool = buffer.readUnsignedByte() == 1;
            this.charWidths = new byte[256];
            buffer.read(this.charWidths, 0, 256);
            if (bool) {
                int[] is_28_ = new int[256];
                int[] is_29_ = new int[256];
                for (int i_30_ = 0; (i_30_ ^ 0xffffffff) > -257; i_30_++) {
                    is_28_[i_30_] = buffer.readUnsignedByte();
                }
                for (int i_31_ = 0; (i_31_ ^ 0xffffffff) > -257; i_31_++) {
                    is_29_[i_31_] = buffer.readUnsignedByte();
                }
                byte[][] is_32_ = new byte[256][];
                for (int i_33_ = 0; i_33_ < 256; i_33_++) {
                    is_32_[i_33_] = new byte[is_28_[i_33_]];
                    byte i_34_ = 0;
                    for (int i_35_ = 0; i_35_ < is_32_[i_33_].length; i_35_++) {
                        i_34_ += buffer.readByte();
                        is_32_[i_33_][i_35_] = i_34_;
                    }
                }
                byte[][] is_36_ = new byte[256][];
                for (int i_37_ = 0; (i_37_ ^ 0xffffffff) > -257; i_37_++) {
                    is_36_[i_37_] = new byte[is_28_[i_37_]];
                    byte i_38_ = 0;
                    for (int i_39_ = 0; i_39_ < is_36_[i_37_].length; i_39_++) {
                        i_38_ += buffer.readByte();
                        is_36_[i_37_][i_39_] = i_38_;
                    }
                }
                this.sizes = new byte[256][256];
                for (int i_40_ = 0; (i_40_ ^ 0xffffffff) > -257; i_40_++) {
                    if (i_40_ != 32 && i_40_ != 160) {
                        for (int i_41_ = 0; i_41_ < 256; i_41_++) {
                            if (i_41_ != 32 && i_41_ != 160) {
                                this.sizes[i_40_][i_41_] = (byte) (RSFont.method4003(i_41_, i_40_, is_32_, false, is_28_, is_36_, is_29_, this.charWidths));
                            }
                        }
                    }
                }
                //				this.anInt1518 = is_28_[32] + is_29_[32];
            } else {
                //				this.anInt1518 = buffer.readUnsignedByte();
                buffer.readUnsignedByte();
            }
            buffer.readUnsignedByte();
            buffer.readUnsignedByte();
            //			this.anInt1517 = buffer.readUnsignedByte();
            //			this.anInt1514 = buffer.readUnsignedByte();
            buffer.readUnsignedByte();
            buffer.readUnsignedByte();
        } finally {
            buffer.close();
        }
    }

    static final int method4003(int i, int i_1_, byte[][] is, boolean bool, int[] is_2_, byte[][] is_3_, int[] is_4_, byte[] is_5_) {
        if (bool != false) {
            return 25;
        }
        int i_6_ = is_4_[i_1_];
        int i_7_ = i_6_ + is_2_[i_1_];
        int i_8_ = is_4_[i];
        int i_9_ = i_8_ - -is_2_[i];
        int i_10_ = i_6_;
        if (i_6_ < i_8_) {
            i_10_ = i_8_;
        }
        int i_11_ = i_7_;
        if (i_9_ < i_7_) {
            i_11_ = i_9_;
        }
        int i_12_ = 0xff & is_5_[i_1_];
        if ((is_5_[i] & 0xff) < i_12_) {
            i_12_ = 0xff & is_5_[i];
        }
        byte[] is_13_ = is_3_[i_1_];
        byte[] is_14_ = is[i];
        int i_15_ = -i_6_ + i_10_;
        int i_16_ = -i_8_ + i_10_;
        for (int i_17_ = i_10_; (i_11_ ^ 0xffffffff) < (i_17_ ^ 0xffffffff); i_17_++) {
            int i_18_ = is_14_[i_16_++] + is_13_[i_15_++];
            if (i_18_ < i_12_) {
                i_12_ = i_18_;
            }
        }
        return -i_12_;
    }

    /**
     * Fetches the width of the given character
     *
     * @param theChar the character as an int. >= 0 and <= 255
     * @return the width of the character
     */
    public final int getCharWidth(char theChar) {
        return 0xFF & this.charWidths[theChar];
    }
}
