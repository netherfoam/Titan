package org.maxgamer.rs.assets.formats;

import net.openrs.util.ByteBufferUtils;
import org.maxgamer.rs.network.io.stream.RSOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class EnumFormat extends Format {
    private static final char[] UNICODE_UNESCAPES = {
            '\u20ac', '\0', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020',
            '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\0',
            '\u017d', '\0', '\0', '\u2018', '\u2019', '\u201c', '\u201d',
            '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a',
            '\u0153', '\0', '\u017e', '\u0178'};

    public static final char decodeCp1252Char(byte charValue) {
        int unsignedValue = 0xff & charValue;

        if ((unsignedValue ^ 0xffffffff) == -1) {
            throw new IllegalArgumentException("Non cp1252 character 0x" + Integer.toString(unsignedValue, 16) + " provided");
        }

        if (unsignedValue >= 128 && (~unsignedValue) > -161) {
            int escapedValue = UNICODE_UNESCAPES[unsignedValue - 128];
            if ((escapedValue ^ 0xffffffff) == -1) {
                escapedValue = 63;
            }
            unsignedValue = escapedValue;
        }

        return (char) unsignedValue;
    }

    private char aChar;
    private char bChar;
    private int anInt;

    private String defaultValue;
    private Map<Integer, Object> values;

    public EnumFormat(ByteBuffer buffer) throws IOException {
        decode(buffer);
    }

    @Override
    public void encode(ByteArrayOutputStream out) throws IOException {
        RSOutputStream stream = new RSOutputStream();
        if(defaultValue != null) {
            stream.write(3);
            stream.writePJStr1(defaultValue);
        }

        Map<Integer, String> strings = new LinkedHashMap<>(values.size());

        for(Map.Entry<Integer, Object> entry : values.entrySet()) {
            if(!(entry.getValue() instanceof String)) continue;
            strings.put(entry.getKey(), (String) entry.getValue());
        }

        Map<Integer, Integer> ints = new LinkedHashMap<>(values.size());
        for(Map.Entry<Integer, Object> entry : values.entrySet()) {
            if(!(entry.getValue() instanceof Integer)) continue;
            ints.put(entry.getKey(), (Integer) entry.getValue());
        }

        if(!strings.isEmpty()) {
            stream.writeByte(5);
            stream.writeShort(strings.size());

            for(Map.Entry<Integer, String> entry : strings.entrySet()) {
                stream.writeInt(entry.getKey());
                stream.writePJStr1(entry.getValue());
            }
        }

        if(!ints.isEmpty()) {
            stream.writeByte(6);
            stream.writeShort(ints.size());

            for(Map.Entry<Integer, Integer> entry : ints.entrySet()) {
                stream.writeInt(entry.getKey());
                stream.writeInt(entry.getValue());
            }
        }

        out.write(stream.getPayload());
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void set(int key, String value) {
        values.put(key, value);
    }

    public void set(int key, int value) {
        values.put(key, value);
    }

    public String getString(int key) {
        String s = (String) values.get(key);
        if(s == null) return defaultValue;

        return s;
    }

    public int getInt(int key) {
        return (Integer) values.get(key);
    }

    @Override
    public void decode(int opcode, ByteBuffer buffer, Stasis stasis) throws IOException {
        if (opcode == 1) {
            aChar = decodeCp1252Char(buffer.get());
            stasis.preserve();
            return;
        }

        if(opcode == 2) {
            bChar = decodeCp1252Char(buffer.get());
            stasis.preserve();
            return;
        }

        if(opcode == 3) {
            defaultValue = ByteBufferUtils.getJagexString(buffer);
            return;
        }

        if(opcode == 4) {
            // Unknown
            anInt = buffer.getInt();
            stasis.preserve();
            return;
        }

        if(opcode == 5 || opcode == 6) {
            int length = buffer.getShort();
            values = new LinkedHashMap<>(length);

            for(int i = 0; i < length; i++) {
                int hash = buffer.getInt();

                if(opcode == 5) {
                    // Strings
                    values.put(hash, ByteBufferUtils.getJagexString(buffer));
                } else {
                    // Integers
                    values.put(hash, buffer.getInt());
                }
            }
            return;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnumFormat that = (EnumFormat) o;

        if (aChar != that.aChar) return false;
        if (bChar != that.bChar) return false;
        if (anInt != that.anInt) return false;
        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null) return false;

        return values != null ? values.equals(that.values) : that.values == null;
    }

    @Override
    public int hashCode() {
        int result = (int) aChar;
        result = 31 * result + (int) bChar;
        result = 31 * result + anInt;
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (values != null ? values.hashCode() : 0);

        return result;
    }
}
