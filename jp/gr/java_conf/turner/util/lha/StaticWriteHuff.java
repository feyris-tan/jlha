/*
 * $RCSfile: StaticWriteHuff.java,v $ $Date: 2001/11/21 17:08:59 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.8 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.io.*;

/**
 * �G���R�[�h�p�ÓI�n�t�}������.
 *
 * @auther TURNER
 */
class StaticWriteHuff extends StaticHuffman implements ItfWriteHuff
{
	//protected Leaf[] encodeTable;

	/**
	 * false�̎��̓n�t�}���c���[�\�z�̂��߂̕p�x���W���[�h.
	 */
	protected boolean encodeModeFlg = false;

	/**
	 * �p�x���W���[�h������ۂɃG���R�[�h���郂�[�h�Ɉڍs����֐�.
	 * �n�t�}���c���[�I�u�W�F�N�g�͎g���̂ĂȂ̂Ō��ɖ߂����@�͂Ȃ�.
	 *
	 */
	protected void setEncodeMode(){
		encodeModeFlg = true;
	}

	/**
	 * �n�t�}���R�[�h�����N���X�̃R���X�g���N�^.
	 * 
	 * @param table_size �n�t�}�������̑傫��
	 */
	protected StaticWriteHuff( int table_size ){
		super( table_size );
		initFreq();
	}

	/**
	 * �n�t�}���R�[�h�ɃG���R�[�h����.
	 * 
	 * @param real_code    ���f�[�^�R�[�h
	 * @param packer       �r�b�g�p�b�J�[
	 *
	 * @throws IOException IO�G���[���N�������Ƃ�
	 */
	public void encode( int real_code, BitPacker packer )
		throws IOException
	{
		if( encodeModeFlg ){
			Leaf leaf = leafs[real_code];
			packer.putBits( leaf.code, leaf.code_len );
		}else{
			countFreq( real_code ); /* �p�x�𐔂��� */
		}
	}



	/**
	 * �n�t�}���e�[�u�����t�@�C���ɏ�������.
	 *
	 * @param effective_len_bits �L���������̓ǂݍ��݃r�b�g��
	 * @param special_index      �󔒃C���f�b�N�X�w��
	 * @param packer             �r�b�g�p�b�J�[�i�X�g���[���j
	 *
	 * @param IOException IO�G���[���N�������Ƃ�
	 */
	protected void makeTreeAndSaveTo(  int effective_len_bits, int special_index,
			BitPacker packer )
		throws IOException
	{

		int i;
		for( i = leafs.length - 1; i >= 0; i-- ){
			if( leafs[i].freq > 0 )break;
		}
		i++;
		if( i < leafs.length ){
			Leaf[] tmp_leafs = new Leaf[i];
			System.arraycopy( leafs, 0, tmp_leafs, 0, i );
			leafs = tmp_leafs;
		}

		Leaf[] sort = makeProvisionalTree();
		Leaf[] bakup = leafs;
		if( sort.length > 1 ){
			makeCodeLen(sort);
		}else{
			leafs = new Leaf[1];
			leafs[0] = sort[0];
		}

		//�R�[�h���P�����Ȃ��e�[�u���̏ꍇ
		//�n�t�}���R�[�h�̊���U��͂���Ȃ��B
		if( sort.length > 1 ){
			makeTableCode();
		}
		
		restoreTree();

		writeTableLen( effective_len_bits, special_index, packer );

		//encodeTable = new Leaf[table_size];
		//for( int i=0; i < leafs.length; i++ ){
		//	encodeTable[leafs[i].real_code] = leafs[i];
		//}

		leafs = bakup;
		setEncodeMode();

	}

	/**
	 * �p�x���W�v���邽�߂ɏ���������.
	 * 
	 */
	private void initFreq(){
		leafs = new Leaf[table_size];
		for( int i=0; i<table_size; i++ ){
			leafs[i] = new Leaf();
			leafs[i].real_code = i;
		}
	}

	/**
	 * �p�x���W�v����.
	 *
	 * @param code �p�x�𐔂���R�[�h
	 */
	private void countFreq( int code ){
		leafs[code].freq++;
	}

	/**
	 * ���������t�@�C���ɏ�������.
	 *
	 * @param effective_len_bits �L���������̃r�b�g��
	 * @param special_index      �󔒃C���f�b�N�X�w��
	 * @param packer             �r�b�g�p�b�J�[�i�X�g���[���j
	 *
	 * @param IOException �h�n�G���[���N�������Ƃ�
	 */
	protected void writeTableLen( int effective_len_bits, int special_index, 
			BitPacker packer )
		throws IOException
	{
		if( leafs.length == 1 ){
			packer.putBits( 0, effective_len_bits );
			packer.putBits( leafs[0].real_code, effective_len_bits );
		}
		else{
			packer.putBits( leafs.length, effective_len_bits );
			int c;
			for( int i = 0; i < leafs.length; i++ ){
				if( i == special_index ){
					int j = 0;
					while( leafs[i].code_len == 0 && j < 3 ){
						j++; i++;
						if( i == leafs.length ) break;
					}
					packer.putBits( j, 2 );
					if( i == leafs.length ) break;
				}
				c = leafs[i].code_len;
				if( c < 7 ){
					packer.putBits( c, 3 );
				}else{
					packer.putBits( 7, 3 );
					packer.putCluster( 1, c-7 );
					packer.putBit( 0 );
				}
			}
		}
	}

