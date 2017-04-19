package org.maxgamer.rs.assets.formats;

import org.maxgamer.rs.network.io.stream.RSOutputStream;
import org.maxgamer.rs.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Contains information regarding the default player look and title enum set
 * @author netherfoam
 */
public class DefaultPlayerSettingsFormat extends Format {
    /**
     * Describes the clothing for a default player - This is a fixed length
     */
    private final int[] defaultPlayerLook = new int[6];

    /**
     * The array of male title groups. At most, 4 of these will be used. Each group may have upto 255 titles
     */
    private int[] maleTitleIds = new int[0];

    /**
     * The array of female title groups
     */
    private int[] femaleTitleIds = new int[0];

    public DefaultPlayerSettingsFormat() {
    }

    public DefaultPlayerSettingsFormat(ByteBuffer content) throws IOException {
        decode(content);
    }

    @Override
    public void encode(ByteArrayOutputStream out) throws IOException {
        RSOutputStream stream = new RSOutputStream();
        stream.writeByte(1);
        for(int i = 0; i < defaultPlayerLook.length; i++) {
            stream.writeShort(defaultPlayerLook[i]);
        }

        stream.writeByte(4);
        encodeTitles(stream, maleTitleIds);

        stream.writeByte(5);
        encodeTitles(stream, femaleTitleIds);

        // End of file
        stream.writeByte(0);

        out.write(stream.getPayload());
    }

    @Override
    public void decode(int opcode, ByteBuffer buffer, Stasis stasis) throws IOException {
        if(opcode == 1) {
            for(int i = 0; i < defaultPlayerLook.length; i++) {
                defaultPlayerLook[i] = buffer.getShort();
            }
            return;
        }

        if(opcode == 4) {
            maleTitleIds = decodeTitles(buffer);
            return;
        }

        if(opcode == 5) {
            femaleTitleIds = decodeTitles(buffer);
            return;
        }

        throw new IOException("Bad opcode: " + opcode);
    }

    private void encodeTitles(RSOutputStream stream, int[] titles) {
        Assert.isTrue(titles.length <= 255, "Max of 255 titles");
        stream.writeByte(titles.length);
        for(int i = 0; i < titles.length; i++) {
            stream.writeShort(titles[i]);
        }
    }

    private int[] decodeTitles(ByteBuffer buffer) {
        int[] titleIds = new int[buffer.get() & 0xFF];
        for(int i = 0; i < titleIds.length; i++) {
            int v = buffer.getShort() & 0xFFFF;
            if(v == 0xFFFF) v = -1;

            titleIds[i] = v;
        }

        return titleIds;
    }

    public int getDefaultPlayerLook(int type) {
        return defaultPlayerLook[type];
    }

    public void setDefaultPlayerLook(int type, int look) {
        defaultPlayerLook[type] = look;
    }

    public int[] getFemaleTitleIds() {
        return femaleTitleIds;
    }

    public void setFemaleTitleIds(int[] femaleTitleIds) {
        this.femaleTitleIds = femaleTitleIds;
    }

    public int[] getMaleTitleIds() {
        return maleTitleIds;
    }

    public void setMaleTitleIds(int[] maleTitleIds) {
        this.maleTitleIds = maleTitleIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultPlayerSettingsFormat that = (DefaultPlayerSettingsFormat) o;

        if (!Arrays.equals(defaultPlayerLook, that.defaultPlayerLook)) return false;
        if (!Arrays.equals(maleTitleIds, that.maleTitleIds)) return false;

        return Arrays.equals(femaleTitleIds, that.femaleTitleIds);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(defaultPlayerLook);
        result = 31 * result + Arrays.hashCode(maleTitleIds);
        result = 31 * result + Arrays.hashCode(femaleTitleIds);

        return result;
    }
}
