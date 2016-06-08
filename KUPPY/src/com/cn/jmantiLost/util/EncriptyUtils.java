package com.cn.jmantiLost.util;

import com.cn.jmantiLost.application.AppContext;



public class EncriptyUtils {

	// 转化字符串为十六进制编码 
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}
	
	// 转化十六进制编码为字符串 
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// 
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	// 高低位转换
	public static String zhuanHuan(String str) {
		String s = "";
		s = str.substring(6, 8);
		s = s + str.substring(4, 6);
		s = s + str.substring(2, 4);
		s = s + str.substring(0, 2);
		return s;
	}
	
	public static String zhuanHuan2(String str) {
		String s = "";
		s = str.substring(2, 4);
		s = s + str.substring(0, 2);
		return s;
	}
	
	
	public static String encripty(String plainData,String key){
		String encripty = null ;
		long intPlainData = Long.parseLong(plainData,16);
		long intKey = Long.parseLong(key,16);
		long temp = (intPlainData ^ intKey) ;
		long tempEncripty = temp >> 3 ;
		encripty = Long.toHexString(tempEncripty) ;
		return encripty ;
	}
	
	/*
	 * 
	 * NSData *auData = nil;
    Byte SessoinKey[4]={0};
    SessoinKey[0] = 0xaa;
    SessoinKey[1] = 0xbb;
    SessoinKey[2] = 0xfe;
    //SessoinKey[3] = arc4random()%255;
    SessoinKey[3] = 0x21;
    m_sessoinKey[0] = SessoinKey[1];
    m_sessoinKey[1] = SessoinKey[0];
    
    Byte SecureKey[4]={0};
    SecureKey[0] = 0x72;
    SecureKey[1] = 0x85;
    SecureKey[2] = 0x3a;
    SecureKey[3] = 0xc1;
    
    
    
    NSData *auData = nil;
    Byte SessoinKey[4]={0};
    SessoinKey[0] = 0xaa;
    SessoinKey[1] = 0xbb;
    SessoinKey[2] = 0xfe;
    //SessoinKey[3] = arc4random()%255;
    SessoinKey[3] = 0x21;
    m_sessoinKey[0] = SessoinKey[1];
    m_sessoinKey[1] = SessoinKey[0];
    
    Byte SecureKey[4]={0};
    SecureKey[0] = 0x72;
    SecureKey[1] = 0x85;
    SecureKey[2] = 0x3a;
    SecureKey[3] = 0xc1;
	 * 
	 */
	
	public static String decryption(String data){
		if(AppContext.SESSION_KEY == null){
			return "";
		}
		String convertData = zhuanHuan2(data);
		long intData = Long.parseLong(convertData,16);
		long temp =  (intData << 3) ;
		long tempDecryption = temp ^ Long.parseLong(AppContext.SESSION_KEY,16) ;
		//long tempDecryption = temp ^ Long.parseLong(Constant.SESSION_KEY,16) ;
		String ret = Long.toHexString(tempDecryption);
		return ret ;
	}
	
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	} 
	
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}  
	
	/** 
	 * Convert char to byte 
	 * @param c char 
	 * @return byte 
	 */  
	 private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	}  
	
}
