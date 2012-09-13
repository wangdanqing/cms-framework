package net.pusuo.cms.client.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Encoding strings by MD5.
 * 
 * @author luciali
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StrMD5 {
	/**
	 * Encoding a string by MD5.
	 * 
	 * @param key The string need to be encoded.
	 * @return The resultant string.
	 */
	public static String getStringMD5(String key) {
		String value = null;
		MessageDigest currentAlgorithm;
		try {
			currentAlgorithm = MessageDigest.getInstance("MD5");
			currentAlgorithm.reset();
			currentAlgorithm.update(key.getBytes());
			byte[] hash = currentAlgorithm.digest();
			String d = "";
			int usbyte = 0; // unsigned byte
			for (int i = 0; i < hash.length; i += 2) { // format with 2-byte
				// words with spaces.
				usbyte = hash[i] & 0xFF; // byte-wise AND converts signed byte
				// to unsigned.
				if (usbyte < 16)
					d += "0" + Integer.toHexString(usbyte); // pad on left if
				// single hex digit.
				else
					d += Integer.toHexString(usbyte);
				usbyte = hash[i + 1] & 0xFF; // byte-wise AND converts signed
				// byte to unsigned.
				if (usbyte < 16)
					d += "0" + Integer.toHexString(usbyte);//+ " "; // pad on
				// left if single hex
				// digit.
				else
					d += Integer.toHexString(usbyte);// + " ";
			}
			//return d.toUpperCase();
			value = d.trim().toLowerCase();

		} catch (NoSuchAlgorithmException e) {
			System.out.println("MD5 algorithm not available.");
		}
		return value;
	}
};