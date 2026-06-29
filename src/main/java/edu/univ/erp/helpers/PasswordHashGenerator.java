package edu.univ.erp.helpers;

import edu.univ.erp.security.hash.PasswordHasher;

/**
 * Utility to print bcrypt hashes for given passwords.
 *
 * Usage via Maven:
 * mvn exec:java -Dexec.mainClass=edu.univ.erp.helpers.PasswordHashGenerator -Dexec.args="admin123 inst123 stu1pass stu2pass"
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: PasswordHashGenerator <password1> <password2> ...");
            return;
        }

        for (String password : args) {
            String hash = PasswordHasher.hash(password);
            System.out.println("Password: " + password);
            System.out.println("Hash:     " + hash);
            System.out.println();
        }
    }
}


