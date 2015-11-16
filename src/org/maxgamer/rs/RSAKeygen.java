package org.maxgamer.rs;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSAKeygen {
	public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException{
		KeyFactory factory = KeyFactory.getInstance("RSA");
	    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	    keyGen.initialize(1024);
	    KeyPair keypair = keyGen.genKeyPair();
	    PrivateKey privateKey = keypair.getPrivate();
	    PublicKey publicKey = keypair.getPublic();
	    
	    RSAPrivateKeySpec privSpec = factory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
	    RSAPublicKeySpec pubSpec = factory.getKeySpec(publicKey, RSAPublicKeySpec.class);
	    
	    System.out.println("Generated RSA Public/Private keys:");
	    System.out.println("Private (Do not give this to anyone):");
	    System.out.printf("0x%X\n", privSpec.getModulus());
	    System.out.println("Exponent");
	    System.out.printf("0x%X\n", privSpec.getPrivateExponent());
	    
	    System.out.println("Public (Place this in client):");
	    System.out.printf("0x%X\n", pubSpec.getModulus());
	    System.out.println("Exponent");
	    System.out.printf("0x%X\n", pubSpec.getPublicExponent());
	    
	}
}
