package org.maxgamer.rs.model.map.object;

import org.maxgamer.rs.cache.RSInputStream;
import org.maxgamer.rs.structure.ReflectUtil;
import org.maxgamer.rs.util.io.ByteBufferInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;

/* Class352 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */

public final class GameObjectProto {
    //private int[] anIntArray2926;

    /**
     * Also called "transformIds" - These appear to be other objects that
     * represent this but in a different state. Eg, "Opened" and "Closed" doors
     * are different object, but the concept is that they're the same object in
     * a different state.
     */
    private int[] objectIds;
    /*
     * private int anInt2929; private byte aByte2930; private int anInt2931 = 0;
     * private byte aByte2932; private int anInt2933;
     */
    //private int[] anIntArray2934;
    private int id;
    //private int[] anIntArray2937;
    //private int anInt2938;
    private String[] options;
    //private int anInt2940;
    //private int anInt2941;
    //private byte aByte2942;
    /*
     * private int anInt2945 = 0; private int anInt2946; private int anInt2948;
     * private int anInt2949; private int anInt2950;
     */
    //private int[][] anIntArrayArray2951;
    private String name;
    //private int anInt2953;
    //private int anInt2954;
    //private byte[] aByteArray2955;
    //private int anInt2958;
    private boolean clippingFlag;
    private boolean isSolid;
    //private int anInt2962;
    //private int anInt2964;
    //private short[] aShortArray2965;
    //private int anInt2966;
    //private byte aByte2967;
    //
    /*
     * private int anInt2970; private int anInt2972; private int anInt2973;
     */
    //private short[] aShortArray2974;
    //private int anInt2975;
    //boolean aBoolean2976;
    //private int anInt2977;
    private int sizeY;
    //private int[] anIntArray2979;
    /*
     * private int anInt2980; private int anInt2981; private int anInt2985;
     * private int anInt2986; private int anInt2987; private int anInt2988;
     * private int anInt2989; private int anInt2990;
     */
    private int sizeX;
    //private byte[] aByteArray2994;
    //private short[] aShortArray2995;
    //private int anInt2996;
    //private int anInt2997;
    /* private int hasOptions; */

    /* Something to do with object transforms */
    private int anInt2983;
    @SuppressWarnings("unused")
    private int anInt2968;

    /**
     * Acceptable values appear to be 0,1 or 2. They are involved in the
     * clipping.
     * <p>
     * 0: No clip applies to this object. If clippingFlag is set, actionCount =
     * 0.
     * <p>
     * 1: Decoration clip, 0x40000. Applied to tile at location. Clip applies to
     * this object.
     * <p>
     * 2: Clip applies to this object.
     */
    private int actionCount;

    //private int anInt3002;
    //private short[] aShortArray3003;
    //private boolean aBoolean3005;
    //private int anInt3006;
    //private int anInt3008;

    public GameObjectProto() {
        /*
         * anIntArray2937 = null; this.anInt2941 = -1; this.anInt2949 = 0;
         * anInt2929 = 128; aByte2932 = (byte) 0; anInt2954 = 128; anInt2946 =
         * 0;
         */
        this.clippingFlag = false;
        /*
         * this.anInt2953 = 0; this.anInt2970 = 0;
         */
        this.options = new String[5];
        /*
         * this.anInt2962 = 0; this.anInt2966 = 64; anInt2968 = -1;
         * this.anInt2933 = -1; anInt2973 = 0; anInt2940 = 0; this.anInt2977 =
         * -1;
         */
        this.sizeY = 1;
        /* anInt2983 = -1; */
        this.name = "null";
        /*
         * this.anInt2975 = -1; this.anInt2948 = 0; this.anInt2958 = -1;
         * this.aBoolean2976 = false; anInt2938 = 128; anInt2988 = 0;
         */
        this.sizeX = 1;
        /*
         * anInt2989 = 0; anInt2980 = 0; this.anIntArray2979 = null;
         * this.anInt2990 = -1; this.anInt2950 = 256; anInt2997 = 0;
         */
        this.isSolid = true;
        /*
         * this.anInt2996 = -1; this.anInt2986 = 960; anInt2964 = 0;
         * this.anInt2972 = 0; anInt2985 = -1; this.aBoolean3005 = false;
         * this.anInt2981 = 0; this.anInt2987 = 255; this.anInt3002 = -1;
         */
        this.actionCount = 2;
        /*
         * this.anInt3006 = 256; this.anInt3008 = -1;
         */
        /* this.hasOptions = -1; */
    }

