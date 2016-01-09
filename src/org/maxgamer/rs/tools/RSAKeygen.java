package org.maxgamer.rs.tools;

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

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.structure.configs.FileConfig;

public class RSAKeygen {
	public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException{
		KeyFactory factory = KeyFactory.getInstance("RSA");
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		
		Prompter prompt = new Prompter();
		prompt.println("== RSA Key Pair Generator ==");
		prompt.println("How many bytes would you like your RSA key? [1024]: ");
		keyGen.initialize(prompt.getInt(1024));
		
		KeyPair keypair = keyGen.genKeyPair();
	    PrivateKey privateKey = keypair.getPrivate();
	    PublicKey publicKey = keypair.getPublic();
	    
	    RSAPrivateKeySpec privSpec = factory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
	    RSAPublicKeySpec pubSpec = factory.getKeySpec(publicKey, RSAPublicKeySpec.class);
	    
	    
	    prompt.println("Private Modulus (For Server):");
	    prompt.printf("%X\n", privSpec.getModulus());
	    prompt.println("Private Exponent (For Server):");
	    prompt.printf("%X\n", privSpec.getPrivateExponent());
	    prompt.println();
	    prompt.println("Public Modulus (For Client):");
	    prompt.printf("%X\n", pubSpec.getModulus());
	    prompt.println("Public Exponent  (For Client):");
	    prompt.printf("%X\n", pubSpec.getPublicExponent());
	    
	    prompt.println("Should I insert the private modulus/exponent into your config/world.yml now? [no]:");
	    if(prompt.getBoolean(false)){
	    	FileConfig config = Core.getWorldConfig();
	    	config.set("rsa.private-key", String.format("%X", privSpec.getModulus()));
	    	config.set("rsa.private-exponent", String.format("%X", privSpec.getPrivateExponent()));
	    	config.save();
	    }
	    
	    prompt.println("Done! Don't forget to insert your public modulus and exponent into your client!");
	    prompt.close();
	}
}
