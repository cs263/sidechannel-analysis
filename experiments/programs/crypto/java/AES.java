package experiments.programs.crypto.java;

import javax.crypto.Cipher;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//A simple example of AES encryption using the standard java cryptographic library 
//Example based off of https://www.madirish.net/561

public class AES{
	static byte[] iv = new byte[16];
	static String encrypt_key = "26-ByteSharedKey";
	static byte key_to_bytes[] = encrypt_key.getBytes();

	public static byte[] encrypt(byte[] plainText, byte[] encryption_key) throws Exception{ 
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryption_key, "AES");
        System.out.println("Starting encryption");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(plainText);
	}

	public static byte[] decrypt(byte[] cipherText, byte[] encryption_key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(encryption_key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(iv));
        System.out.println("Statring decryption");
        return cipher.doFinal(cipherText);
	}

	public static void main(String[] args) throws Exception{
		if (args.length != 1){
			System.out.println("Please provide a plaintext to encrypt");
			System.exit(1);
		}
		byte[] plainText = args[0].getBytes("UTF8");
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(iv); //generates a new initialization vector each time 
		try {
			byte[] cipherText = encrypt(plainText, key_to_bytes);
			System.out.println("Finished encryption: ");
			System.out.println(new String(cipherText, "UTF8") + '\n');
			byte[] newPlainText = decrypt(cipherText, key_to_bytes);
			System.out.println("Finished decryption: ");
			System.out.println(new String(newPlainText, "UTF8"));
		}
		catch (Exception e) {
            e.printStackTrace();
        }
	}
}