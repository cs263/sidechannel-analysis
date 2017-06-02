package experiments.programs.password.java;

public class PasswordConstantTime {

    private static String password;

    public static boolean checkPassword(String s){
    	if (s.length() != password.length()){
            return false;
    	}
        int res = 0;
        for (int i = 0; i < s.length(); i++){
        	try {
                Thread.sleep(1000);
            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            res |= s.charAt(i) ^ password.charAt(i);
        }
        return res == 0;
    }

    public static void main(String[] args){
    	if (args.length != 2){
    		System.out.println("Please provide two arguments -- a password and a guess");
    		System.exit(0); 
    	}
        password = args[0];
        boolean res = checkPassword(args[1]);
        System.out.println(res);
    }
}