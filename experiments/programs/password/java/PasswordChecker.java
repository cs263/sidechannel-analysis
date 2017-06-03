package tests.password;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import java.io.*;

public interface PasswordChecker {
    public boolean checkPassword(String s, final String password);
}