package net.yoomai.otp;

import sun.security.provider.MD5;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;

import static javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA1;

/**
 * OTP算法实现
 * 参考代码
 * <a href="http://my.oschina.net/u/265943/blog/117599">http://my.oschina.net/u/265943/blog/117599</a>
 * <a href="https://github.com/coffeefoam/paycode/blob/master/paycode.go">https://github.com/coffeefoam/paycode/blob/master/paycode.go </a>
 *
 * @author Ray & coffeefoam@126.com & http://github.com/coffeefoam
 * @(#)Supplier.java 1.0 08/07/2016
 */
public class OtpCore {
    private final static String NUM_CHAR = "0123456789";
    private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    /**
     * 每60秒生成1个8位动态密码
     *
     * @param seed
     * @return
     */
    public String getTOTP(String seed) {
        long T0 = 0;
        long X = 60;
        Calendar cal = Calendar.getInstance();
        long time = cal.getTimeInMillis() / 1000;
        String steps = "0";
        try {
            long T = (time - T0) / X;
            steps = Long.toHexString(T).toUpperCase();
            while (steps.length() < 16)
                steps = "0" + steps;
            return generateTOTP(seed, steps, "4");
        } catch (final Exception e) {
            System.out.println("Error : " + e);
            return "生成动态口令失败";
        }
    }


    /**
     * 获取一个随机种子
     *
     * @param digitNumber
     * @return
     */
    public String getRandomSeed(int digitNumber) {
        long seed = System.currentTimeMillis();
        StringBuffer buffer = new StringBuffer();
        Random random = new Random(seed);

        for (int i = 0; i < digitNumber; i++) {
            buffer.append(NUM_CHAR.charAt(random.nextInt(NUM_CHAR.length())));
        }

        return buffer.toString();
    }

    /* ------------------------------- */
    /*        Private Methods          */
    /* --------------------------------*/

    private String generateTOTP(String key, String time, String returnDigits) throws InvalidKeyException, NoSuchAlgorithmException {
        int codeDigits = Integer.decode(returnDigits).intValue();
        String result = null;
        // Using the counter
        // First 8 bytes are for the movingFactor
        // Compliant with base RFC 4226 (HOTP)
        while (time.length() < 16)
            time = "0" + time;
        // Get the HEX in a Byte[]
        byte[] msg = hexString2Byte(time);
        byte[] k = hexString2Byte(key);

        byte[] hash = hmacSHA(k, msg);
        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;
        int binary = ((hash[offset] & 0x7f) << 24)
                | ((hash[offset + 1] & 0xff) << 16)
                | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
        System.out.println(offset);
        int otp = binary % DIGITS_POWER[codeDigits];
                result = Integer.toString(otp);
        while (result.length() < codeDigits) {
            result = "0" + result;
        }
        return result;
    }


    /**
     * hmac_sha的加密
     *
     * @param key
     * @param value
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private byte[] hmacSHA(byte[] key, byte[] value) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "RAW");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKeySpec);

        return mac.doFinal(value);
    }

    /**
     * 十六进制字符串转换成字节数组
     *
     * @param source
     * @return
     */
    private byte[] hexString2Byte(String source) {
        source = source.trim().replace(" ", "").toUpperCase();
        int m = 0, n = 0;
        int len = source.length() / 2;

        byte[] ret = new byte[len];

        for (int i = 0; i < len; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = (byte) (Integer.decode("0x" + source.substring(i * 2, m) + source.substring(m, n)) & 0xFF);
        }

        return ret;
    }
}
