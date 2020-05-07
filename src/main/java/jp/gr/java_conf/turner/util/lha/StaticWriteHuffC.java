/*
 * $RCSfile: StaticWriteHuffC.java,v $ $Date: 2001/11/21 17:08:58 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.5 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.io.*;


/**
 * �R�[�h�p�ÓI�n�t�}�������������ݗp.
 * 
 * @author TURNER
 */
class StaticWriteHuffC extends StaticWriteHuff
{
	/**
	 * �R�[�h�p�ÓI�n�t�}�������̃R���X�g���N�^.
	 * 
	 * @param table_size �n�t�}�������̑傫���B
	 */
	protected StaticWriteHuffC( int table_size )
	{
		super( table_size );
	}

//	/**
//	 * �W�v�������Ƀn�t�}���c���[���\�z���t�@�C���ɏ�������.
//	 *
//	 * @param effective_len_bits �L���������̓ǂݍ��݃r�b�g��
//	 * @param writeHuf           �󔒃C���f�b�N�X�w��
//	 * @param packer             �r�b�g�p�b�J�[�i�X�g���[���j
//	 *
//	 * @param IOException IO�G���[���N�������Ƃ�
//	 */
//	protected void makeTreeAndSaveTo(  int effective_len_bits, int special_index,
//			BitPacker packer )
//		throws IOException
//	{
//		Leaf[] sort = makeProvisionalTree();
//		makeCodeLen( sort );
//
//		//�R�[�h���P�����Ȃ��e�[�u���̏ꍇ
//		//�n�t�}���R�[�h�̊���U��͂���Ȃ��B
//		if( leafs.length > 1 ){
//			makeTableCode();
//		}
//		restoreTree();
//
//
//	//	encodeTable = new Leaf[table_size];
//	//	for( int i=0; i < leafs.length; i++ ){
//	//		encodeTable[leafs[i].real_code] = leafs[i];
//	//	}
//
//		setEncodeMode();//** ����ȍ~�̓G���R�[�h���[�h
//	}

	/**
	 * �n�t�}���R�[�h�̃r�b�g������������.
	 * 
	 * @param effective_len_bits �n�t�}�������̑傫����ǂݍ��ނ��߂̃r�b�g��
	 * @param special_index      ���̊֐��ł͕s�g�p
	 * @param cutter             �ǂݍ��݂Ɏg�p����r�b�g�J�b�^�[�i�X�g���[���j
	 */
	protected void writeTableLen( int effective_len_bits, int special_index,
			BitPacker packer )
		throws IOException
	{
		if( leafs.length == 0 ){
			throw new InternalError( "no freq data." );
		}

		int NT = 16+3;
		int TBIT = 5;		/* smallest integer such that (1 << TBIT) > * NT */
		StaticWriteHuff writeHuf = new StaticWriteHuff(NT);

		if( leafs.length == 1 ){
			writeHuf.encode( 0, null );
			writeHuf.makeTreeAndSaveTo( TBIT, 3, packer );
			super.writeTableLen( effective_len_bits, 0, packer );
		}else{

			/* �n�t�}���e�[�u���̏������̂��߂ɕp�x�𐔂��� */
			OutputStream dmy = new DmyOutputStream();
			writeTableLenSub( effective_len_bits, writeHuf, new BitPacker( dmy ) );

			/* �������ޖ{�� */
			writeHuf.makeTreeAndSaveTo( TBIT, 3, packer );
			writeTableLenSub( effective_len_bits, writeHuf, packer );
		}
	}

	/**
	 * �n�t�}���R�[�h�̃r�b�g������������.
	 * 
	 * @param effective_len_bits �n�t�}�������̑傫����ǂݍ��ނ��߂̃r�b�g��
	 * @param writehuff          ���̃n�t�}��������ǂݍ��ނ̂Ɏg�p����n�t�}������
	 * @param cutter             �ǂݍ��݂Ɏg�p����r�b�g�J�b�^�[�i�X�g���[���j
	 */
	private void writeTableLenSub( int effective_len_bits, StaticWriteHuff writehuf,
			BitPacker packer )
		throws IOException
	{
		packer.putBits( leafs.length, effective_len_bits );

		int i = 0;
		while( i < leafs.length ){

			int code_len = leafs[i++].code_len;
			if( code_len == 0 ){

				/* �R�[�h���󔒂O�̎��̏��� */

				/* �󔒂̘A�����鐔�𐔂��� */
				int zero_cnt = 1;
				while( i < leafs.length && leafs[i].code_len == 0 ){
					i++;
					zero_cnt++;
				}

				if( zero_cnt <= 2 ){

					/* �󔒂��Q�܂ł̂Ƃ� */
					for( int j = 0; j < zero_cnt; j++ ){
						writehuf.encode( 0, packer );
					}
				}else if ( zero_cnt <= 18 /* 2^4+3 */ ){

					/* �󔒂�18�܂ł̂Ƃ� */
					writehuf.encode( 1, packer );
					packer.putBits( zero_cnt - 3, 4 );
				}else if ( zero_cnt == 19 ){

					/* �󔒂�19�̂Ƃ��͂P�̂Ƃ��̏����{18�܂ł̏��� */
					writehuf.encode( 0, packer );

					writehuf.encode( 1, packer );
					packer.putBits( 15 /* 18 - 3 */, 4 );
				}else{

					/* �󔒂�20�ȏ�̂Ƃ��̏��� */
					writehuf.encode( 2, packer );
					packer.putBits( zero_cnt - 20, effective_len_bits );
				}
			}else{

				/* �󔒈ȊO�̗L���R�[�h�̎��̏��� */
				writehuf.encode( code_len + 2, packer );
			}
		}
	}
}

/**
 * �_�~�[�o�̓X�g���[��.
 * 
 * @author TURNER
 */
class DmyOutputStream extends OutputStream{
	public void write( int c ){};
}