    public static GameObjectProto decode(int id, ByteBuffer bb) throws IOException {
        if (bb == null) {
            throw new NullPointerException("ByteBuffer may not be null!");
        }
        GameObjectProto proto = new GameObjectProto();
        proto.name = "";
        proto.id = id;

        RSInputStream in = new RSInputStream(new ByteBufferInputStream(bb));
        proto.readObject(in);
        if (bb.remaining() > 0) {
            throw new RuntimeException("Remaining(): " + bb.remaining());
        }
        proto.setup();
        if (proto.clippingFlag) {
            proto.isSolid = true;
            proto.actionCount = 0;
        }

        return proto;
    }

    final void readObject(RSInputStream buffer) throws IOException {
        while (true) {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0) break;
            readObject(buffer, opcode);
        }
    }

    private void method3854(RSInputStream buffer) throws IOException {
        int length = buffer.readUnsignedByte();
        for (int j = 0; j < length; j++) {
            buffer.skip(1);
            buffer.skip(buffer.readUnsignedByte() * 2);
        }
    }

    public boolean hasOption(String option) {
        if (this.options == null) return false;

        for (String s : this.options) {
            if (s == null) continue;
            if (s.equals(option)) return true;
        }
        return false;
    }

    private void readObject(RSInputStream buffer, int opcode) throws IOException {
        if (opcode == 1 || opcode == 5) {
            boolean creator_Boolean = false; //A boolean from the creator class in the client
            if (opcode == 5 && creator_Boolean) method3854(buffer);

            int length = buffer.readUnsignedByte();
            byte[] bytes = new byte[length];
            int[][] intsints = new int[length][];
            for (int i = 0; i < length; i++) {
                bytes[i] = buffer.readByte();
                int value = buffer.readUnsignedByte();
                intsints[i] = new int[value];
                for (int j = 0; j < value; j++)
                    intsints[i][j] = buffer.readShort();
            }
            if (opcode == 5 && !creator_Boolean) method3854(buffer);
        } else if (opcode == 2) this.name = buffer.readPJStr1();
        else if (opcode == 14) this.sizeX = buffer.readUnsignedByte();
        else if (opcode == 19) /* this.hasOptions = */ buffer.readUnsignedByte();
        else if (opcode == 22) {
        } else if (opcode == 23) {
        } else if (opcode == 27) this.actionCount = 1;
        else if (opcode == 29) /* anInt2931 = */ buffer.readByte();
        else if (opcode == 40) {
            int length = (buffer.readUnsignedByte());
            short[] aShortArray3003 = new short[length];
            short[] aShortArray2965 = new short[length];
            for (int j = 0; j < length; j++) {
                aShortArray3003[j] = buffer.readShort();
                aShortArray2965[j] = buffer.readShort();
            }
        }
        if (opcode == 62) {
        }
        if (opcode == 65) /* anInt2938 = */ buffer.readShort();

        else if (opcode != 15) {
            if ((~opcode) != -18) {
                if (opcode != 18) {
                    if (opcode != 21) {


                        if ((~opcode) != -25) {

                            if (opcode != 28) {

                                if ((~opcode) != -40) {
                                    if ((~opcode) > -31 || opcode >= 35) {

                                        if ((~opcode) != -42) {
                                            if ((~opcode) != -43) {

                                                if (opcode != 64) {

                                                    if ((~opcode) != -67) {
                                                        if ((~opcode) == -68) /*
                                                                                         * anInt2929
                                                                                         * =
                                                                                         */ buffer.readShort();
                                                        else if ((~opcode) != -70) {
                                                            if ((~opcode) == -71) /*
                                                                                             * anInt2973
                                                                                             * =
                                                                                             */ buffer.readUnsignedShort(); //buffer.readUShort(false) << -836995390;
                                                            else if ((~opcode) == -72) /*
                                                                                                     * anInt2997
                                                                                                     * =
                                                                                                     */ buffer.readUnsignedShort(); //buffer.readUShort(false) << -1352000926;
                                                            else if ((~opcode) != -73) {
                                                                if (opcode != 73) {
                                                                    if (opcode != 74) {
                                                                        if (opcode != 75) {
                                                                            if ((~opcode) != -78 && (~opcode) != -93) {
                                                                                if (opcode == 78) {
                                                                                    /*
                                                                                     * int
                                                                                     * anInt2996
                                                                                     * =
                                                                                     */
                                                                                    buffer.readShort();
                                                                                    /*
                                                                                     * int
                                                                                     * anInt2981
                                                                                     * =
                                                                                     */
                                                                                    buffer.readUnsignedByte();
                                                                                } else if (opcode != 79) {
                                                                                    if ((~opcode) != -82) {
                                                                                        if (opcode == 82) {
                                                                                        } else if (opcode == 88) {
                                                                                        } else if ((~opcode) == -90) {
                                                                                        } else if (opcode == 91) {
                                                                                        } else if (opcode != 93) {
                                                                                            if (opcode == 94) {
                                                                                            } else if ((~opcode) == -96) {
                                                                                                /*
                                                                                                 * int
                                                                                                 * anInt2985
                                                                                                 * =
                                                                                                 */
                                                                                                buffer.readUnsignedShort();
                                                                                            } else if ((~opcode) == -98) {
                                                                                            } else if (opcode == 98) {
                                                                                            } //this.aBoolean3005 = true;
                                                                                            else if ((~opcode) != -100) {
                                                                                                if (opcode == 100) {
                                                                                                    /*
                                                                                                     * int
                                                                                                     * anInt2933
                                                                                                     * =
                                                                                                     */
                                                                                                    buffer.readUnsignedByte();
                                                                                                    /*
                                                                                                     * int
                                                                                                     * anInt2977
                                                                                                     * =
                                                                                                     */
                                                                                                    buffer.readShort();
                                                                                                } else if (opcode == 101) /*
                                                                                                                         * anInt2962
                                                                                                                         * =
                                                                                                                         */ buffer.readUnsignedByte();
                                                                                                else if (opcode == 102) /*
                                                                                                                         * anInt2990
                                                                                                                         * =
                                                                                                                         */ buffer.readShort();
                                                                                                else if ((~opcode) != -104) {
                                                                                                    if ((~opcode) == -105) /*
                                                                                                                                         * this
                                                                                                                                         * .
                                                                                                                                         * anInt2987
                                                                                                                                         * =
                                                                                                                                         */ buffer.readUnsignedByte();
                                                                                                    else if ((~opcode) != -106) {
                                                                                                        if (opcode == 106) {
                                                                                                            int i_64_ = buffer.readUnsignedByte();
                                                                                                            int[] anIntArray2937 = new int[i_64_];
                                                                                                            int[] anIntArray2979 = new int[i_64_];
                                                                                                            /*
                                                                                                             * int
                                                                                                             * anInt2964
                                                                                                             * =
                                                                                                             * 0
                                                                                                             * ;
                                                                                                             */
                                                                                                            for (int i_65_ = 0; (~i_65_) > (~i_64_); i_65_++) {
                                                                                                                anIntArray2979[i_65_] = buffer.readShort();
                                                                                                                int i_66_ = buffer.readUnsignedByte();
                                                                                                                anIntArray2937[i_65_] = i_66_;
                                                                                                                /*
                                                                                                                 * anInt2964
                                                                                                                 * +=
                                                                                                                 * i_66_
                                                                                                                 * ;
                                                                                                                 */
                                                                                                            }
                                                                                                        } else if ((~opcode) != -108) {
                                                                                                            if (opcode < 150 || (~opcode) <= -156) {
                                                                                                                if (opcode == 160) {
                                                                                                                    int i_67_ = buffer.readUnsignedByte();
                                                                                                                    int[] anIntArray2934 = new int[i_67_];
                                                                                                                    for (int i_68_ = 0; (~i_68_) > (~i_67_); i_68_++)
                                                                                                                        anIntArray2934[i_68_] = buffer.readShort();
                                                                                                                } else if (opcode == 162) {
                                                                                                                    /*
                                                                                                                     * int
                                                                                                                     * anInt2985
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.readInt();
                                                                                                                } else if (opcode == 163) {
                                                                                                                    /*
                                                                                                                     * byte
                                                                                                                     * aByte2930
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.readByte();
                                                                                                                    /*
                                                                                                                     * byte
                                                                                                                     * aByte2942
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.readByte();
                                                                                                                    /*
                                                                                                                     * byte
                                                                                                                     * aByte2967
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.readByte();
                                                                                                                    /*
                                                                                                                     * byte
                                                                                                                     * aByte2932
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.readByte();
                                                                                                                } else if (opcode == 164) /*
                                                                                                                                         * anInt2940
                                                                                                                                         * =
                                                                                                                                         */ buffer.readUnsignedShort();
                                                                                                                else if ((~opcode) != -166) {
                                                                                                                    if ((~opcode) != -167) {
                                                                                                                        if ((~opcode) != -168) {
                                                                                                                            if ((~opcode) == -169) {
                                                                                                                            } else if (opcode == 169) {
                                                                                                                            } else if ((~opcode) != -171) {
                                                                                                                                if ((~opcode) != -172) {
                                                                                                                                    if (opcode == 173) {
                                                                                                                                        /*
                                                                                                                                         * int
                                                                                                                                         * anInt3006
                                                                                                                                         * =
                                                                                                                                         */
                                                                                                                                        buffer.readShort();
                                                                                                                                        /*
                                                                                                                                         * int
                                                                                                                                         * anInt2950
                                                                                                                                         * =
                                                                                                                                         */
                                                                                                                                        buffer.readShort();
                                                                                                                                    } else if ((~opcode) == -178) {
                                                                                                                                    } else if ((~opcode) == -179) /*
                                                                                                                                                                             * this
                                                                                                                                                                             * .
                                                                                                                                                                             * anInt2970
                                                                                                                                                                             * =
                                                                                                                                                                             */ buffer.readUnsignedByte();
                                                                                                                                    else if (opcode == 249) {
                                                                                                                                        int i_69_ = buffer.readUnsignedByte();
                                                                                                                                        /*
                                                                                                                                         * if
                                                                                                                                         * (
                                                                                                                                         * aClass377_2944
                                                                                                                                         * ==
                                                                                                                                         * null
                                                                                                                                         * )
                                                                                                                                         * {
                                                                                                                                         * int
                                                                                                                                         * i_70_
                                                                                                                                         * =
                                                                                                                                         * Class48
                                                                                                                                         * .
                                                                                                                                         * method453
                                                                                                                                         * (
                                                                                                                                         * 423660257
                                                                                                                                         * ,
                                                                                                                                         * i_69_
                                                                                                                                         * )
                                                                                                                                         * ;
                                                                                                                                         * aClass377_2944
                                                                                                                                         * =
                                                                                                                                         * new
                                                                                                                                         * Class377
                                                                                                                                         * (
                                                                                                                                         * i_70_
                                                                                                                                         * )
                                                                                                                                         * ;
                                                                                                                                         * }
                                                                                                                                         */
                                                                                                                                        for (int i_71_ = 0; i_71_ < i_69_; i_71_++) {
                                                                                                                                            boolean bool = buffer.readUnsignedByte() == 1;
                                                                                                                                            buffer.read24BitInt();
                                                                                                                                            //RSStream stream;
                                                                                                                                            if (!bool) buffer.readInt(); //stream = new Class98_Sub34(buffer.readInt(-2));
                                                                                                                                            else buffer.readPJStr1(); //stream = new Class98_Sub15(buffer.readRS2String((byte) 84));
                                                                                                                                            //aClass377_2944.copyTo(stream, i_72_, -1);
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                } else /*
                                                                                                                                     * this
                                                                                                                                     * .
                                                                                                                                     * anInt2953
                                                                                                                                     * =
                                                                                                                                     */buffer.readSmart();
                                                                                                                            } else /*
                                                                                                                                 * this
                                                                                                                                 * .
                                                                                                                                 * anInt2986
                                                                                                                                 * =
                                                                                                                                 */buffer.readSmart();
                                                                                                                        } else /*
                                                                                                                             * this
                                                                                                                             * .
                                                                                                                             * anInt2945
                                                                                                                             * =
                                                                                                                             */buffer.readShort();
                                                                                                                    } else /*
                                                                                                                         * anInt2989
                                                                                                                         * =
                                                                                                                         */buffer.readUnsignedShort();
                                                                                                                } else /*
                                                                                                                     * anInt2988
                                                                                                                     * =
                                                                                                                     */buffer.readUnsignedShort();
                                                                                                            } else {
                                                                                                                this.options[opcode - 150] = buffer.readPJStr1();
                                                                                                                //if (!this.creator.aBoolean2516) this.options[-150 + opcode] = null;
                                                                                                            }
                                                                                                        } else /*
                                                                                                             * this
                                                                                                             * .
                                                                                                             * anInt2958
                                                                                                             * =
                                                                                                             */buffer.readShort();
                                                                                                    } else {
                                                                                                    } //this.aBoolean2976 = true;
                                                                                                } else {
                                                                                                }
                                                                                            } else {
                                                                                                /*
                                                                                                 * this
                                                                                                 * .
                                                                                                 * anInt3002
                                                                                                 * =
                                                                                                 */
                                                                                                buffer.readUnsignedByte();
                                                                                                /*
                                                                                                 * this
                                                                                                 * .
                                                                                                 * anInt3008
                                                                                                 * =
                                                                                                 */
                                                                                                buffer.readShort();
                                                                                            }
                                                                                        } else {
                                                                                            /*
                                                                                             * anInt2985
                                                                                             * =
                                                                                             */
                                                                                            buffer.readShort();
                                                                                        }
                                                                                    } else {
                                                                                        /*
                                                                                         * anInt2985
                                                                                         * =
                                                                                         * 256
                                                                                         * *
                                                                                         */
                                                                                        buffer.readUnsignedByte();
                                                                                    }
                                                                                } else {
                                                                                    /*
                                                                                     * this
                                                                                     * .
                                                                                     * anInt2949
                                                                                     * =
                                                                                     */
                                                                                    buffer.readShort();
                                                                                    /*
                                                                                     * this
                                                                                     * .
                                                                                     * anInt2972
                                                                                     * =
                                                                                     */
                                                                                    buffer.readShort();
                                                                                    /*
                                                                                     * this
                                                                                     * .
                                                                                     * anInt2981
                                                                                     * =
                                                                                     */
                                                                                    buffer.readUnsignedByte();
                                                                                    int i_73_ = buffer.readUnsignedByte();
                                                                                    int[] anIntArray2926 = new int[i_73_];
                                                                                    for (int i_74_ = 0; (~i_73_) < (~i_74_); i_74_++)
                                                                                        anIntArray2926[i_74_] = buffer.readShort();
                                                                                }
                                                                            } else {
                                                                                // Opcode 77 or 92
                                                                                anInt2983 = buffer.readShort();
                                                                                if ((~anInt2983) == -65536) {
                                                                                    anInt2983 = -1;
                                                                                }
                                                                                int anInt2968 = buffer.readShort();
                                                                                if (anInt2968 == 65535) {
                                                                                    anInt2968 = -1;
                                                                                }
                                                                                int i_75_ = -1;
                                                                                if (opcode == 92) {
                                                                                    i_75_ = buffer.readShort();
                                                                                    if (i_75_ == 65535) {
                                                                                        i_75_ = -1;
                                                                                    }
                                                                                }

                                                                                int i_76_ = buffer.readUnsignedByte();
                                                                                this.objectIds = new int[i_76_ + 2];
                                                                                for (int i_77_ = 0; i_76_ >= i_77_; i_77_++) {
                                                                                    this.objectIds[i_77_] = buffer.readShort() & 0xFFFF;
                                                                                    if (this.objectIds[i_77_] == 65535) this.objectIds[i_77_] = -1;
                                                                                }
                                                                                this.objectIds[1 + i_76_] = i_75_;
                                                                            }
                                                                        } else /*
                                                                             * this
                                                                             * .
                                                                             * anInt2975
                                                                             * =
                                                                             */buffer.readUnsignedByte();
                                                                    } else this.clippingFlag = true;
                                                                } else {
                                                                }
                                                            } else /*
                                                                 * anInt2946
                                                                 * =
                                                                 */buffer.readUnsignedShort(); //buffer.readUShort(false) << -784917758;
                                                        } else /*
                                                             * this.anInt2948
                                                             * =
                                                             */buffer.readUnsignedByte();
                                                    } else /* anInt2954 = */buffer.readShort();
                                                } else {
                                                }
                                            } else {
                                                int i_78_ = (buffer.readUnsignedByte());
                                                byte[] aByteArray2955 = new byte[i_78_];
                                                for (int i_79_ = 0; ((~i_78_) < (~i_79_)); i_79_++)
                                                    aByteArray2955[i_79_] = (buffer.readByte());
                                            }
                                        } else {
                                            int i_80_ = (buffer.readUnsignedByte());
                                            short[] aShortArray2974 = new short[i_80_];
                                            short[] aShortArray2995 = new short[i_80_];
                                            for (int i_81_ = 0; i_80_ > i_81_; i_81_++) {
                                                aShortArray2995[i_81_] = buffer.readShort();
                                                aShortArray2974[i_81_] = buffer.readShort();
                                            }
                                        }
                                    } else this.options[opcode + -30] = (buffer.readPJStr1());
                                } else /* anInt2980 = */buffer.readByte() /* 5 */;
                            } else /* this.anInt2966 = */buffer.readUnsignedByte() /*
                                                                                 * <<
                                                                                 * -
                                                                                 * 69774750
                                                                                 */;
                        } else {
                            /* this.anInt2941 = */
                            buffer.readShort();
                            /*
                             * if ((this.anInt2941 ^ 0xffffffff) == -65536)
                             * this.anInt2941 = -1;
                             */
                        }
                    } else {
                    }
                } else this.isSolid = false; //Opcode 18
            } else {
                //Opcode 17
                this.isSolid = false;
                this.actionCount = 0;
            }
        } else this.sizeY = buffer.readUnsignedByte();
        //method3857(33);
    }

    final void setup() {
        /*
         * if ((this.hasOptions ^ 0xffffffff) == 0) { this.hasOptions = 0; if
         * (this.aByteArray2994 != null && (this.aByteArray2994.length ^
         * 0xffffffff) == -2 && ((this.aByteArray2994[0] ^ 0xffffffff) == -11))
         * this.hasOptions = 1; for (int j = 0; j < 5; j++) { if
         * (this.options[j] != null) { this.hasOptions = 1; break; } } } if
         * ((this.anInt2941 ^ 0xffffffff) != 0 || this.aBoolean3005 ||
         * this.objectIds != null) { } if (this.anInt2975 != -1) return;
         * this.anInt2975 = this.actionCount == 0 ? 0 : 1;
         */
    }

    public String getOption(int num) {
        return options[num];
    }

    public int getActionCount() {
        return actionCount;
    }

    public String getExamine() {
        return "No Examine"; //TODO: Send actual examine
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public boolean hasRangeBlockClipFlag() {
        return clippingFlag;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public String[] getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return ReflectUtil.describe(this);
    }

    public int[] getAliases() {
        return this.objectIds;
    }

    public void setOption(int index, String option) {
        this.options[index] = option;
    }
}
