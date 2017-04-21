using System;

namespace tests.password
{
    class PasswordConstantTime {
        private static String password;

        static bool CheckPassword(string s)
        {
    	    if (s.Length != password.Length)
            {
                return false;
    	    }

            int res = 0;
            for (int i = 0; i < s.Length; i++)
            {
                res |= s[i] ^ password[i];
            }

            return res == 0;
        }

        static void Main(string[] args)
        {
    	    if (args.Length != 2)
            {
    		    Console.WriteLine("Please provide two arguments -- a password and a guess");
    		    Environment.Exit(0); 
    	    }

            password = args[0];
            bool res = CheckPassword(args[1]);
            Console.WriteLine(res);
        }
    }
}

