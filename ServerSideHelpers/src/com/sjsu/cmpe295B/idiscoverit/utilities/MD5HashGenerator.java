package com.sjsu.cmpe295B.idiscoverit.utilities;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
public class MD5HashGenerator{

	private static Logger logger = Logger.getLogger(MD5HashGenerator.class);
	private static MessageDigest mDigest;
	
	private static String generateMD5(String password){
		logger.info("----------Inside generateMD5 of MD5HashGenerator----------");
		String md5pwd = null;
		try {
			
			mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(password.getBytes("UTF-8"),0,password.length());
			md5pwd = new BigInteger(1,mDigest.digest()).toString(16);
			
			while(md5pwd.length()<32){
				md5pwd="0"+md5pwd;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return md5pwd;
	}
	public static String getMD5(String password){
		logger.info("----------Inside getMD5 of MD5HashGenerator----------");
		String generatedHashedPwd = generateMD5(password);
		return generatedHashedPwd;
	}
	
}
