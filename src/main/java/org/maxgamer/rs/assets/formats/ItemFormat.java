package org.maxgamer.rs.assets.formats;

import org.maxgamer.rs.model.item.condition.ItemMetadataSet;
import org.maxgamer.rs.util.BufferUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * @author netherfoam
 */
public class ItemFormat extends Format {
    protected transient int interfaceModelId;

    // The IDs of the models to paint on characters (Eg on the real character, not an interface)
    protected transient int maleWornModelId1 = -1;
    protected transient int maleWornModelId2 = -1;
    protected transient int femaleWornModelId1 = -1;
    protected transient int femaleWornModelId2 = -1;
    protected String name;

    private transient int modelOffset1;
    private transient int modelOffset2;
    private transient boolean membersOnly;
    private transient String[] groundOptions = new String[]{null, "Take", null, null, null};
    private transient String[] inventoryOptions = new String[]{null, null, null, null, "Drop"};
    private transient boolean unnoted;
    private transient int[] stackIds;
    private transient int[] stackAmounts;
    private transient int teamId;
    private transient HashMap<Integer, Object> clientScriptData;
    private transient ItemMetadataSet flags;

    private static void discard(ByteBuffer bb, int op, int bytes) {
        for (int i = 0; i < bytes; i++) {
            bb.get();
        }
    }

