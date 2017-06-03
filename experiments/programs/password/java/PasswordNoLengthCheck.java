package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;

public class PasswordNoLengthCheck implements PasswordChecker {
    private static Dictionary d = new Dictionary();

    public static void main(String[] args) throws IOException {
        d.run(new PasswordNoLengthCheck());
    }

    public boolean checkPassword(String s, final String password) {
        int min = Math.min(s.length(), password.length());

        for (int i = 0; i < min; i++) {
            try {
                Thread.sleep(1);
            } catch(Exception e) {}

            if (s.charAt(i) != password.charAt(i)) {
                return false;
            }
        }

        return true;    
    }
}