/*
 * $RCSfile: StaticWriteHuffP.java,v $ $Date: 2001/11/19 12:56:35 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.2 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;
import java.io.*;

/**
 * �X���C�h������v�ʒu�G���R�[�h�p�ÓI�n�t�}������.
 *
 * @author TURNER
 */
class StaticWriteHuffP extends StaticWriteHuff
{
	int codelen_max;

	/**
	 * �X���C�h������v�ʒu�G���R�[�h�p�ÓI�n�t�}�������R���X�g���N�^.
	 *
	 * @param size �n�t�}�������̑傫��
	 */
	protected StaticWriteHuffP( int size ){
		super( size );
	}

	/**
	 * �G���R�[�h����.
	 *
	 * @param cutter �f�[�^��ǂݍ��ރr�b�g�J�b�^�[�i�X�g���[���j
	 */
	public void encode( int code, BitPacker packer )
		throws IOException
	{
		int len = getCodeLen( code );
		//* �G���R�[�h���ď����o��
		super.encode( len, packer );
		if( encodeModeFlg && len > 1 ){
			packer.putBits( code & ((1 << (len-1)) - 1), (len-1) );
		}
	}

	/**
	 * �L���R�[�h���𐔂���֐�.
	 * �ő�30�r�b�g�܂�.
	 *
	 * @param code �R�[�h���𐔂�����ۂ̃R�[�h
	 *
	 * @return �L���R�[�h��
	 */
	private int getCodeLen( int code ){
		//* code�̗L���r�b�g���𐔂���
		int len;
		for( len = 0; len <= 30; len++ ){
			if( (1 << len) > code ) break;
		}
		return len;
	}

}
