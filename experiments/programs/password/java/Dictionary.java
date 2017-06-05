package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;

public class Dictionary {
    static SecureRandom rnd = new SecureRandom();
    
    private static StringBuffer buf = new StringBuffer();    
    private static final ArrayList<String> words = new ArrayList<String>();
    private static String password;
    private static long t;

    public Dictionary() {
        try {
            Files.lines(Paths.get("words.txt")).forEach(e -> words.add(e));
        } catch(IOException e) {}
    }

    public void run(PasswordChecker pc) {
        password = words.get(rnd.nextInt(words.size()));
        System.out.println(password + "\n===========================");
        for(int i = 0; i < 1000000; i++) {
            String chosen = words.get(rnd.nextInt(words.size() - 1));
            while (chosen.length()< 4){
                chosen = words.get(rnd.nextInt(words.size() - 1));

            }
            t = System.nanoTime();
            pc.checkPassword(chosen, password);
            t = System.nanoTime() - t;
            buf.append(String.format("%10d, %s\n", t, chosen));
        }

        System.out.println(buf.toString());
    }
}
