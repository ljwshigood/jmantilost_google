package com.cn.jmantiLost.util;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class NumberUtils {

		// 把时间戳（秒），转换为日期格式：2014-0-04-16 13:25
		public static String timeStamp2format(long time_stamp){		
		
			 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 String reTime = df.format(time_stamp * 1000L);
			
			return reTime;
		}
		
				
		// byte[] 高低位转换
		public static byte[] byteArrayReverse(byte[] bs) {
			for (int i = 0; i < bs.length / 2; i++) {
				byte temp = bs[i];
				bs[i] = bs[bs.length - 1 - i];
				bs[bs.length - 1 - i] = temp;
			}
			return bs;
		}

		// 2进制的byte[]数组转int类型
		public static int byteToInt(byte[] b) {  
			  
	        int mask = 0xff;  
	        int temp = 0;  
	        int n = 0;  
	        for(int i = 0; i < b.length; i++){  
	           n <<= 8;  
	           temp = b[i] & mask;  
	           n |= temp;  
	       }  
	        return n;  
		} 
			
		// 2进制的byte[]高低位置换数组转int类型
		public static int byteReverseToInt(byte[] b) {  
			
			int mask = 0xff;  
			int temp = 0;  
			int n = 0;  
			for(int i = b.length - 1; i > -1; i--){  
				n <<= 8;
				temp = b[i] & mask;  
				n |= temp;  
			}  
			return n;  
		}

		// int 类型转byte[] 2进制数组
		public static byte[] intToByteArray(int i) {
			byte[] result = new byte[4];
			result[0] = (byte) ((i >> 24) & 0xFF);
			result[1] = (byte) ((i >> 16) & 0xFF);
			result[2] = (byte) ((i >> 8) & 0xFF);
			result[3] = (byte) (i & 0xFF);
			return result;
		}

		// int 类型转byte[] 2进制数组
		public static String bytes2HexString(byte[] b) {
			String ret = "";
			for (int i = 0; i < b.length; i++) {
				String hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				ret += hex.toUpperCase();
			}
			return ret;
		}
		

	    // 2进制字符串传化为16进制字符串： "01111111" -->  "7F"
	    public static String binaryString2hexString(String bString)  
	    {  
	        if (bString == null || bString.equals("") || bString.length() % 8 != 0)  
	            return null;  
	        StringBuffer tmp = new StringBuffer();  
	        int iTmp = 0;  
	        for (int i = 0; i < bString.length(); i += 4)  
	        {  
	            iTmp = 0;  
	            for (int j = 0; j < 4; j++)  
	            {  
	                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);  
	            }  
	            tmp.append(Integer.toHexString(iTmp).toUpperCase());  
	        }  
	        return tmp.toString();  
	    }  
	    
	    // 16进制字符串传化为2进制字符串  : "FF" -->  "01111111"
	    public static String hexString2binaryString(String hexString)  
	    {  
	        if (hexString == null || hexString.length() % 2 != 0)  
	            return null;  
	        String bString = "", tmp;  
	        for (int i = 0; i < hexString.length(); i++)  
	        {  
	            tmp = "0000"  
	                    + Integer.toBinaryString(Integer.parseInt(hexString  
	                            .substring(i, i + 1), 16));  
	            bString += tmp.substring(tmp.length() - 4);  
	        }  
	        return bString;  
	    } 
	    
	    
	    
	    
	    // 二进制字符串转换成2进制数组 byte[]: "0111111" -->byte[]再换int类型是：127
	    public static byte[] binaryStr2Bytes(String binaryByteString){ 
	        //假设binaryByte 是01，10，011，00以，分隔的格式的字符串 
	        String[] binaryStr = binaryByteString.split(","); 
	        byte[] byteArray = new byte[binaryStr.length]; 
	        for(int i=0;i<byteArray.length;i++){ 
	            byteArray[i] = (byte)parse(binaryStr[i]); 
	        } 
	        return byteArray; 
	    } 
	    
	    public static int parse(String str){ 
	        //32位 为负数 
	        if(32==str.length()){ 
	            str="-"+str.substring(1); 
	            return -(Integer.parseInt(str, 2)+Integer.MAX_VALUE+1); 
	        } 
	        return Integer.parseInt(str, 2); 
	    }
	        
	    
	    private static String hexStr =  "0123456789ABCDEF";  
	    private static String[] binaryArray =   
	        {"0000","0001","0010","0011",  
	        "0100","0101","0110","0111",  
	        "1000","1001","1010","1011",  
	        "1100","1101","1110","1111"}; 
	    /** 
	     *  
	     * @param str 
	     * @return 2进制数组byte[]转换为二进制字符串 
	     * new byte[]{0b01111111}-->"01111111" ;  new byte[]{0x1F}-->"00011111" 
	     */  
	    public static String bytes2BinaryStr(byte[] bArray){  
	          
	        String outStr = "";  
	        int pos = 0;  
	        for(byte b:bArray){  
	            //高四位  
	            pos = (b&0xF0)>>4;  
	            outStr+= binaryArray[pos];  
	            //低四位  
	            pos=b&0x0F;  
	            outStr+= binaryArray[pos];  
	        }  
	        return outStr;  
	          
	    }  
	    /** 
	     *  
	     * @param bytes 
	     * @return 将二进制转换为十六进制字符输出 
	     *  new byte[]{0b01111111}-->"7F" ;  new byte[]{0x2F}-->"2F"
	     */  
	    public static String binaryToHexString(byte[] bytes){  
	          
	        String result = "";  
	        String hex = "";  
	        for(int i=0;i<bytes.length;i++){  
	            //字节高4位  
	            hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));  
	            //字节低4位  
	            hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));  
	            result +=hex+" ";  
	        }  
	        return result;  
	    }  
	    /** 
	     *  
	     * @param hexString 
	     * @return 将十六进制转换为字节数组 
	     * "1F"-->0b01111111 或 0x1F
	     */  
	    public static byte[] hexStringToBinary(String hexString){  
	        //hexString的长度对2取整，作为bytes的长度  
	        int len = hexString.length()/2;  
	        byte[] bytes = new byte[len];  
	        byte high = 0;//字节高四位  
	        byte low = 0;//字节低四位  
	  
	        for(int i=0;i<len;i++){  
	             //右移四位得到高位  
	             high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);  
	             low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));  
	             bytes[i] = (byte) (high|low);//高地位做或运算  
	        }  
	        return bytes;  
	    }  
}
