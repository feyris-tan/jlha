/*
 * $RCSfile: LZHEncoder.java,v $ $Date: 2001/11/23 12:25:59 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.2 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.io.IOException;

class LZHEncoder extends LZHSlideDic {

	BitPacker packer;       //�r�b�g�J�b�^�[�i���̓X�g���[������C�Ӄr�b�g�擾�j

	int now_pos = 0;        //�X���C�h�����̏������̐擪�ʒu.
	int match_pos = 0;      //�X���C�h�����̈�v�ʒu
	int match_len = 0;      //�X���C�h�����̈�v��

	ItfWriteHuff	hufC;       //�R�[�h�p�n�t�}������
	ItfWriteHuff	hufP;       //�X���C�h�����|�C���^�p�n�t�}������

	private long count = 0;

	private short[] block = new short[32768/8*7];

	/**
	 * �X���C�h�����f�R�[�_�[�N���X�̃R���X�g���N�^.
	 * 
	 * @param dicbits �����̑傫���i�r�b�g���j
	 * @param packer �r�b�g�J�b�^�[�i�C�ӂ̃r�b�g��؂�o�����̓X�g���[���̃��b�p�[�j
	 */
	protected LZHEncoder( int cmp_method, BitPacker packer )
	{
		super( cmp_method );
		block_size = 0;

		this.packer = packer;
	}

	/**
	 * �ÓI�n�t�}���e�[�u������������.
	 * 
	 * @exception java.io.IOException
	 */
	private void makeTreeAndSaveStaticHuffmanTable()
		throws IOException
	{
		StaticWriteHuffC tmp_hufC = (StaticWriteHuffC)hufC;
		StaticWriteHuffP tmp_hufP = (StaticWriteHuffP)hufP;
		tmp_hufC.makeTreeAndSaveTo( 9, -1, packer);
		tmp_hufP.makeTreeAndSaveTo( huf_p_bits , -1, packer);
	}

	/**
	 * �ÓI�n�t�}���e�[�u��������������.
	 *
	 * @exception java.io.IOException
	 */
	private void initStaticHuffmanTable()
	{
		//�n�t�}���e�[�u���̐���
		hufC = new StaticWriteHuffC( 512 );
		hufP = new StaticWriteHuffP( huf_p_max );
	}

	/**
	 * ���I�n�t�}���e�[�u��������������ilh1�p�j.
	 * 
	 */
	protected void initHuffmanTableForLH1()
	{
		throw new InternalError("this method unsupported now.");
		//hufC = new DynamicHuffmanC( 314 );
		//hufP = new ReadyMadeHuffmanP( huf_p_max );
	}

	/**
	 * �f�[�^��W�J���P�o�C�g���o��.
	 * 
	 * @return ���o�����P�o�C�g�Ԃ�̃f�[�^
	 * @exception java.io.IOException
	 */
	protected void write( int c )
		throws IOException
	{
		count++;
		c &= 0xFF;
		if( block_size == 0 ){
			initStaticHuffmanTable();
		}

		block[block_size] = (short)(0xFF&c);
		hufC.encode( c, packer );


		//LH1�ȊO�̂Ƃ���block_size���X�V����
		//block_size�����t�ɂȂ�����t���b�V������
		if( block_size >= 0 ){
			block_size++;
			if( block_size == block.length ){
				flushBlock();
			}
		}
	}

	protected void flushBlock()
		throws IOException
	{
		if( block_size == 0 )return;

		packer.putBits( block_size >>> 8, 8 );
		packer.putBits( 0xFF&block_size, 8 );
		makeTreeAndSaveStaticHuffmanTable();

		for( int i=0; i<block_size; i++ ){
			hufC.encode( block[i], packer );
			if( block[i] >= 256 ){
				i++;
				hufP.encode( block[i], packer );
			}
		}
		block_size = 0;
	}
	
	protected long getCount(){
		return count;
	}

	private int search( int start ){
		return 0;
	}

}
