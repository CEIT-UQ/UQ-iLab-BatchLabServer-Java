/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.utilities;

import java.security.MessageDigest;
import java.util.Random;

/**
 *
 * @author uqlpayne
 */
public class Password {

    //<editor-fold defaultstate="collapsed" desc="Constants">
    private static final String STR_ClassName = Password.class.getName();
    /*
     * String constants
     */
    private static final String STR_Algorithm = "SHA";
    //</editor-fold>

    public static String CreateRandom() {
        String password = null;

        try {
            /*
             * Generate a random number and hash it
             */
            Random random = new Random();
            Integer number = random.nextInt();
            MessageDigest md = MessageDigest.getInstance(STR_Algorithm);
            byte[] bytes = md.digest(number.toString().getBytes());

            /*
             * Take the last four bytes of the hashed number, convert these to
             * eight lowercase hexadecimal digits and use as the password
             */
            password = "";
            for (int i = bytes.length - 4; i < bytes.length; i++) {
                password += String.format("%02x", bytes[i]);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        return password;
    }

    /**
     * Convert the password to its hashed form which consists of a string of
     * uppercase hexadecimal digits.
     * @param password
     * @return 
     */
    public static String ToHash(String password) {
        String hashedPassword = null;

        try {
            /*
             * Hash the password
             */
            MessageDigest md = MessageDigest.getInstance(STR_Algorithm);
            md.reset();
            byte[] bytes = md.digest(password.getBytes());
            hashedPassword = "";
            for (int i = 0; i < bytes.length; i++) {
                hashedPassword += String.format("%02X", bytes[i]);
            }
        } catch (Exception ex) {
            Logfile.WriteError(ex.toString());
        }

        return hashedPassword;
    }
}
