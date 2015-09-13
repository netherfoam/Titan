package net.openrs.util.crypto;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * An implementation of the RSA algorithm.
 * @author Graham
 * @author `Discardedx2
 */
public final class Rsa {

	/**
	 * Encrypts/decrypts the specified buffer with the key and modulus.
	 * @param buffer The input buffer.
	 * @param modulus The modulus.
	 * @param key The key.
	 * @return The output buffer.
	 */
	public static ByteBuffer crypt(ByteBuffer buffer, BigInteger modulus, BigInteger key) {
		byte[] bytes = new byte[buffer.limit()];
		buffer.get(bytes);

		BigInteger in = new BigInteger(bytes);
		BigInteger out = in.modPow(key, modulus);

		return ByteBuffer.wrap(out.toByteArray());
	}

	/**
	 * Default private constructor to prevent instantiation.
	 */
	private Rsa() {
		
	}

}
