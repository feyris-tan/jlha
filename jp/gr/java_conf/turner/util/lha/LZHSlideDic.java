/*
 * $RCSfile: LZHSlideDic.java,v $ $Date: 2001/11/23 09:51:27 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.1 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.util.*;

abstract class LZHSlideDic {
	static final int THRESHOLD = 3; //��v���̍ŏ��T�C�Y

	byte[] dic;     //�X���C�h����
	int dic_mask;   //�X���C�h�����C���f�b�N�X�̃��b�v�A���E���h�p�}�X�N
	
	int block_size = 0;     //�u���b�N�T�C�Y�iLH4�`��LH7�Ŏg�p�j
	int huf_p_bits = 0;     //�X���C�h�����|�C���^�p�n�t�}�������A�L���T�C�Y�ǂݍ��݃r�b�g��
	int huf_p_max = 0;      //�X���C�h�����|�C���^�p�n�t�}�������A�ő�T�C�Y

	int cmp_method;         //���k���\�b�h


	/**
	 * �X���C�h�����f�R�[�_�[�N���X�̃R���X�g���N�^.
	 * 
	 * @param cmp_method �����̑傫���i�r�b�g���j
	 */
	protected LZHSlideDic( int cmp_method )
	{
		int dicbits;
		this.cmp_method = cmp_method;
		switch( cmp_method ){
		case LhaInputStream.CMP_TYPE_LH1:
			dicbits = 12;		//�S�j�o�C�g
			//LH1�ł̓n�t�}���e�[�u�����t�@�C������ǂݍ��܂Ȃ��̂�
			//huf_p_bits�̒l�͎g�p���Ȃ�.
			huf_p_max = 1 << (12 - 6);
			initHuffmanTableForLH1();
			block_size = -1;//lh1�ɂ̓u���b�N���Ȃ��̂Ŗ�����\��-1
			break;
		case LhaInputStream.CMP_TYPE_LH4:
			dicbits = 12;		//�S�j�o�C�g
			huf_p_bits = 4;
			huf_p_max = 14;
			block_size = 0;
			break;
		case LhaInputStream.CMP_TYPE_LH5:
			dicbits = 13;		//�W�j�o�C�g
			huf_p_bits = 4;
			huf_p_max = 14;
			block_size = 0;
			break;
		case LhaInputStream.CMP_TYPE_LH6:
/*
*			UNLHA32.DLL�̃w���v�ɂ���
*			LH6�̃w�b�_��LH7�ň��k���ꂽ�t�@�C�������݂���炵��.
*
*			dicbits = 15;		//�R�Q�j�o�C�g
*			huf_p_bits = 5;
*			huf_p_max = 16;
*			block_size = 0;
*			break;
*/
		case LhaInputStream.CMP_TYPE_LH7:
			dicbits = 16;		//�U�S�j�o�C�g
			huf_p_bits = 5;
			huf_p_max = 32;
			block_size = 0;
			break;
		default:
			throw new Error("internal Error Illegal method:"+cmp_method);
		}

		int dicsize = 1 << dicbits;
		this.dic = new byte[dicsize];
		dic_mask = dicsize - 1;
		Arrays.fill( dic, (byte)' ' );
	}

	static protected String toHex( long n, int len ){
		String hex = "0000000000000000"+Long.toHexString(n);
		hex = hex.toUpperCase();
		while( Math.pow( 16 , len ) <= n ){
			len++;
		}
		return hex.substring( hex.length() - len );
	}

	abstract protected void initHuffmanTableForLH1();
}
