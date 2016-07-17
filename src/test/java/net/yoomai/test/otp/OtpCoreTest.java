package net.yoomai.test.otp;

import net.yoomai.otp.OtpCore;
import net.yoomai.otp.PayCode;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ray & coffeefoam@126.com & http://github.com/coffeefoam
 * @(#)Supplier.java 1.0 08/07/2016
 */
public class OtpCoreTest {

    private OtpCore otpCore;
    private PayCode payCode;

    @Before
    public void init() {
        otpCore = new OtpCore();
        payCode = new PayCode();
    }

    @Test
    public void testGenerateOtpPasscode() {
        String seed = otpCore.getRandomSeed(8);
        System.out.println("生成的种子是 " + seed);

        String code = otpCore.getTOTP(seed);
        System.out.println("生成的密码是 " + code);

        String paycode = payCode.generatePayCode(10203405609L, code);
        System.out.println("生成的付款码是 " + paycode);
    }
}
