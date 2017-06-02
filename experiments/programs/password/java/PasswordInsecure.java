package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;

public class PasswordInsecure {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
    static SecureRandom rnd = new SecureRandom();

    String randomString( int len ){
	StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
		            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	         return sb.toString();
    }
    
    private static StringBuffer buf = new StringBuffer();    
    private static String password;
    private static final ArrayList<String> words = new ArrayList<String>();
    private static boolean result;
    private static long t;

    public static boolean checkPassword(String s) {
    	if (s.length() != password.length()) {
    		return false;
    	}
        for (int i = 0; i < s.length(); i++) {
	    try {
    		Thread.sleep(10);
	    } catch(Exception e) {}

            if (s.charAt(i) != password.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
	Files.lines(Paths.get("words.txt")).forEach(e -> words.add(e));
	
	password = words.get(rnd.nextInt(words.size()));
	System.out.println(password + "\n===========================");
	for(int i = 0; i < 10000; i++) {
		String chosen = words.get(rnd.nextInt(words.size() - 1));
		t = System.nanoTime();
		result = checkPassword(chosen);
		t = System.nanoTime() - t;
		buf.append(t).append("\t\t").append(chosen).append("\n");
	}

	System.out.println(buf.toString());
    }
}
