package com.inter.trade.exception;

/**
 * @author hzx 2014Äê4ÔÂ21ÈÕ
 * @version V1.0
 */
public class NoSuchCommandException extends Exception
{

	/**
	 * @author hzx
	 */
	private static final long serialVersionUID = 6775989272729970537L;

	public NoSuchCommandException()
	{
		super();
	}

	public NoSuchCommandException(String detailMessage)
	{
		super(detailMessage);
	}

}
