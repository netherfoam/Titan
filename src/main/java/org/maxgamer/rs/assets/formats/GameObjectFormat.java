package org.maxgamer.rs.assets.formats;

import net.openrs.util.ByteBufferUtils;
import org.maxgamer.rs.structure.ReflectUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class GameObjectFormat extends Format {
    /**
     * Also called "transformIds" - These appear to be other objects that
     * represent this but in a different state. Eg, "Opened" and "Closed" doors
     * are different object, but the concept is that they're the same object in
     * a different state.
     */
    private int[] objectIds;
    private String[] options;
    private String name = "";
    private boolean clippingFlag;
    private boolean isSolid;
    private int sizeY;
    private int sizeX;
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

    public GameObjectFormat() {
        this.clippingFlag = false;
        this.options = new String[5];
        this.sizeY = 1;
        this.name = "null";
        this.sizeX = 1;
        this.isSolid = true;
        this.actionCount = 2;
    }

    public boolean hasOption(String option) {
        if (this.options == null) return false;

        for (String s : this.options) {
            if (s == null) continue;
            if (s.equals(option)) return true;
        }
        return false;
    }

    @Override
    public void decode(int opcode, ByteBuffer buffer, Stasis stasis) throws IOException {
        if (opcode == 1 || opcode == 5) {
            boolean creator_Boolean = false; //A boolean from the creator class in the client
            if (opcode == 5 && creator_Boolean) {
                int length = buffer.get() & 0xFF;
                for (int j = 0; j < length; j++) {
                    buffer.get();

                    int n = (buffer.get() & 0xFF) * 2;
                    buffer.position(buffer.position() + n);
                }
            }

            int length = buffer.get() & 0xFF;
            byte[] bytes = new byte[length];
            int[][] intsints = new int[length][];
            for (int i = 0; i < length; i++) {
                bytes[i] = buffer.get();
                int value = buffer.get() & 0xFF;
                intsints[i] = new int[value];
                for (int j = 0; j < value; j++)
                    intsints[i][j] = buffer.getShort();
            }

            if (opcode == 5 && !creator_Boolean) {
                length = buffer.get() & 0xFF;
                for (int j = 0; j < length; j++) {
                    buffer.get();

                    int n = (buffer.get() & 0xFF) * 2;
                    buffer.position(buffer.position() + n);
                }
            }
        } else if (opcode == 2) this.name = ByteBufferUtils.getJagexString(buffer);
        else if (opcode == 14) this.sizeX = buffer.get() & 0xFF;
        else if (opcode == 19) /* this.hasOptions = */ buffer.get(); // & 0xFF
        else if (opcode == 22) {
        } else if (opcode == 23) {
        } else if (opcode == 27) this.actionCount = 1;
        else if (opcode == 29) /* anInt2931 = */ buffer.get();
        else if (opcode == 40) {
            int length = (buffer.get() & 0xFF);
            short[] aShortArray3003 = new short[length];
            short[] aShortArray2965 = new short[length];
            for (int j = 0; j < length; j++) {
                aShortArray3003[j] = buffer.getShort();
                aShortArray2965[j] = buffer.getShort();
            }
        }
        if (opcode == 62) {
        }
        if (opcode == 65) /* anInt2938 = */ buffer.getShort();

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
                                                                                         */ buffer.getShort();
                                                        else if ((~opcode) != -70) {
                                                            if ((~opcode) == -71) /*
                                                                                             * anInt2973
                                                                                             * =
                                                                                             */ buffer.getShort() ; // & 0xFFFF//buffer.readUShort(false) << -836995390;
                                                            else if ((~opcode) == -72) /*
                                                                                                     * anInt2997
                                                                                                     * =
                                                                                                     */ buffer.getShort();  // & 0xFFFF //buffer.readUShort(false) << -1352000926;
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
                                                                                    buffer.getShort();
                                                                                    /*
                                                                                     * int
                                                                                     * anInt2981
                                                                                     * =
                                                                                     */
                                                                                    buffer.get(); // & 0xFF
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
                                                                                                buffer.getShort(); // & 0xFFFF
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
                                                                                                    buffer.get(); // & 0xFF
                                                                                                    /*
                                                                                                     * int
                                                                                                     * anInt2977
                                                                                                     * =
                                                                                                     */
                                                                                                    buffer.getShort();
                                                                                                } else if (opcode == 101) /*
                                                                                                                         * anInt2962
                                                                                                                         * =
                                                                                                                         */ buffer.get(); // & 0xFF
                                                                                                else if (opcode == 102) /*
                                                                                                                         * anInt2990
                                                                                                                         * =
                                                                                                                         */ buffer.getShort();
                                                                                                else if ((~opcode) != -104) {
                                                                                                    if ((~opcode) == -105) /*
                                                                                                                                         * this
                                                                                                                                         * .
                                                                                                                                         * anInt2987
                                                                                                                                         * =
                                                                                                                                         */ buffer.get(); // & 0xFF
                                                                                                    else if ((~opcode) != -106) {
                                                                                                        if (opcode == 106) {
                                                                                                            int i_64_ = buffer.get(); // & 0xFF
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
                                                                                                                anIntArray2979[i_65_] = buffer.getShort();
                                                                                                                int i_66_ = buffer.get() & 0xFF;
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
                                                                                                                    int i_67_ = buffer.get() & 0xFF;
                                                                                                                    int[] anIntArray2934 = new int[i_67_];
                                                                                                                    for (int i_68_ = 0; (~i_68_) > (~i_67_); i_68_++)
                                                                                                                        anIntArray2934[i_68_] = buffer.getShort();
                                                                                                                } else if (opcode == 162) {
                                                                                                                    /*
                                                                                                                     * int
                                                                                                                     * anInt2985
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.getInt();
                                                                                                                } else if (opcode == 163) {
                                                                                                                    /*
                                                                                                                     * byte
                                                                                                                     * aByte2930
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.get();
                                                                                                                    /*
                                                                                                                     * byte
                                                                                                                     * aByte2942
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.get();
                                                                                                                    /*
                                                                                                                     * byte
                                                                                                                     * aByte2967
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.get();
                                                                                                                    /*
                                                                                                                     * byte
                                                                                                                     * aByte2932
                                                                                                                     * =
                                                                                                                     */
                                                                                                                    buffer.get();
                                                                                                                } else if (opcode == 164) /*
                                                                                                                                         * anInt2940
                                                                                                                                         * =
                                                                                                                                         */ buffer.getShort(); // & 0xFFFF
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
                                                                                                                                        buffer.getShort();
                                                                                                                                        /*
                                                                                                                                         * int
                                                                                                                                         * anInt2950
                                                                                                                                         * =
                                                                                                                                         */
                                                                                                                                        buffer.getShort();
                                                                                                                                    } else if ((~opcode) == -178) {
                                                                                                                                    } else if ((~opcode) == -179) /*
                                                                                                                                                                             * this
                                                                                                                                                                             * .
                                                                                                                                                                             * anInt2970
                                                                                                                                                                             * =
                                                                                                                                                                             */ buffer.get(); // & 0xFF
                                                                                                                                    else if (opcode == 249) {
                                                                                                                                        int i_69_ = buffer.get(); // & 0xFF
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
                                                                                                                                            boolean bool = (buffer.get() & 0xFF) == 1;
                                                                                                                                            ByteBufferUtils.getTriByte(buffer);
                                                                                                                                            //RSStream stream;
                                                                                                                                            if (!bool) buffer.getInt(); //stream = new Class98_Sub34(buffer.readInt(-2));
                                                                                                                                            else ByteBufferUtils.getJagexString(buffer); //stream = new Class98_Sub15(buffer.readRS2String((byte) 84));
                                                                                                                                            //aClass377_2944.copyTo(stream, i_72_, -1);
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                } else /*
                                                                                                                                     * this
                                                                                                                                     * .
                                                                                                                                     * anInt2953
                                                                                                                                     * =
                                                                                                                                     */ByteBufferUtils.readSmart(buffer);

                                                                                                                            } else /*
                                                                                                                                 * this
                                                                                                                                 * .
                                                                                                                                 * anInt2986
                                                                                                                                 * =
                                                                                                                                 */ByteBufferUtils.readSmart(buffer);
                                                                                                                        } else /*
                                                                                                                             * this
                                                                                                                             * .
                                                                                                                             * anInt2945
                                                                                                                             * =
                                                                                                                             */buffer.getShort();
                                                                                                                    } else /*
                                                                                                                         * anInt2989
                                                                                                                         * =
                                                                                                                         */buffer.getShort(); // & 0xFFFF
                                                                                                                } else /*
                                                                                                                     * anInt2988
                                                                                                                     * =
                                                                                                                     */buffer.getShort(); // & 0xFFFF
                                                                                                            } else {
                                                                                                                this.options[opcode - 150] = ByteBufferUtils.getJagexString(buffer);
                                                                                                                //if (!this.creator.aBoolean2516) this.options[-150 + opcode] = null;
                                                                                                            }
                                                                                                        } else /*
                                                                                                             * this
                                                                                                             * .
                                                                                                             * anInt2958
                                                                                                             * =
                                                                                                             */buffer.getShort();
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
                                                                                                buffer.get(); // & 0xFF
                                                                                                /*
                                                                                                 * this
                                                                                                 * .
                                                                                                 * anInt3008
                                                                                                 * =
                                                                                                 */
                                                                                                buffer.getShort();
                                                                                            }
                                                                                        } else {
                                                                                            /*
                                                                                             * anInt2985
                                                                                             * =
                                                                                             */
                                                                                            buffer.getShort();
                                                                                        }
                                                                                    } else {
                                                                                        /*
                                                                                         * anInt2985
                                                                                         * =
                                                                                         * 256
                                                                                         * *
                                                                                         */
                                                                                        buffer.get(); // & 0xFF
                                                                                    }
                                                                                } else {
                                                                                    /*
                                                                                     * this
                                                                                     * .
                                                                                     * anInt2949
                                                                                     * =
                                                                                     */
                                                                                    buffer.getShort(); // & 0xFFFF
                                                                                    /*
                                                                                     * this
                                                                                     * .
                                                                                     * anInt2972
                                                                                     * =
                                                                                     */
                                                                                    buffer.getShort(); // & 0xFFFF
                                                                                    /*
                                                                                     * this
                                                                                     * .
                                                                                     * anInt2981
                                                                                     * =
                                                                                     */
                                                                                    buffer.get(); // & 0xFF
                                                                                    int i_73_ = buffer.get() & 0xFF;
                                                                                    int[] anIntArray2926 = new int[i_73_];
                                                                                    for (int i_74_ = 0; (~i_73_) < (~i_74_); i_74_++)
                                                                                        anIntArray2926[i_74_] = buffer.getShort();
                                                                                }
                                                                            } else {
                                                                                // Opcode 77 or 92
                                                                                anInt2983 = buffer.getShort();
                                                                                if ((~anInt2983) == -65536) {
                                                                                    anInt2983 = -1;
                                                                                }
                                                                                int anInt2968 = buffer.getShort();
                                                                                if (anInt2968 == 65535) {
                                                                                    anInt2968 = -1;
                                                                                }
                                                                                int i_75_ = -1;
                                                                                if (opcode == 92) {
                                                                                    i_75_ = buffer.getShort() & 0xFFFF;
                                                                                    if (i_75_ == 65535) {
                                                                                        i_75_ = -1;
                                                                                    }
                                                                                }

                                                                                int i_76_ = buffer.get() & 0xFF;
                                                                                this.objectIds = new int[i_76_ + 2];
                                                                                for (int i_77_ = 0; i_76_ >= i_77_; i_77_++) {
                                                                                    this.objectIds[i_77_] = buffer.getShort() & 0xFFFF;
                                                                                    if (this.objectIds[i_77_] == 65535) this.objectIds[i_77_] = -1;
                                                                                }
                                                                                this.objectIds[1 + i_76_] = i_75_;
                                                                            }
                                                                        } else /*
                                                                             * this
                                                                             * .
                                                                             * anInt2975
                                                                             * =
                                                                             */buffer.get(); // & 0xFF
                                                                    } else {
                                                                        this.clippingFlag = true;
                                                                        this.isSolid = true;
                                                                        this.actionCount = 0;
                                                                    }
                                                                } else {
                                                                }
                                                            } else /*
                                                                 * anInt2946
                                                                 * =
                                                                 */buffer.get(); // & 0xFF //buffer.readUShort(false) << -784917758;
                                                        } else /*
                                                             * this.anInt2948
                                                             * =
                                                             */buffer.get(); // & 0xFF
                                                    } else /* anInt2954 = */buffer.getShort();
                                                } else {
                                                }
                                            } else {
                                                int i_78_ = (buffer.get() & 0xFF);
                                                byte[] aByteArray2955 = new byte[i_78_];
                                                for (int i_79_ = 0; ((~i_78_) < (~i_79_)); i_79_++)
                                                    aByteArray2955[i_79_] = (buffer.get());
                                            }
                                        } else {
                                            int i_80_ = (buffer.get() & 0xFF);
                                            short[] aShortArray2974 = new short[i_80_];
                                            short[] aShortArray2995 = new short[i_80_];
                                            for (int i_81_ = 0; i_80_ > i_81_; i_81_++) {
                                                aShortArray2995[i_81_] = buffer.getShort();
                                                aShortArray2974[i_81_] = buffer.getShort();
                                            }
                                        }
                                    } else this.options[opcode + -30] = (ByteBufferUtils.getJagexString(buffer));
                                } else /* anInt2980 = */buffer.get() /* 5 */;
                            } else /* this.anInt2966 = */buffer.get(); // & 0xFF
                        } else {
                            /* this.anInt2941 = */
                            buffer.getShort();
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
        } else this.sizeY = buffer.get() & 0xFF;

        stasis.preserve();
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

    @Override
    public void encode(ByteArrayOutputStream out) throws IOException {

    }

    @Override
    public int hashCode() {
        return Objects.hash(name, clippingFlag, isSolid, actionCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final GameObjectFormat other = (GameObjectFormat) obj;
        return Objects.deepEquals(this.objectIds, other.objectIds)
                && Objects.deepEquals(this.options, other.options)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.clippingFlag, other.clippingFlag)
                && Objects.equals(this.isSolid, other.isSolid)
                && Objects.equals(this.sizeY, other.sizeY)
                && Objects.equals(this.sizeX, other.sizeX)
                && Objects.equals(this.anInt2983, other.anInt2983)
                && Objects.equals(this.anInt2968, other.anInt2968)
                && Objects.equals(this.actionCount, other.actionCount);
    }
}
