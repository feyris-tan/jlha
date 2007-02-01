/*
 * $RCSfile: StaticHuffmanP.java,v $ $Date: 2000/04/15 17:28:07 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.1.1.1 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;
import java.io.*;

/**
 * �X���C�h������v�ʒu�f�R�[�h�p�ÓI�n�t�}������.
 *
 * @author TURNER
 */
class StaticHuffmanP extends StaticHuffman
{

	/**
	 * �X���C�h������v�ʒu�f�R�[�h�p�ÓI�n�t�}������.
	 *
	 * @param size �n�t�}�������̑傫��
	 */
	protected StaticHuffmanP( int size ){
		super( size );
	}

	/**
	 * �f�R�[�h����.
	 *
	 * @param cutter �f�[�^��ǂݍ��ރr�b�g�J�b�^�[�i�X�g���[���j
	 */
	public int decode( BitCutter cutter )
		throws IOException
	{
		int ret = super.decode(cutter);

		if (ret != 0){
			ret = (1 << (ret - 1)) + cutter.getBits(ret - 1);
		}
		else{
			ret = 0;
		}
		return ret;
	}

}
