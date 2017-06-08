package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;
import java.util.Random;

public class PasswordTwoBranches implements PasswordChecker {
    private static Dictionary d = new Dictionary();
    private Random rnd = new Random();
    public static void main(String[] args) throws IOException {
        d.run(new PasswordTwoBranches());
    }

    public boolean checkPassword(String s, final String password) {
        boolean[] flag = new boolean[2];
        flag[0] = true; 
        flag[1] = true;         
        for (int i = 0; i < 4; i++){
            if (s.charAt(i) != password.charAt(i)){
                flag[0] = false; 
            }
            else{
                flag[1] = false;
            }
        }
        return flag[rnd.nextInt(2)];
    }

}
