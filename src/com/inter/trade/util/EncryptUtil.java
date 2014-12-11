package com.inter.trade.util;

import com.inter.trade.EasyPayApplication;

public class EncryptUtil
{

	/**
	 * 加密
	 * 
	 * @param input
	 * @param key
	 * @return
	 */
	public static String encrypt(String input, int index)
	{
		byte[] crypted = null;
		try
		{
			crypted = CryptionUtil.encrypt(input.getBytes(), index,
					EasyPayApplication.getInstance());
		}
		catch (Exception e)
		{
		}
		return new String(Base64.encode(crypted));
	}

	/**
	 * 解密
	 * 
	 * @param input
	 * @param key
	 * @return
	 */
	public static String decrypt(String input, int index)
	{
		byte[] output = null;
		try
		{
			output = CryptionUtil.decrypt(Base64.decode(input), index,
					EasyPayApplication.getInstance());
			return new String(output, "UTF-8");
		}
		catch (Exception e)
		{

		}
		return "";
	}

	public static int createRandomkeySort()
	{
		int randomNum = (int) (Math.random() * 10);
		return randomNum;
	}
}
