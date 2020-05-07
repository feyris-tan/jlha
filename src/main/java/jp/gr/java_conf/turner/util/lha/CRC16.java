/*
 * $RCSfile: CRC16.java,v $ $Date: 2001/11/16 17:54:49 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.4 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

/**
 * CRC16�v�Z�p�N���X.
 * ���̃N���X�ł�char��unsigned short �̑���ɗp���Ă���.
 * 
 * @author		TURNER
 */
public class CRC16{

	static char[] crctable;

	private int crc = 0;
	private static final int CRCPOLY = 0xA001;
	private static final int BYTE_BITS = 8;
	private static final int BYTE_MAX = (1<<BYTE_BITS)-1;

	/**
	 * CRC16�I�u�W�F�N�g�𐶐����܂�.
	 */
	public CRC16() {
		if( crctable == null ){
			init();
		}
	}
   

	/**
	 * CRC16�̒l���P�o�C�g�̈����ōX�V���܂�.
	 * @param b CRC16���v�Z����f�[�^
	 */
	public void update(byte b) {
		crc = crctable[(crc ^ (b)) & 0xFF] ^ (crc >> BYTE_BITS);
	}

	/**
	 * CRC16�̒l���o�C�g�̔z��ōX�V���܂�.
	 * 
	 * @param b CRC16���v�Z����f�[�^�̔z��
	 * @param off �f�[�^�̊J�n�ʒu�������z���̃C���f�b�N�X
	 * @param len ���ۂɌv�Z����f�[�^�̃o�C�g��
	 */
	public void update(byte[] b, int off, int len) {
		int end = (off+len);
		for( int i = off; i < end; i++ ){
			crc = crctable[(crc ^ (b[i])) & 0xFF] ^ (crc >> BYTE_BITS);
		}
	}

	/**
	 * CRC�̒l��byte�̔z��ōX�V���܂�.
	 * 
	 * @param b CRC16���v�Z����byte�̔z��
	 */
	public void update(byte[] b) {
		update( b, 0, b.length);
	}

	/**
	 * CRC16�̒l���O�Ƀ��Z�b�g���܂�.
	 */
	public void reset() {
		crc = 0;
	}

	/**
	 * CRC16�̒l���擾���܂�.
	 */
	public int getValue() {
		return crc & 0x0000ffff;
	}


	/*
	 * create table.
	 */
	private void init()
	{
		int    i, j, r;
		char[] tmp = new char[BYTE_MAX+1];

		for (i = 0; i <= BYTE_MAX; i++) {
			r = i;
			for (j = 0; j < 8; j++){
				if ((r & 1) != 0){
					r = (r >> 1) ^ CRCPOLY;
				}
				else{
					r >>= 1;
				}
			}
			tmp[i] = (char)r;
		}
		crctable = tmp;
	}

}
