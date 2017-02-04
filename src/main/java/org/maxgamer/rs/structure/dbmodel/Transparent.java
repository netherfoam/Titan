package org.maxgamer.rs.structure.dbmodel;

import org.maxgamer.rs.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * A class which helps in retrieving simple rows from a database, changing them,
 * and then updating them. This usually works for only simple models involving
 * one table.
 *
 * @author netherfoam
 */
public class Transparent {
    /**
     * The name of the table that this object belongs to
     */
    public final String TABLE;

    /**
     * The name of the key columns in the database
     */
    private final String[] keys;

    /**
     * The values which are stored in the database, parallel with the keys array.
     */
    private final Object[] identifiers; //The current values in the DB for the keys.

    public Transparent() {
        this.TABLE = null;
        this.keys = null;
        this.identifiers = null;
    }

    /**
     * Constructs a new Transparent object
     *
     * @param table  the name of the table the object belongs to
     * @param keys   the array of column names that are key columns (not null)
     * @param values the array of values, corresponding to the keys array, of the
     *               current data in the key columns.
     */
    public Transparent(String table, String[] keys, Object[] values) {
        if (keys.length <= 0) {
            throw new IllegalArgumentException("Keys length must be > 0");
        }
        if (values != null && values.length != keys.length) {
            throw new IllegalArgumentException("values.length(" + values.length + ") must == keys.length(" + keys.length + ")");
        }

        this.TABLE = table;
        this.keys = keys.clone();
        this.identifiers = values == null ? new Object[keys.length] : values.clone();

        if (values != null) {
            // Set the key value fields automatically
            //for(String fName : this.keys){
            for (int i = 0; i < keys.length; i++) {
                String fName = keys[i];
                Object o = values[i];

                try {
                    Field f = this.getClass().getDeclaredField(fName);
                    Mapping a = f.getAnnotation(Mapping.class);
                    if (a == null) {
                        throw new NoSuchFieldException("Field '" + fName + "' was found, but missing @Mapping attribute");
                    }
                    f.setAccessible(true);

                    if (f.getType().isPrimitive()) {
                        Class<?> type = f.getType();
                        Number n = (Number) o;

                        if (type == byte.class) {
                            f.set(this, n.byteValue());
                        } else if (type == char.class) {
                            f.set(this, (char) n.byteValue());
                        } else if (type == short.class) {
                            f.set(this, n.shortValue());
                        } else if (type == int.class) {
                            f.set(this, n.intValue());
                        } else if (type == long.class) {
                            f.set(this, n.longValue());
                        } else if (type == float.class) {
                            f.set(this, n.floatValue());
                        } else if (type == double.class) {
                            f.set(this, n.doubleValue());
                        } else {
                            throw new RuntimeException("I don't know what kind of primitive you are, " + f.getType() + " on " + f);
                        }
                    } else {
                        f.set(this, o);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("field: " + fName, e);
                }
            }
        }
    }

    /**
     * Inserts this object into the database.
     *
     * @param con the connection to insert it on
     * @throws SQLException
     */
    public void insert(Connection con) throws SQLException {
        if (TABLE == null) {
            throw new IllegalStateException("This Transparent object wasn't initialised with a table!");
        }
        try {
            HashSet<Field> fields = this.getFields();

            StringBuilder sb = new StringBuilder("INSERT INTO " + TABLE + " (");
            Iterator<Field> fit = fields.iterator();

            int i = 0;

            HashMap<String, Object> attributes = new HashMap<>(1);
            while (fit.hasNext()) {
                Field f = fit.next();

                Mapping a = f.getAnnotation(Mapping.class);
                Serializer s = a.serializer().newInstance();
                f.setAccessible(true);
                s.serialize(attributes, f, f.get(this));
            }

            Object[] values = new Object[attributes.size()];
            Iterator<Entry<String, Object>> ait = attributes.entrySet().iterator();
            Entry<String, Object> e = ait.next();
            sb.append(e.getKey());
            values[i++] = e.getValue();

            while (ait.hasNext()) {
                e = ait.next();
                sb.append(", ").append(e.getKey());

                values[i++] = e.getValue();
            }

            sb.append(") VALUES (?");

            for (i = 1; i < values.length; i++) {
                sb.append(", ?");
            }
            sb.append(")");

            PreparedStatement ps = con.prepareStatement(sb.toString());
            for (i = 0; i < values.length; i++) {
                ps.setString(i + 1, String.valueOf(values[i]));
            }

            ps.execute();
            //Our identifiers have been changed, so update them
            for (i = 0; i < this.keys.length; i++) {
                Field f = this.getClass().getDeclaredField(this.keys[i]);
                f.setAccessible(true);
                this.identifiers[i] = f.get(this);
            }
        } catch (IllegalAccessException | InstantiationException | SecurityException | NoSuchFieldException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates this object in the database.
     *
     * @param con the connection
     * @throws SQLException
     */
    public void update(Connection con) throws SQLException {
        if (TABLE == null) {
            throw new IllegalStateException("This Transparent object wasn't initialised with a table!");
        }

        try {
            HashSet<Field> fields = this.getFields();
            StringBuilder sb = new StringBuilder("UPDATE " + TABLE + " SET ");
            Iterator<Field> fit = fields.iterator();

            int i = 0;

            HashMap<String, Object> attributes = new HashMap<>(fields.size());
            while (fit.hasNext()) {
                Field f = fit.next();

                Mapping a = f.getAnnotation(Mapping.class);
                Serializer s = a.serializer().newInstance();
                f.setAccessible(true);
                s.serialize(attributes, f, f.get(this));
            }

            Object[] values = new Object[attributes.size()];
            Iterator<Entry<String, Object>> ait = attributes.entrySet().iterator();
            Entry<String, Object> e = ait.next();
            sb.append(e.getKey()).append(" = ?");
            values[i++] = e.getValue();

            while (ait.hasNext()) {
                e = ait.next();
                sb.append(", ").append(e.getKey()).append(" = ?");
                values[i++] = e.getValue();
            }

            sb.append(" WHERE");

            i = 0;

            //BUG: This will match the literal String 'null'
            if (identifiers[i] == null) {
                sb.append(" ").append(keys[i]).append(" IS NULL OR ").append(keys[i]).append(" = ?");
            } else {
                sb.append(" ").append(keys[i]).append(" = ?");
            }
            i++;

            while (i < keys.length) {
                //"Nothing compares to null!"
                // - Not even null itself. This is a best of both worlds workaround.
                if (identifiers[i] == null) {
                    sb.append(" AND (").append(keys[i]).append(" IS NULL OR ").append(keys[i]).append(" = ?)");
                } else {
                    sb.append(" AND ").append(keys[i]).append(" = ?");
                }

                i++;
            }

            PreparedStatement ps = con.prepareStatement(sb.toString());
            for (i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            for (i = 0; i < keys.length; i++) {
                ps.setObject(i + 1 + values.length, identifiers[i]);
            }

            ps.execute();

            //Our identifiers have been changed, so we update them
            for (i = 0; i < keys.length; i++) {
                Field f = this.getClass().getDeclaredField(this.keys[i]);
                f.setAccessible(true);
                this.identifiers[i] = f.get(this);
            }
        } catch (IllegalAccessException | InstantiationException | SecurityException | NoSuchFieldException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes this object from the database
     *
     * @param con the connection
     * @throws SQLException
     */
    public void delete(Connection con) throws SQLException {
        if (TABLE == null) {
            throw new IllegalStateException("This Transparent object wasn't initialised with a table!");
        }

        StringBuilder sb = new StringBuilder("DELETE FROM " + TABLE + " WHERE ");
        try {
            int i = 0;

            String k = keys[i++];
            sb.append(k).append(" = ?");

            while (i < keys.length) {
                k = keys[i++];
                sb.append(" AND ").append(k).append(" = ?");
            }
            sb.append(" LIMIT 1");

            PreparedStatement ps = con.prepareStatement(sb.toString());
            for (i = 0; i < keys.length; i++) {
                ps.setObject(i + 1, identifiers[i]);
            }

            ps.execute();
        } catch (SQLException e) {
            Log.info("Keys: " + Arrays.toString(keys) + ", values: " + Arrays.toString(identifiers) + ", SQL: " + sb.toString());
            throw e;
        }
    }

    /**
     * Reloads this object from the database
     *
     * @param rs the result set. If fields are in this object which have the @Mapping annotation, and
     *           the result set contains no such matching column, an SQL exception is guaranteed to be
     *           thrown. The opposite (Extra columns in the result set) will not cause an exception.
     * @throws SQLException
     */
    public void reload(ResultSet rs) throws SQLException {
        try {
            for (Field f : this.getFields()) {
                Mapping a = f.getAnnotation(Mapping.class);
                Serializer s = a.serializer().newInstance();
                Object o = s.deserialize(f, rs);
                f.setAccessible(true);
                f.set(this, o);

                //Now we must update our identifiers
                if (keys != null) {
                    for (int i = 0; i < this.keys.length; i++) {
                        String key = this.keys[i];
                        if (key.equals(f.getName())) {
                            this.identifiers[i] = o;
                        }
                    }
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * All fields in this transparent object with the @Mapping annotation
     *
     * @return All fields in this transparent object with the @Mapping annotation
     */
    private HashSet<Field> getFields() {
        HashSet<Field> fields = new HashSet<>();
        for (Field f : getClass().getDeclaredFields()) {
            Annotation a = f.getAnnotation(Mapping.class);
            if (a == null) continue;

            fields.add(f);
        }

        return fields;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("getClass().getCanonicalName() (Transparent)\n");
        for (Field f : this.getFields()) {
            f.setAccessible(true);
            try {

                if (f.getType().isArray()) {
                    Object v = f.get(this);
                    Object[] arr = new Object[Array.getLength(v)];
                    for (int i = 0; i < arr.length; i++) {
                        arr[i] = Array.get(v, i);
                    }
                    sb.append(f.getName()).append(" = ").append(Arrays.toString(arr)).append("\n");
                } else {
                    sb.append(f.getName()).append(" = ").append(f.get(this)).append("\n");
                }
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
        return sb.toString();
    }
}