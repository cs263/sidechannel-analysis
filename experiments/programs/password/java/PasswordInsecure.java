package tests.password;

public class PasswordInsecure {

    private static String password;

    public static boolean checkPassword(String s){
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

    public static void main(String[] args) {
    	if (args.length != 2) {
    		System.out.println("Please provide two arguments -- a password and a guess");
    		System.exit(0); 
    	}
        password = args[0];
        boolean res = checkPassword(args[1]);
        System.out.println(res);
    }
}