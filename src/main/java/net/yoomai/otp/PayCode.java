package net.yoomai.otp;

/**
 * @author Ray & coffeefoam@126.com & http://github.com/coffeefoam
 * @(#)Supplier.java 1.0 08/07/2016
 */
public class PayCode {
    private final static int FACTORY = 5;

    public String generatePayCode(long uid, String code) {
        int x = Integer.valueOf(code);
        long y =  uid / x + uid * FACTORY;

        int z = (int) (uid % x);

        String paycode = String.format("%04d%09d%03d", x, y, z);
        return "28" + paycode;
    }
}
