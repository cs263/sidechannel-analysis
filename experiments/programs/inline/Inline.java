package tests.inline;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;
import java.util.Random;

public class Inline  {
    static SecureRandom rnd = new SecureRandom();
    static int threshold = 90;
    private static StringBuffer buf = new StringBuffer();    
        private static long t;



    public static void main(String[] args) throws IOException {
        int guess;
        for (int i = 0; i < 100000; i++){
            guess = rnd.nextInt(100);
            t = System.nanoTime();
            check(guess);
            t = System.nanoTime() - t;
            System.out.print((guess<threshold) + " " );
            System.out.println(t);

        }
                System.out.println(buf.toString());

    }

    public static int doWork(int a){
        a+=4;
        return a+a;
        

    }

    public static int doWork2(int b){
        b+=4; 
        return b+b;
    }

    public static boolean check(int guess) {
        if (guess < threshold){
            for (int i =0; i < 100000; i++){
                doWork(i);
            }

        }
        else{
            for (int i = 0; i < 100000; i++){
                doWork(i);
            }
        }
        return false;
    }
}

