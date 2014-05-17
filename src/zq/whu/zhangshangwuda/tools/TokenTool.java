package zq.whu.zhangshangwuda.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TokenTool {
	private long timestamp;
	
	private String token;
	
	private String VAR = "account";
	
	public TokenTool() {
		timestamp = System.currentTimeMillis()/1000;
		token = SHA1(VAR + timestamp);
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public String getToken() {
		return token;
	}
	
	private static String SHA1(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
  
    private static String toHexString(byte[] keyData) {
        if (keyData == null) {
            return null;
        }
        int expectedStringLen = keyData.length * 2;
        StringBuilder sb = new StringBuilder(expectedStringLen);
        for (int i = 0; i < keyData.length; i++) {
            String hexStr = Integer.toString(keyData[i] & 0x00FF,16);
            if (hexStr.length() == 1) {
                hexStr = "0" + hexStr;
            }
            sb.append(hexStr);
        }
        return sb.toString();
    }
}
