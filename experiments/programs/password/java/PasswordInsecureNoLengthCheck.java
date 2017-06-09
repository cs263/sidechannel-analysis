package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;

public class PasswordInsecureNoLengthCheck implements PasswordChecker {
    private static Dictionary d = new Dictionary();

    public static void main(String[] args) throws IOException {
        d.run(new PasswordInsecureNoLengthCheck());
    }

    public boolean checkPassword(String s, final String password) {
        for (int i = 0; i < 4; i++) {
            if (s.charAt(i) != password.charAt(i)) {
                return false;
            }
        }

        return true;    
    }
}
