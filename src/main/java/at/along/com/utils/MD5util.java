package at.along.com.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5util {
    public static String getMd5(String plainTest) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] input = plainTest.getBytes();
            md.update(input);
            byte[] b = md.digest();

            int i;

            StringBuffer sb = new StringBuffer();
            for (int k = 0; k < b.length; k++) {
                i = b[k];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }
            //32位加密
            return sb.toString();
            //16位加密
            //return sb.toString().substring(8, 24);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
