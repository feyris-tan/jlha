/*
 * $RCSfile: LhaException.java,v $ $Date: 2001/03/30 16:58:59 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.2 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.io.IOException;


/**
 * LHA �W�J�Ŕ��������O�N���X.
 *
 */
public class LhaException extends IOException 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LhaException() {
		super();
	}
	public LhaException(String s) {
		super(s);
	}
}
