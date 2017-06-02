package experiments.programs.password.java;

public class PossiblyInsecure {

    private static String password;

    public static boolean checkPassword(String s){
        boolean flag = true; 
    	if (s.length() != password.length()){
            return false;
    	}
        for (int i = 0; i < s.length(); i++){
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            if (s.charAt(i) != password.charAt(i)){
                flag = false; 
            }
        }
        return flag;
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