	/**
	 * heap�����̃T�u����.
	 * �b��c���[�쐬�֐��Ŏg�p����.
	 * 
	 * @param i         �q�[�v��̏����Ώۃf�[�^�̃C���f�b�N�X.
	 * @param heap      �q�[�v
	 * @param heapsize  �q�[�v��Ŏ��ۂɎg�p���Ă���傫��
	 */
	private void downHeap( int i, TreeNode[] heap, int heapsize ){
		int j;
		TreeNode k;

		k = heap[i];
		while ((j = 2 * i) < heapsize) {
			if ( j+1 < heapsize && heap[j].freq > heap[j+1].freq ){
				j++;
			}
			if (k.freq <= heap[j].freq){
				break;
			}
			heap[i] = heap[j];
			i = j;
		}
		heap[i] = k;
	}

	/**
	 * �b��c���[�쐬�֐�.
	 * �p�x�W�v���ʂ𗘗p����
	 * heap�\�[�g�̉��p��haffman�c���[���b��I�ɍ\�z����.
	 *
	 * @return �p�xfreq�̏����Ƀ\�[�g���ꂽ�c���[�̗t�̔z��
	 */
	protected Leaf[] makeProvisionalTree() {
		int i;
		int heap_size;
		TreeNode[] heap = new TreeNode[leafs.length*2];

		heap_size = 1; //�q�[�v�ł͂O�̃C���f�b�N�X�͎g��Ȃ��̂łP����͂��߂�
		for( i = 0; i < leafs.length; i++ ){
			if( leafs[i].freq > 0 ){
				heap[heap_size] = leafs[i];
				heap_size++;
			}
		}

		//���ڂ��Ȃ��B�q�[�v�͂P����n�܂��Ă���̂�
		if( heap_size == 1 ){
			throw new InternalError( "no freq data." );
		}

		Leaf[] code_len_sort = new Leaf[heap_size-1];

		if( heap_size == 2 ){
			treeRoot = heap[1];
			code_len_sort[0] = (Leaf)heap[1];
		}else{

			//* �����ȕp�x�̃m�[�h�����_�ɂ���悤�Ƀq�[�v�𐳋K������**
			for( i = heap_size / 2; i >= 1; i-- ){
				downHeap( i, heap, heap_size );
			}

			//* �����ȕp�x�̃m�[�h���Q�Â}�Ō������Ȃ���c���[���\�����Ă䂭 **
			TreeNode j,k;
			Branch l;
			int code_len_sort_i = 0;
			do{
				j = heap[1];
				if( j instanceof Leaf ){
					code_len_sort[code_len_sort_i++] = (Leaf)j;
				}
				heap_size--;
				heap[1] = heap[heap_size];
				heap[heap_size] = null;
				downHeap( 1, heap, heap_size );

				k = heap[1];
				if( k instanceof Leaf ){
					code_len_sort[code_len_sort_i++] = (Leaf)k;
				}
				
				l = new Branch();
				l.freq = j.freq + k.freq;
				l.child_0 = j;
				l.child_1 = k;

				heap[1] = l;
				downHeap( 1, heap, heap_size );
			}while( heap_size > 2 ); //** heap_size = 2�̂Ƃ��͂O�͎g��Ȃ��̂Ŏc����
			
			treeRoot = l;
		}

		return code_len_sort;

	}

	/**
	 * �c���[�̊e���[�̃��[�g����̋����𑪂��ďW�v�����֐�.
	 * ���[�g����̋����𑪂�ۂɍċA���g��.
	 *
	 * @param node      �����c���[�̃��[�g�m�[�h
	 * @param len_count ���[�g����̋����̏W�v�l��Ԃ��o�͈���
	 * @param depth     �ЂƂe�̃m�[�h�̃��[�g����̋���
	 * @param code      �n�t�}���R�[�h
	 */
	private void countLen( TreeNode node, int[] len_count, int depth, int code ){
		if( node instanceof Leaf ){
			len_count[(depth < len_count.length)?depth:len_count.length-1]++;
			((Leaf)node).code = code;
			((Leaf)node).code_len = depth;
		}else{
			countLen( ((Branch)node).child_0, len_count, depth + 1, code << 1  );
			countLen( ((Branch)node).child_1, len_count, depth + 1,(code << 1)+1 );
		}
	}

	/**
	 * �n�t�}���R�[�h�̒��������c���[������.
	 *
	 * @auther TURNER
	 */
	protected void makeCodeLen( Leaf[] sort )
	{
		int[] len_count = new int[CODELEN_MAX+1];
		countLen( treeRoot, len_count, 0, 0 );

		int i;
		int cum = 0;
		for( i = CODELEN_MAX; i > 0; i-- ){
			cum += len_count[i] << (CODELEN_MAX - i);
		}
		cum &= 0xFFFF;

		//*** ��������n�t�}���R�[�h��Z������
		if (cum > 0) {
			System.err.println("17");
			len_count[CODELEN_MAX] -= cum;	/* always len_cnt[16] > cum */
			do {
				for (i = CODELEN_MAX - 1; i > 0; i--) {
					if (len_count[i] > 0) {
						len_count[i]--;
						len_count[i + 1] += 2;
						break;
					}
				}
				cum--;
			} while (cum > 0);
		}

		//** �n�t�}���R�[�h�̕�������ݒ肷�� **
		int index = 0;
		for (i = CODELEN_MAX; i > 0; i--) {
			for( int k=0; k < len_count[i]; k++ ) {
				sort[index++].code_len = i;
			}
		}
	}

	public int decode( BitCutter cutter ){
		throw new InternalError( "unsupported method." );
	}

	protected void loadFrom( int a, int b, BitCutter c ){
		throw new InternalError( "unsupported method." );
	}

	protected void readTableLen( int a, int b, BitPacker c ){
		throw new InternalError( "unsupported method." );
	}

}
