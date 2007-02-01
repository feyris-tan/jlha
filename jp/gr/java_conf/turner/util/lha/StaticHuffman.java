/*
 * $RCSfile: StaticHuffman.java,v $ $Date: 2001/11/16 17:28:01 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.8 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.io.*;

/**
 * �ÓI�n�t�}�������̊�{�N���X.
 *
 * @auther TURNER
 */
class StaticHuffman  implements ItfHuffman
{

	protected static int CODELEN_MAX = 16;
	protected TreeNode treeRoot = null;
	protected Leaf[] leafs;
	private int shortcuts_bits;
	protected TreeNode[] shortcuts;
	protected int table_size;

	
	/**
	 * �n�t�}���R�[�h�����N���X�̃R���X�g���N�^.
	 * 
	 * @param table_size �n�t�}�������̑傫��
	 */
	protected StaticHuffman( int table_size ){
		this.table_size = table_size;
		//leafs = new Leaf[table_size];
		
		//�V���[�g�J�b�g�e�[�u���̑傫�������߂�
		shortcuts_bits = 4;
		while( (1<<shortcuts_bits) < table_size ){
			shortcuts_bits++;
		}
		shortcuts = new TreeNode[1<<shortcuts_bits];
	}

	
	/**
	 * �c���[�����ǂ��ăf�R�[�h����.
	 *
	 * @param cutter �r�b�g�J�b�^�[�i�X�g���[���j
	 */
	public int decode( BitCutter cutter )
		throws IOException
	{
		int copy_code = cutter.copyBits( shortcuts_bits );
		TreeNode currentNode = shortcuts[copy_code];
		
		if( currentNode instanceof Leaf ){
			cutter.skipBits( ((Leaf)currentNode).code_len );
		}
		else{
			cutter.skipBits( shortcuts_bits );
			do{
				if( cutter.getBit() == 1 ){
					currentNode = ((Branch)currentNode).child_1;
				}
				else{
					currentNode = ((Branch)currentNode).child_0;
				}
			}while( !(currentNode instanceof Leaf) );
		}

		return ((Leaf)currentNode).real_code;	 
	}
	
	/**
	 * ���������t�@�C������ǂݍ���.
	 *
	 * @param effective_len_bits �L���������̓ǂݍ��݃r�b�g��
	 * @param special_index      �󔒃C���f�b�N�X�w��
	 * @param cutter             �r�b�g�J�b�^�[�i�X�g���[���j
	 */
	protected void readTableLen( int effective_len_bits, int special_index, BitCutter cutter )
		throws IOException
	{
		int i, c;
		
		int effective_len = cutter.getBits( effective_len_bits );
		

		if( effective_len == 0 ){
			leafs = new Leaf[1];
			leafs[0] = new Leaf();

			c = cutter.getBits( effective_len_bits );
			leafs[0].code_len = 0;
			leafs[0].code = 0;
			leafs[0].real_code = c;
		}
		else{
			leafs = new Leaf[effective_len];
			for( i=0; i < leafs.length; i++ ){
				leafs[i] = new Leaf();
			}

			i = 0;
			while( i < effective_len ){
				c = cutter.getBits(3);
				if( c == 7 ){
					c += cutter.getClusterLen( 1 );
				}
				leafs[i].code_len = c;
				i++;
				if( i == special_index ){
					c = cutter.getBits(2);
					while( --c >= 0 ){
						leafs[i++].code_len = 0;
					}
				}
			}
		}
	}
	

	/**
	 *	���������畄���������.
	 */
	protected void makeTableCode()
		throws IOException
	{
		int i,j;
		/*
		 *	�������̏o�������J�E���g
		 */
		int[] len_count = new int[CODELEN_MAX + 1];
		for( i=0; i < leafs.length; i++ ){
			if( leafs[i].code_len > CODELEN_MAX ){
				throw new LhaException("Invalid Huffman table.");
			}
			len_count[leafs[i].code_len]++;
		}

		/*
		 *	����������n�t�}�������𐶐�
		 *	�n�t�}���R�[�h�����O���Ă��Ƃ͂��̃R�[�h�͊��蓖�Ă��ĂȂ����Ă���
		 *	������i=1���珈�����J�n����B
		 */
		int[] codeStart = new int[len_count.length+1];
		for( i=1; i < (codeStart.length - 1); i++ ){
			codeStart[i+1] = ( codeStart[i] + len_count[i] ) << 1;
		}

		for( i = 1; i < codeStart.length ; i++ ){
			for( j = 0 ; j < leafs.length ; j++ ){
				if( leafs[j].code_len == i ){
					leafs[j].code = codeStart[ i ]++;
					leafs[j].real_code = j; //�{���̃R�[�h
				}
			}
		}
	}

