package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;

public class PasswordPossibly implements PasswordChecker {
    private static Dictionary d = new Dictionary();

    public static void main(String[] args) throws IOException {
        d.run(new PasswordPossibly());
    }

    public boolean checkPassword(String s, final String password){
        boolean flag = true; 
        if (s.length() != password.length()){
            return false;
        }
        for (int i = 0; i < s.length(); i++){
            try {
                Thread.sleep(1);
            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            if (s.charAt(i) != password.charAt(i)){
                flag = false; 
            }
        }
        return flag;
    }
}