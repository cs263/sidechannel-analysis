package experiments.programs.binary.java;

public class BinarySearch {

    private static Integer pin = 1234;

    public static boolean check(Integer guess){
    	boolean flag = true; 
    	if (guess < pin){
    		try {
                Thread.sleep(1000);
            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            flag = false;
    	}
    	else if (guess > pin){
    		flag = false; 
    	}
    	return flag; 
    }

    public static void main(String[] args){
    	if (args.length != 1){
    		System.out.println("Please provide one argument -- a guess of the secret pin");
    		System.exit(0); 
    	}
    
        boolean res = check(Integer.parseInt(args[0]));
        System.out.println(res);
    }
}