    @Override
    public void decode(int opcode, ByteBuffer buffer, Stasis stasis) throws IOException {
        switch (opcode) {
            case 4:
            /* modelZoom = */
                discard(buffer, opcode, 2);/* & 0xFFFF */
                stasis.preserve();
                break;
            case 5:
            /* modelRotation1 = */
                discard(buffer, opcode, 2);/* & 0xFFFF */
                stasis.preserve();
                break;
            case 6:
            /* modelRotation2 = */
                discard(buffer, opcode, 2);/* & 0xFFFF */
                stasis.preserve();
                break;
            case 11:
                //stackable = true;
                stasis.preserve();
                break;
            case 12:
            /* value = */
                buffer.getInt();
                stasis.preserve();
                break;
            case 23:
                maleWornModelId1 = buffer.getShort() & 0xFFFF;
                stasis.preserve();
                break;
            case 25:
                maleWornModelId2 = buffer.getShort() & 0xFFFF;
                stasis.preserve();
                break;
            case 26:
                femaleWornModelId2 = buffer.getShort() & 0xFFFF;
                stasis.preserve();
                break;
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
                inventoryOptions[opcode - 35] = BufferUtils.readRS2String(buffer);
                break;
            case 40: {
                int length = buffer.get() & 0xFF;
                int[] originalModelColors = new int[length];
                int[] modifiedModelColors = new int[length];
                for (int index = 0; index < length; index++) {
                    originalModelColors[index] = buffer.getShort() & 0xFFFF;
                    modifiedModelColors[index] = buffer.getShort() & 0xFFFF;
                }
                stasis.preserve();
                break;
            }
            case 41: {
                int length = buffer.get() & 0xFF;
                int[] textureColour1 = new int[length];
                int[] textureColour2 = new int[length];
                for (int index = 0; index < length; index++) {
                    textureColour1[index] = buffer.getShort() & 0xFFFF;
                    textureColour2[index] = buffer.getShort() & 0xFFFF;
                }
                stasis.preserve();
                break;
            }
            case 42: {
                int length = buffer.get() & 0xFF;
                byte[] unknownArray1 = new byte[length];
                for (int index = 0; index < length; index++) {
                    unknownArray1[index] = buffer.get();
                }
                stasis.preserve();
                break;
            }
            case 65:
                unnoted = true;
                stasis.preserve();
                break;
            case 78:
            /* colourEquip1 = */
                buffer.getShort()/* & 0xFFFF */;
                stasis.preserve();
                break;
            case 79:
            /* colourEquip2 = */
                buffer.getShort()/* & 0xFFFF */;
                stasis.preserve();
                break;
            case 91:
                // buffer.getShort();
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 98:
            /* certTemplateId = */
                discard(buffer, opcode, 2); /* & 0xFFFF */
                stasis.preserve();
                break;
            case 110:
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 111:
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 115:
                teamId = buffer.get() & 0xFF;
                stasis.preserve();
                break;
            case 122:
            /* lendTemplateId = */
                buffer.getShort()/* & 0xFFFF */;
                stasis.preserve();
                break;
            case 130:
                discard(buffer, opcode, 1);
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 139:
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 249: {
                int length = buffer.get() & 0xFF;
                if (clientScriptData == null) {
                    clientScriptData = new HashMap<>();
                }
                for (int index = 0; index < length; index++) {
                    boolean stringInstance = buffer.get() == 1;
                    int key = BufferUtils.getTriByte(buffer);
                    Object value = stringInstance ? BufferUtils
                            .readRS2String(buffer) : buffer.getInt();
                    clientScriptData.put(key, value);
                }
                stasis.preserve();
                break;
            }
            case 140:
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 134:
                discard(buffer, opcode, 1);
                stasis.preserve();
                break;
            case 132: {
                int length = buffer.get() & 0xFF;
                int[] unknownArray2 = new int[length];
                for (int index = 0; index < length; index++) {
                    unknownArray2[index] = buffer.getShort() & 0xFFFF;
                }
                stasis.preserve();
                break;
            }
            case 129:
            case 128:
            case 127:
                discard(buffer, opcode, 1);
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 126:
            case 125:
                buffer.get();
                buffer.get();
                buffer.get();
                stasis.preserve();
                break;
            case 121:
            /* lendId = */
                buffer.getShort()/* & 0xFFFF */;
                stasis.preserve();
                break;
            case 114:
                buffer.get();
                stasis.preserve();
                break;
            case 113:
                buffer.get();
                stasis.preserve();
                break;
            case 112:
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
                if (stackIds == null) {
                    stackIds = new int[10];
                    stackAmounts = new int[10];
                }
                stackIds[opcode - 100] = buffer.getShort() & 0xFFFF;
                stackAmounts[opcode - 100] = buffer.getShort() & 0xFFFF;
                stasis.preserve();
                break;
            case 96:
                buffer.get();
                // certId = buffer.getShort();
                stasis.preserve();
                break;
            case 97:
            /* certTemplateId = */
                buffer.getShort();
                stasis.preserve();
                break;
            case 95:
            case 93:
            case 92:
            case 90: // unknown
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                groundOptions[opcode - 30] = BufferUtils.readRS2String(buffer);
                stasis.preserve();
                break;
            case 24:
                femaleWornModelId1 = buffer.getShort();
                stasis.preserve();
                break;
            case 18:
                discard(buffer, opcode, 2);
                stasis.preserve();
                break;
            case 16:
                membersOnly = true;
                stasis.preserve();
                break;
            case 8:
                modelOffset2 = buffer.getShort() & 0xFFFF;
                if (modelOffset2 > 32767)
                    modelOffset2 -= 65536;
                modelOffset2 <<= 0;
                stasis.preserve();
                break;
            case 7:
                modelOffset1 = buffer.getShort() & 0xFFFF;
                if (modelOffset1 > 32767)
                    modelOffset1 -= 65536;
                modelOffset1 <<= 0;
                stasis.preserve();
                break;
            case 2:
                name = BufferUtils.readRS2String(buffer);
                stasis.preserve();
                break;
            case 1:
                interfaceModelId = buffer.getShort() & 0xFFFF;
                stasis.preserve();
                break;
            default:
                throw new IOException("Unknown opcode " + opcode);
        }
    }

    @Override
    public void encode(ByteArrayOutputStream out) throws IOException {
        // No extra data written here, yet.
        for(int i = 0; i < 5; i++) {
            String option = inventoryOptions[i];

            if(option == null) continue;
            if(i == 4 && "Drop".equals(option)) continue;

            out.write((byte) (i + 35));
            for(int j = 0; j < option.length(); j++) {
                out.write((byte) option.charAt(j));
            }
            out.write((byte) 0);
        }
    }

    public void setInventoryOption(int id, String option) {
        this.inventoryOptions[id] = option;
    }

    public String getInventoryOption(int i) {
        return inventoryOptions[i];
    }
}
