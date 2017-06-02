package experiments.programs.crypto.java;

import java.security.*;
import javax.crypto.*;

//Code based off https://www.ibm.com/developerworks/java/tutorials/j-sec1/j-sec1.html
public class DES {

	private Key key; 

	public static byte[] encrypt(byte[] plaintext) throws Exception {
		System.out.println("Generating DES key");
		Key key = keyGeneration();
		System.out.println("Finished generated DES key \n");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		System.out.println("Starting encryption");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(plaintext);
		System.out.println("Finished encryption: ");
		System.out.println(new String(cipherText, "UTF8") + '\n');
		//Check that the result is correct!
		System.out.println("Starting decryption");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] newPlainText = cipher.doFinal(cipherText);
		System.out.println("Finished decryption: ");
		System.out.println(new String(newPlainText, "UTF8"));
		return cipherText;
	}


	public static Key keyGeneration() throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		keyGen.init(56);
		Key key = keyGen.generateKey();
		return key;
	}

	public static void main (String[] args) throws Exception{
		if (args.length != 1){
			System.out.println("Please provide a plaintext to encrypt");
			System.exit(1);
		}
		byte[] plainText = args[0].getBytes("UTF8");
		encrypt(plainText);

	}
}