	/**
	 * �����ꂩ��c���[�\�����\�z����.
	 */
	protected void restoreTree()
	{
		int i;

		//�����ꂪ�P��ނ����Ȃ��������̓��ꏈ��
		if( leafs.length == 1 ){
			treeRoot = leafs[0];
			for( i = 0; i < shortcuts.length; i++ ){
				shortcuts[i] = treeRoot;
			}
			return;
		}

		Branch currentNode;
		int mask;

		treeRoot = new Branch(); 
	
		for( i=0; i < leafs.length; i++ ){
			
			//���ꂼ��̃n�t�}���R�[�h���P�r�b�g�����ǂ�Ȃ���c���[��g�ݗ��ĂĂ䂭
			//�c���[�͏�Ƀ��[�g���炽�ǂ�n�߂�
			currentNode = (Branch)treeRoot; 		
			mask = 1 << (leafs[i].code_len - 1);
			for( int j=0; j < leafs[i].code_len; j++ ){

				if( (leafs[i].code & mask) != 0 ){
					//�R�[�h�̃r�b�g���P�������Ƃ��̏����B
					
					//�n�t�}���R�[�h�̍Ō�ɂ��ǂ蒅������c���[�̗t�Ƃ��ēo�^����B
					if( j == (leafs[i].code_len - 1) ){
						currentNode.child_1 = leafs[i];
					}
					else{
						//�������Ƀm�[�h���L��΂��������ǂ��Ă䂭�B
						//�Ȃ���΁A�V�����m�[�h�����B
						if( currentNode.child_1 == null ){
							currentNode.child_1 = new Branch();
							
							//�V���[�g�J�b�g�̔z��ɃZ�b�g
							if( j == (shortcuts_bits-1) ){
							    int index = leafs[i].code >>> (leafs[i].code_len - shortcuts_bits);
								shortcuts[index] = currentNode.child_1;
							}
						}
						currentNode = (Branch)currentNode.child_1;
					}
				}
				else{
					//�R�[�h�̃r�b�g���O�������Ƃ��̏���
					
					//�n�t�}���R�[�h�̍Ō�ɂ��ǂ蒅������c���[�̗t�Ƃ��ēo�^����B
					if( j == (leafs[i].code_len - 1) ){
						currentNode.child_0 = leafs[i];
					}
					else{
						//�������Ƀm�[�h���L��΂��������ǂ��Ă䂭�B
						//�Ȃ���΁A�V�����m�[�h�����B
						if( currentNode.child_0 == null ){
							currentNode.child_0 = new Branch();
							
							//�V���[�g�J�b�g�̔z��ɃZ�b�g
							if( j == (shortcuts_bits-1) ){
							    int index = leafs[i].code >>> (leafs[i].code_len - shortcuts_bits);
								shortcuts[index] = currentNode.child_0;
							}
						}
						currentNode = (Branch)currentNode.child_0;
					}
				}

				//�r�b�g�}�X�N���P�i�߂�
				mask >>>= 1;
			}

			//�V���[�g�J�b�g�̔z��ɃZ�b�g
			if( leafs[i].code_len > 0 && leafs[i].code_len <= shortcuts_bits ){
				int index_mask = ((1 << leafs[i].code_len)-1) << (shortcuts_bits - leafs[i].code_len);
				int index = leafs[i].code << (shortcuts_bits - leafs[i].code_len);
				for( int k = index; (k & index_mask) == index; k++ ){
					shortcuts[k] = leafs[i];
				}
			}
		}
	}

	/**
	 * �n�t�}���e�[�u�����t�@�C������ǂݍ���.
	 *
	 * @param effective_len_bits �L���������̓ǂݍ��݃r�b�g��
	 * @param special_index      �󔒃C���f�b�N�X�w��
	 * @param cutter             �r�b�g�J�b�^�[�i�X�g���[���j
	 */
	protected void loadFrom(  int effective_len_bits, int special_index,
			BitCutter cutter )
		throws IOException
	{
		readTableLen( effective_len_bits, special_index, cutter );
		//�R�[�h���P�����Ȃ��e�[�u���̏ꍇ
		//�n�t�}���R�[�h�̊���U��͂���Ȃ��B
		if( leafs.length > 1 ){
			makeTableCode();
		}
		restoreTree();
	}

}
