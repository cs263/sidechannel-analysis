package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;

public class PasswordConstantTime implements PasswordChecker {
    private static Dictionary d = new Dictionary();

    public static void main(String[] args) throws IOException {
        d.run(new PasswordConstantTime());
    }

    public boolean checkPassword(String s, final String password){
        if (s.length() != password.length()){
            return false;
        }
        int res = 0;
        for (int i = 0; i < s.length(); i++){
            try {
                Thread.sleep(1);
            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            res |= s.charAt(i) ^ password.charAt(i);
        }
        return res == 0;
    }
}