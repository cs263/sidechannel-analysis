package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;

public class PasswordPossiblyNoLengthCheck implements PasswordChecker {
    private static Dictionary d = new Dictionary();

    public static void main(String[] args) throws IOException {
        d.run(new PasswordPossiblyNoLengthCheck());
    }

    public boolean checkPassword(String s, final String password){
        boolean flag = true; 
        int min = Math.min(s.length(), password.length());
        if (min > 4){
        min = 4;
    }
        for (int i = 0; i < min; i++){
            /*try {
                Thread.sleep(1);
            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }*/
            if (s.charAt(i) != password.charAt(i)){
                flag = false; 
            }
        }
        return flag;
    }
}