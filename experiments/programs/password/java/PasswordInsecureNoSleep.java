package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;

public class PasswordInsecureNoSleep implements PasswordChecker {
    private static Dictionary d = new Dictionary();

    public static void main(String[] args) throws IOException {
        d.run(new PasswordInsecure());
    }

    public boolean checkPassword(String s, final String password) {
        if (s.length() != password.length()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != password.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
