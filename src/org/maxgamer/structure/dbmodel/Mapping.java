package org.maxgamer.structure.dbmodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mapping class that is used by transparent objects to define class properties
 * that are to be serialized and stored in the database, or extracted from the
 * database.
 * @author netherfoam
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mapping {
	/**
	 * The serializer class that is to be used. The serializer class will be constructed
	 * using its no-args constructor. The default value is a {@link PrimitiveSerializer}
	 * @return the serializer class to use not null
	 */
    public Class<? extends Serializer> serializer() default PrimitiveSerializer.class;
}
