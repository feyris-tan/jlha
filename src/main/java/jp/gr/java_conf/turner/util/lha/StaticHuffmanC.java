/*
 * $RCSfile: StaticHuffmanC.java,v $ $Date: 2001/11/21 17:09:00 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.5 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.io.*;


/**
 * �R�[�h�p�ÓI�n�t�}������.
 * 
 * @author TURNER
 */
class StaticHuffmanC extends StaticHuffman
{
	/**
	 * �R�[�h�p�ÓI�n�t�}�������̃R���X�g���N�^.
	 * 
	 * @param table_size �n�t�}�������̑傫���B
	 */
	protected StaticHuffmanC( int table_size )
	{
		super( table_size );
	}

//	/**
//	 * �R�[�h�p�ÓI�n�t�}�������̃R���X�g���N�^���t�@�C������ǂݍ���.
//	 * 
//	 * @param effective_len_bits �n�t�}�������̑傫��ǂݍ��ރr�b�g��
//	 * @param huffman   ���̃n�t�}��������ǂݍ��ނ̂Ɏg�p����n�t�}������
//	 * @param cutter    �ǂݍ��݂Ɏg�p����r�b�g�J�b�^�[�i�X�g���[���j
//	 */
//	protected void loadFrom( int effective_len_bits, int special_index,
//			BitCutter cutter )
//		throws IOException
//	{
///		readTableLen( effective_len_bits, special_index, cutter );
//
//		//�R�[�h���P�����Ȃ��e�[�u���̏ꍇ
//		//���̂Ƃ��A�n�t�}���R�[�h�̊���U��͂���Ȃ��B
//		if( leafs.length > 1 ){
//			makeTableCode();
//		}
//		restoreTree();
//	}

/*
	protected void loadFrom(  int effective_len_bits, int special_index,
			BitCutter cutter )
		throws IOException
	{
		throw new InternalError("Not use this method in this class.");
	}
*/

	/**
	 * �n�t�}���R�[�h�̃r�b�g����ǂݍ���.
	 * 
	 * @param effective_len_bits �n�t�}�������̑傫����ǂݍ��ނ��߂̃r�b�g��
	 * @param huffman    ���̃n�t�}��������ǂݍ��ނ̂Ɏg�p����n�t�}������
	 * @param cutter     �ǂݍ��݂Ɏg�p����r�b�g�J�b�^�[�i�X�g���[���j
	 */
	protected void readTableLen( int effective_len_bits, int special_index,
			BitCutter cutter )
		throws IOException
	{
		int TBIT = 5;		/* smallest integer such that (1 << TBIT) > * NT */
		int NT = 16+3;
		StaticHuffman huffman = new StaticHuffman(NT);
		huffman.loadFrom( TBIT, 3, cutter );

		int i, c;
		int effective_len = cutter.copyBits( effective_len_bits );

		if( effective_len == 0 ){
			super.readTableLen( effective_len_bits, 0, cutter );
		}
		else{
			cutter.skipBits( effective_len_bits );	

			leafs = new Leaf[effective_len];
			for( i=0; i < leafs.length; i++ ){
				leafs[i] = new Leaf();
			}

			i = 0;
			while( i < effective_len ){
				c = huffman.decode( cutter );
				if( c <= 2 ){
					switch( c ){
					case 0:
						c = 1;
						break;
					case 1:
						c = cutter.getBits(4) + 3;
						break;
					case 2:
						c = cutter.getBits( effective_len_bits ) + 20;
					}
					while( --c >= 0 ){
						leafs[i++].code_len = 0;
					}
				}
				else{
					leafs[i++].code_len = c - 2;
				}
			}
		}
	}
}
