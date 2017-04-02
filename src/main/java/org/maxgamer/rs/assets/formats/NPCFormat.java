package org.maxgamer.rs.assets.formats;

import org.maxgamer.rs.util.BufferUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author netherfoam
 */
public class NPCFormat extends Format {
    private String name = "null";
    private String[] options = new String[5];
    private int combatLevel = -1;

    /**
     * TODO: This default value doesn't appear right, because NPC's in the cache set it to '1'.
     * TODO: implying that '1' is not the default value. I don't think this is size, personally.
     */
    private int size = 1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOption(int index, String option) {
        this.options[index] = option;
    }

    public String getOption(int index) {
        return options[index];
    }

    public int getLevel() {
        return combatLevel;
    }

    public void setLevel(int combatLevel) {
        this.combatLevel = combatLevel;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, options, combatLevel, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final NPCFormat other = (NPCFormat) obj;
        return Objects.equals(this.name, other.name)
                && Objects.deepEquals(this.options, other.options)
                && Objects.equals(this.combatLevel, other.combatLevel)
                && Objects.equals(this.size, other.size);
    }

    @Override
    public void encode(ByteArrayOutputStream out) throws IOException {
        // Name
        if(!"null".equals(name)) {
            out.write((byte) (2));
            out.write(name.getBytes());
            out.write((byte) 0);
        }

        // Options
        for(int i = 0; i < 5; i++) {
            String option = options[i];

            if(option == null) continue;

            // NB: These can be signalled by both opcodes 150-154 and 30-34 inclusive
            out.write((byte) (i + 150));
            out.write(option.getBytes());
            out.write((byte) 0);
        }

        if(combatLevel != -1) {
            out.write(95);
            out.write(combatLevel >> 8);
            out.write(combatLevel);
        }

        /*if(size != 1) {
            out.write(12);
            out.write(size);
        }*/
    }

