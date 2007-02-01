/*
 * $RCSfile: ReadyMadeHuffmanP.java,v $ $Date: 2001/11/15 16:39:26 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.2 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;
import java.io.*;

/**
 * �X���C�h������v�ʒu�f�R�[�h�p�Œ�n�t�}������.
 * lh1�p�Œ莫��.
 */
class ReadyMadeHuffmanP extends StaticHuffman
{
	/**
	 * �Œ�n�t�}�������R���X�g���N�^.
	 */
	protected ReadyMadeHuffmanP( int size ){
		super( size );

		initTableLen();
		try{
			makeTableCode();//super
		}catch( IOException e ){
			throw new InternalError( "Ready made huffman table init error." );
		}
		restoreTree();
	}
	
	
	protected void readTableLen( int effective_len_bits, int special_index, BitCutter cutter )
	{
	    throw new InternalError("Not use this method in this class.");
	}

	/**
	 * �n�t�}���R�[�h�̕��������Œ�l�ŏ���������.
	 *
	 */
    protected void initTableLen()
    {
	    int i, j;
	    int code, weight;
	    int tbl_index;

	    tbl_index = 0;;
	    j = ready_made_tbl[tbl_index++];
	    weight = 1 << (16 - j);
	    code = 0;
	    for (i = 0; i < leafs.length; i++) {
		    while (ready_made_tbl[tbl_index] == i) {
			    j++;
			    tbl_index++;
			    weight >>= 1;
		    }
		    leafs[i] = new Leaf();
		    leafs[i].code_len = j;
		    leafs[i].real_code = i;
		    code += weight;
	    }
    }
    
	static final short[] ready_made_tbl = {3, 0x01, 0x04, 0x0c, 0x18, 0x30, 0};

	/**
	 * �c���[�����ǂ��ăf�R�[�h���A�c���[���X�V����.
	 *
	 * @param cutter �r�b�g�J�b�^�[�i�X�g���[���j
	 */
	public int decode( BitCutter cutter )
		throws IOException
	{
		int ret = super.decode(cutter);
		ret <<= 6;
		ret |= cutter.getBits(6);
		return ret;
	}
}
