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
    private int[] transformIds;
    private String[] options;
    private String name = "";
    private boolean clippingFlag;
    private boolean isSolid;
    private int sizeY;
    private int sizeX;
    private int primaryState;
    private int secondaryState;

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
    public void encode(ByteArrayOutputStream out) throws IOException {

    }

    @Override
    public void decode(int opcode, ByteBuffer buffer, Stasis stasis) throws IOException {
        if (opcode == 2) {
            this.name = ByteBufferUtils.getJagexString(buffer);
        }

        // These opcodes are processed, but they're all preserved anyway. We don't know what they mean.
        if (opcode == 1 || opcode == 5) {
            boolean creatorBoolean = false; //A boolean from the creator class in the client
            if (opcode == 5 && creatorBoolean) {
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

            if (opcode == 5 && !creatorBoolean) {
                length = buffer.get() & 0xFF;
                for (int j = 0; j < length; j++) {
                    buffer.get();

                    int n = (buffer.get() & 0xFF) * 2;
                    buffer.position(buffer.position() + n);
                }
            }
        }

        if((~opcode) == -178) {
            // Empty
        }

        if(~opcode == -108) {
            buffer.getShort();
        }

        if(~opcode == -82) {
            buffer.get(); // & 0xFF
        }

        if(opcode == 14) {
            this.sizeX = buffer.get() & 0xFF;
        }

        if(opcode == 15) {
            this.sizeY = buffer.get() & 0xFF;
        }

        if(~opcode == -18) {
            this.isSolid = false;
            this.actionCount = 0;
        }

        if(opcode == 18) {
            this.isSolid = false;
        }

        if(opcode == 19) {
            buffer.get(); // & 0xFF
        }

        if(opcode == 22) {
            // Empty
        }

        if(opcode == 21) {
            // Empty
        }

        if(opcode == 23) {
            // Empty
        }

        if(~opcode == -25) {
            buffer.getShort();
        }

        if(opcode == 27) {
            this.actionCount = 1;
        }

        if(opcode == 28) {
            buffer.get(); // & 0xFF
        }

        if(opcode == 29) {
            buffer.get();
        }

        if(~opcode == -40) {
            buffer.get();
        }

        if(opcode >= 30 && opcode < 35) {
            options[opcode - 30] = ByteBufferUtils.getJagexString(buffer);
        }

        if(opcode == 40) {
            int length = buffer.get() & 0xFF;
            short[] first = new short[length];
            short[] second = new short[length];
            for (int i = 0; i < length; i++) {
                first[i] = buffer.getShort();
                second[i] = buffer.getShort();
            }
        }

        if(~opcode == -42) {
            int length = buffer.get() & 0xFF;
            short[] first = new short[length];
            short[] second = new short[length];
            for (int i = 0; i < length; i++) {
                first[i] = buffer.getShort();
                second[i] = buffer.getShort();
            }
        }

        if(~opcode == -43) {
            int length = (buffer.get() & 0xFF);
            byte[] data = new byte[length];
            for(int i = 0; i < length; i++) {
                data[i] = buffer.get();
            }
        }

        if(opcode == 62) {
            // Empty
        }

        if(opcode == 64) {
            // Empty
        }

        if(opcode == 65) {
            buffer.getShort();
        }

        if(~opcode == -67) {
            buffer.getShort();
        }

        if(~opcode == -68) {
            buffer.getShort();
        }

        if(~opcode == -70) {
            buffer.get(); // & 0xFF
        }

        if(~opcode == -71) {
            buffer.getShort(); // & 0xFFFF
        }

        if(~opcode == -72) {
            buffer.getShort(); // & 0xFFFF
        }

        if(~opcode == -73) {
            buffer.getShort(); // & 0xFFFF
        }

        if(opcode == 73) {
            // Empty
        }

        if(opcode == 74) {
            this.clippingFlag = true;
            this.isSolid = true;
            this.actionCount = 0;
        }

        if(opcode == 75) {
            buffer.get(); // & 0xFF
        }

        if(~opcode == -78 || ~opcode == -93) {
            primaryState = buffer.getShort() & 0xFFFF;
            if(primaryState == 0xFFFF ) {
                primaryState = -1;
            }

            secondaryState = buffer.getShort() & 0xFFFF;
            if(secondaryState == 0xFFFF) {
                secondaryState = -1;
            }

            int id = -1;
            if(opcode == 92) {
                id = buffer.getShort() & 0xFFFF;

                if(id == 0xFFFF) {
                    id = -1;
                }
            }

            int length = buffer.get() & 0xFF;
            this.transformIds = new int[length + 2];
            for(int i = 0; i <= length; i++) {
                this.transformIds[i] = buffer.getShort() & 0xFFFF;

                if(transformIds[i] == 0xFFFF) transformIds[i] = -1;
            }
            transformIds[length + 1] = id;
        }

        if(opcode == 78) {
            buffer.getShort();
            buffer.get(); // & 0xFF
        }

        if(opcode == 79) {
            buffer.getShort(); // & 0xFFFF
            buffer.getShort(); // & 0xFFFF
            buffer.get(); // & 0xFF

            int length = buffer.get() & 0xFF;
            int[] data = new int[length];

            for(int i = 0; i < length; i++) {
                data[i] = buffer.getShort();
            }
        }

        if(opcode == 82) {
            // Empty
        }

        if(opcode == 88) {
            // Empty
        }

        if(~opcode == -90) {
            // Empty
        }

        if(opcode == 91) {
            // Empty
        }

        if(opcode == 93) {
            buffer.getShort();
        }

        if(opcode == 94) {
            // Empty
        }

        if(~opcode == -96) {
            buffer.getShort();
        }

        if(~opcode == -98) {
            // Empty
        }

        if(opcode == 98) {
            // Empty
        }

        if(~opcode == -100) {
            buffer.get(); // & 0xFF
            buffer.getShort();
        }

        if(opcode == 100) {
            buffer.get();
            buffer.getShort();
        }

        if(opcode == 101) {
            buffer.get(); // & 0xFF
        }

        if(opcode == 102) {
            buffer.getShort();
        }

        if(~opcode == -104) {
            // Empty
        }

        if(~opcode == -105) {
            buffer.get(); // & 0xFF
        }

        if(~opcode == -106) {
            // Empty
        }


        if((~opcode) == -179) {
            buffer.get(); // & 0xFF
        }

        if (opcode == 106) {
            int length = buffer.get(); // & 0xFF
            int[] firsts = new int[length];
            int[] seconds = new int[length];

            for (int i = 0; i < length; i++) {
                seconds[i] = buffer.getShort();
                firsts[i] = buffer.get() & 0xFF;
            }
        }

        if(opcode >= 150 && opcode < 155) {
            this.options[opcode - 150] = ByteBufferUtils.getJagexString(buffer);
        }

        if(opcode == 160) {
            int length = buffer.get() & 0xFF;
            int[] data = new int[length];
            for (int i = 0; i < length; i++)
                data[i] = buffer.getShort();
        }

        if(opcode == 162) {
            buffer.getInt();
        }

        if(opcode == 163) {
            buffer.get();
            buffer.get();
            buffer.get();
            buffer.get();
        }

        if(opcode == 164) {
            buffer.getShort(); // & 0xFFFF
        }

        if(~opcode == 166) {
            buffer.getShort(); // & 0xFFFF
        }

        if(~opcode == -167) {
            buffer.getShort();
        }

        if(~opcode == -168) {
            buffer.getShort();
        }

        if(~opcode == -169) {
            // Empty
        }

        if(opcode == 169) {
            // Empty
        }

        if(~opcode == -171) {
            ByteBufferUtils.readSmart(buffer);
        }

        if(~opcode == -172) {
            ByteBufferUtils.readSmart(buffer);
        }

        if(opcode == 173) {
            buffer.getShort();
            buffer.getShort();
        }

        if(opcode == 249) {
            int length = buffer.get() & 0xFF;
            for (int i = 0; i < length; i++) {
                boolean isString = (buffer.get() & 0xFF) == 1;
                ByteBufferUtils.getTriByte(buffer);
                if (!isString) buffer.getInt();
                else ByteBufferUtils.getJagexString(buffer);
            }
        }

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

    public int getPrimaryState() {
        return primaryState;
    }

    public int getSecondaryState() {
        return secondaryState;
    }

    @Override
    public String toString() {
        return ReflectUtil.describe(this);
    }

    public int[] getAliases() {
        return this.transformIds;
    }

    public void setOption(int index, String option) {
        this.options[index] = option;
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
        return Objects.deepEquals(this.transformIds, other.transformIds)
                && Objects.deepEquals(this.options, other.options)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.clippingFlag, other.clippingFlag)
                && Objects.equals(this.isSolid, other.isSolid)
                && Objects.equals(this.sizeY, other.sizeY)
                && Objects.equals(this.sizeX, other.sizeX)
                && Objects.equals(this.primaryState, other.primaryState)
                && Objects.equals(this.secondaryState, other.secondaryState)
                && Objects.equals(this.actionCount, other.actionCount);
    }
}