    @Override
    public void decode(int opcode, ByteBuffer bb, Stasis stasis) throws IOException {
        switch (opcode) {
            case 163:
                bb.get();
                break;
            case 164:
                bb.getShort();
                bb.getShort();
                break;
            case 165:
                bb.get();
                break;
            case 168:
                bb.get();
                break;
        }
        if (opcode != 1) {
            if (opcode == 2) {
                this.name = BufferUtils.readRS2String(bb);
                return;
            } else if (opcode != 12) {
                if (opcode < 30 || opcode >= 35) {
                    if (opcode == 40) {
                        int i_1_ = (bb.get() & 0xFF);
                        short[] aShortArray3166 = new short[i_1_];
                        short[] aShortArray3201 = new short[i_1_];
                        for (int i_2_ = 0; i_2_ < i_1_; i_2_++) {
                            aShortArray3201[i_2_] = (short) (bb.getShort() & 0xFFFF);
                            aShortArray3166[i_2_] = (short) (bb.getShort() & 0xFFFF);
                        }
                    } else if (opcode != 41) {
                        if (opcode != 42) {
                            if (opcode != 60) {
                                if (opcode == 93) {
                                        /* d.isVisibleOnMap = false; */
                                } else if (opcode == 95) {
                                    this.combatLevel = (bb.getShort() & 0xFFFF);
                                    return;
                                } else if (opcode != 97) {
                                    if (opcode != 98) {
                                        if (opcode == 99) {
                                                /* d.aBoolean3210 = true; */
                                        } else if (opcode == 100) {
                                            bb.get();
                                        } else if (opcode == 101) {
                                            bb.get();
                                        } else if (opcode == 102) {
                                                /* d.headIcons = ( */
                                            bb.getShort()/*
                                                                                 * &
                                                                                 * 0xFFFF
                                                                                 * )
                                                                                 */;
                                        } else if (opcode == 103) {
                                                /* d.anInt3235 = ( */
                                            bb.getShort()/*
                                                                                 * &
                                                                                 * 0xFFFF
                                                                                 * )
                                                                                 */;
                                        } else if (opcode != 106 && opcode != 118) {
                                            if (opcode != 107) {
                                                if (opcode == 109) {
                                                        /*
                                                         * d.aBoolean3169 =
                                                         * false;
                                                         */
                                                } else if (opcode != 111) {
                                                    if (opcode == 113) {
                                                            /*
                                                             * d.aShort3213 =
                                                             * (short) (
                                                             */
                                                        bb.getShort()/*
                                                                             * &
                                                                             * 0xFFFF
                                                                             * )
                                                                             */;
                                                            /*
                                                             * d.aShort3237 =
                                                             * (short) (
                                                             */
                                                        bb.getShort()/*
                                                                             * &
                                                                             * 0xFFFF
                                                                             * )
                                                                             */;
                                                    } else if (opcode == 114) {
                                                            /* d.aByte3215 = */
                                                        bb.get();
                                                            /* d.aByte3193 = */
                                                        bb.get();
                                                    } else if (opcode != 119) {
                                                        if (opcode != 121) {
                                                            if (opcode != 122) {
                                                                if (opcode == 123) {
                                                                        /*
                                                                         * d.
                                                                         * anInt3203
                                                                         * = (
                                                                         */
                                                                    bb.getShort()/*
                                                                                         * &
                                                                                         * 0xFFFF
                                                                                         * )
                                                                                         */;
                                                                } else if (opcode != 125) {
                                                                    if (opcode == 127) {
                                                                            /*
                                                                             * d.
                                                                             * renderEmote
                                                                             * =
                                                                             * (
                                                                             */
                                                                        bb.getShort()/*
                                                                                             * &
                                                                                             * 0xFFFF
                                                                                             * )
                                                                                             */;
                                                                    } else if (opcode != 128) {
                                                                        if (opcode == 134) {
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3173
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                            bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                                /*
                                                                                 * if
                                                                                 * (
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3173
                                                                                 * ==
                                                                                 * 65535
                                                                                 * )
                                                                                 * {
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3173
                                                                                 * =
                                                                                 * -
                                                                                 * 1
                                                                                 * ;
                                                                                 * }
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3212
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                            bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                                /*
                                                                                 * if
                                                                                 * (
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3212
                                                                                 * ==
                                                                                 * 65535
                                                                                 * )
                                                                                 * {
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3212
                                                                                 * =
                                                                                 * -
                                                                                 * 1
                                                                                 * ;
                                                                                 * }
                                                                                 */
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3226
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                            bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                                /*
                                                                                 * if
                                                                                 * (
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3226
                                                                                 * ==
                                                                                 * 65535
                                                                                 * )
                                                                                 * {
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3226
                                                                                 * =
                                                                                 * -
                                                                                 * 1
                                                                                 * ;
                                                                                 * }
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3179
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                            bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                                /*
                                                                                 * if
                                                                                 * (
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3179
                                                                                 * ==
                                                                                 * 65535
                                                                                 * )
                                                                                 * {
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3179
                                                                                 * =
                                                                                 * -
                                                                                 * 1
                                                                                 * ;
                                                                                 * }
                                                                                 */
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3184
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                            bb.get()/*
                                                                                             * &
                                                                                             * 0xFF
                                                                                             * )
                                                                                             */;
                                                                        } else if (opcode == 135) {
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3214
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                            bb.get()/*
                                                                                             * &
                                                                                             * 0xFF
                                                                                             * )
                                                                                             */;
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3178
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                            bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                        } else if (opcode != 136) {
                                                                            if (opcode == 137) {
                                                                                    /*
                                                                                     * d
                                                                                     * .
                                                                                     * anInt3223
                                                                                     * =
                                                                                     * (
                                                                                     */
                                                                                bb.getShort()/*
                                                                                                     * &
                                                                                                     * 0xFFFF
                                                                                                     * )
                                                                                                     */;
                                                                            } else if (opcode == 138) {
                                                                                    /*
                                                                                     * d
                                                                                     * .
                                                                                     * anInt3167
                                                                                     * =
                                                                                     */
                                                                                bb.getShort()/*
                                                                                                     * &
                                                                                                     * 0xFFFF
                                                                                                     */;
                                                                            } else if (opcode != 139) {
                                                                                if (opcode == 140) {
                                                                                        /*
                                                                                         * d
                                                                                         * .
                                                                                         * anInt3216
                                                                                         * =
                                                                                         */
                                                                                    bb.get()/*
                                                                                                     * &
                                                                                                     * 0xFF
                                                                                                     */;
                                                                                } else if (opcode == 141) {
                                                                                        /*
                                                                                         * d
                                                                                         * .
                                                                                         * aBoolean3187
                                                                                         * =
                                                                                         * true
                                                                                         * ;
                                                                                         */
                                                                                } else if (opcode == 142) {
                                                                                        /*
                                                                                         * d
                                                                                         * .
                                                                                         * anInt3200
                                                                                         * =
                                                                                         */
                                                                                    bb.getShort()/*
                                                                                                         * &
                                                                                                         * 0xFFFF
                                                                                                         */;
                                                                                } else if (opcode != 143) {
                                                                                    if (opcode < 150 || opcode >= 155) {
                                                                                        if (opcode == 155) {
                                                                                            bb.get();
                                                                                            bb.get();
                                                                                            bb.get();
                                                                                            bb.get();
                                                                                        } else if (opcode != 158) {
                                                                                            if (opcode == 159) {
                                                                                                    /*
                                                                                                     * d
                                                                                                     * .
                                                                                                     * aByte3233
                                                                                                     * =
                                                                                                     * (
                                                                                                     * byte
                                                                                                     * )
                                                                                                     * 0
                                                                                                     * ;
                                                                                                     */
                                                                                            } else if (opcode != 160) {
                                                                                                if (opcode != 161) {
                                                                                                    if (opcode == 249) {
                                                                                                        int i_3_ = (bb.get() & 0xFF);
                                                                                                        for (int i_5_ = 0; i_3_ > i_5_; i_5_++) {
                                                                                                            boolean bool = (bb.get() & 0xFF) == 1;
                                                                                                            //bb.read24BitInt();
                                                                                                            BufferUtils.getTriByte(bb);
                                                                                                            //System.out.println("ScriptID " + i_6_);
                                                                                                            if (!bool) {
                                                                                                                bb.getInt();
                                                                                                            } else {
                                                                                                                BufferUtils.readRS2String(bb);
                                                                                                            }
                                                                                                            //System.out.println("Script NPC STRING: " + bb.readRS2String());
                                                                                                        }
                                                                                                    }
                                                                                                } else {
                                                                                                        /*
                                                                                                         * d
                                                                                                         * .
                                                                                                         * aBoolean3190
                                                                                                         * =
                                                                                                         * true
                                                                                                         * ;
                                                                                                         */
                                                                                                }
                                                                                            } else {
                                                                                                int i_7_ = (bb.get() & 0xFF);
                                                                                                int[] anIntArray3219 = new int[i_7_];
                                                                                                for (int i_8_ = 0; i_7_ > i_8_; i_8_++) {
                                                                                                    anIntArray3219[i_8_] = (bb.getShort() & 0xFFFF);
                                                                                                }
                                                                                            }
                                                                                        } else {
                                                                                                /*
                                                                                                 * d
                                                                                                 * .
                                                                                                 * aByte3233
                                                                                                 * =
                                                                                                 * (
                                                                                                 * byte
                                                                                                 * )
                                                                                                 * 1
                                                                                                 * ;
                                                                                                 */
                                                                                        }
                                                                                    } else {
                                                                                        this.options[opcode - 150] = BufferUtils.readRS2String(bb); //bb.readRS2String();
                                                                                        return;
                                                                                            /*
                                                                                             * if
                                                                                             * (
                                                                                             * !
                                                                                             * (
                                                                                             * (
                                                                                             * Class183
                                                                                             * )
                                                                                             * aClass183_3195
                                                                                             * )
                                                                                             * .
                                                                                             * aBoolean2484
                                                                                             * )
                                                                                             * options
                                                                                             * [
                                                                                             * opcode
                                                                                             * +
                                                                                             * -
                                                                                             * 150
                                                                                             * ]
                                                                                             * =
                                                                                             * null
                                                                                             * ;
                                                                                             */
                                                                                    }
                                                                                } else {
                                                                                        /*
                                                                                         * d
                                                                                         * .
                                                                                         * aBoolean3196
                                                                                         * =
                                                                                         * true
                                                                                         * ;
                                                                                         */
                                                                                }
                                                                            } else {
                                                                                    /*
                                                                                     * d
                                                                                     * .
                                                                                     * anInt3164
                                                                                     * =
                                                                                     * (
                                                                                     */
                                                                                bb.getShort()/*
                                                                                                     * &
                                                                                                     * 0xFFFF
                                                                                                     * )
                                                                                                     */;
                                                                            }
                                                                        } else {
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3181
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                            bb.get()/*
                                                                                             * &
                                                                                             * 0xFF
                                                                                             * )
                                                                                             */;
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3227
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                            bb.getShort() /*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                        }
                                                                    } else {
                                                                        bb.get();
                                                                    }
                                                                } else {
                                                                        /*
                                                                         * d.
                                                                         * direction
                                                                         * =
                                                                         */
                                                                    bb.get();
                                                                }
                                                            } else {
                                                                    /*
                                                                     * d.anInt3182
                                                                     * = (
                                                                     */
                                                                bb.getShort() /*
                                                                                     * &
                                                                                     * 0xFFFF
                                                                                     * )
                                                                                     */;
                                                            }
                                                        } else {
                                                            int i_9_ = (bb.get() & 0xFF);
                                                            for (int i_10_ = 0; (i_9_ > i_10_); i_10_++) {
                                                                //(bb.get() & 0xFF);
                                                                bb.get();
                                                                int[] is = new int[3];
                                                                is[0] = bb.get();
                                                                is[1] = bb.get();
                                                                is[2] = bb.get();
                                                            }
                                                        }
                                                    } else {
                                                            /* d.aByte3207 = */
                                                        bb.get();
                                                    }
                                                } else {
                                                        /*
                                                         * d.aBoolean3172 =
                                                         * false;
                                                         */
                                                }
                                            } else {
                                                    /* d.isClickable = false; */
                                            }
                                        } else {
                                                /* d.configFileId = ( */
                                            bb.getShort()/*
                                                                                     * &
                                                                                     * 0xFFFF
                                                                                     * )
                                                                                     */;
                                                /*
                                                 * if (d.configFileId == 65535)
                                                 * { d.configFileId = -1; }
                                                 * d.configId = (
                                                 */
                                            bb.getShort()/* & 0xFFFF) */;
                                                /*
                                                 * if (d.configId == 65535) {
                                                 * d.configId = -1; }
                                                 */
                                            int i_12_ = -1;
                                            if (opcode == 118) {
                                                i_12_ = (bb.getShort() & 0xFFFF);
                                                if (i_12_ == 65535) {
                                                    i_12_ = -1;
                                                }
                                            }
                                            int i_13_ = (bb.get() & 0xFF);
                                            int[] childrenIds = new int[2 + i_13_];
                                            for (int i_14_ = 0; i_14_ <= i_13_; i_14_++) {
                                                childrenIds[i_14_] = (bb.getShort() & 0xFFFF);
                                                if ((childrenIds[i_14_]) == 65535) {
                                                    childrenIds[i_14_] = -1;
                                                }
                                            }
                                            childrenIds[1 + i_13_] = i_12_;
                                        }
                                    } else {
                                        bb.getShort()/* & 0xFFFF */;
                                    }
                                } else {
                                    bb.getShort()/* & 0xFFFF */;
                                }
                            } else {
                                int i_15_ = (bb.get() & 0xFF);
                                int[] anIntArray3192 = new int[i_15_];
                                for (int i_16_ = 0; i_16_ < i_15_; i_16_++) {
                                    anIntArray3192[i_16_] = (bb.getShort() & 0xFFFF);
                                }
                            }
                        } else {
                            int i_17_ = (bb.get() & 0xFF);
                            byte[] aByteArray3205 = new byte[i_17_];
                            for (int i_18_ = 0; i_18_ < i_17_; i_18_++) {
                                aByteArray3205[i_18_] = bb.get();
                            }
                        }
                    } else {
                        int i_19_ = (bb.get() & 0xFF);
                        short[] aShortArray3183 = new short[i_19_];
                        short[] aShortArray3204 = new short[i_19_];
                        for (int i_20_ = 0; i_20_ < i_19_; i_20_++) {
                            aShortArray3183[i_20_] = (short) (bb.getShort() & 0xFFFF);
                            aShortArray3204[i_20_] = (short) (bb.getShort() & 0xFFFF);
                        }
                    }
                } else {
                    this.options[opcode - 30] = BufferUtils.readRS2String(bb);
                    return;
                }
            } else {
                this.size = (bb.get() & 0xFF);
                return;
            }
        } else {
            int i_21_ = (bb.get() & 0xFF);
            int[] anIntArray3230 = new int[i_21_];
            for (int i_22_ = 0; i_21_ > i_22_; i_22_++) {
                anIntArray3230[i_22_] = (bb.getShort() & 0xFFFF);
                if (anIntArray3230[i_22_] == 65535) {
                    anIntArray3230[i_22_] = -1;
                }
            }
        }

        stasis.preserve();
    }
}
