/*
 * $RCSfile: LZHDecoder.java,v $ $Date: 2001/11/23 09:51:28 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.5 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.io.IOException;

class LZHDecoder extends LZHSlideDic {

	
	BitCutter cutter;       //�r�b�g�J�b�^�[�i���̓X�g���[������C�Ӄr�b�g�擾�j

	int now_pos = 0;        //�X���C�h�����̏������̐擪�ʒu.
	int match_pos = 0;      //�X���C�h�����̈�v�ʒu
	int match_len = 0;      //�X���C�h�����̈�v��

	ItfHuffman	hufC;       //�R�[�h�p�n�t�}������
	ItfHuffman	hufP;       //�X���C�h�����|�C���^�p�n�t�}������

	private long count = 0;

	/**
	 * �X���C�h�����f�R�[�_�[�N���X�̃R���X�g���N�^.
	 * 
	 * @param cmp_method �����̑傫���i�r�b�g���j
	 * @param cutter �r�b�g�J�b�^�[�i�C�ӂ̃r�b�g��؂�o�����̓X�g���[���̃��b�p�[�j
	 */
	protected LZHDecoder( int cmp_method, BitCutter cutter )
	{
		super( cmp_method );
		this.cutter = cutter;
	}

	/**
	 * �ÓI�n�t�}���e�[�u����ǂݍ���.
	 * 
	 * @exception java.io.IOException
	 */
	private void loadStaticHuffmanTable()
		throws IOException
	{
		//int NT = 16+3;
		//int PBIT = 5;		/* smallest integer such that (1 << PBIT) > * NP */
		//int TBIT = 5;		/* smallest integer such that (1 << TBIT) > * NT */
		//�n�t�}���e�[�u���̐���
		//StaticHuffman huf = new StaticHuffman(NT);
		StaticHuffmanC tmp_hufC = new StaticHuffmanC( 512 );
		StaticHuffmanP tmp_hufP = new StaticHuffmanP( huf_p_max );

		//BitCutter����n�t�}���e�[�u����ǂݍ���.
		//huf.loadFrom( TBIT, 3, cutter );
		//tmp_hufC.loadFrom( 9, huf, cutter);
		tmp_hufC.loadFrom( 9, -1, cutter);
		tmp_hufP.loadFrom( huf_p_bits , -1, cutter);

		hufC = tmp_hufC;
		hufP = tmp_hufP;

	}

	/**
	 * ���I�n�t�}���e�[�u��������������ilh1�p�j.
	 * 
	 */
	protected void initHuffmanTableForLH1()
	{
		hufC = new DynamicHuffmanC( 314 );
		hufP = new ReadyMadeHuffmanP( huf_p_max );
	}

	/**
	 * �f�[�^��W�J���P�o�C�g���o��.
	 * 
	 * @return ���o�����P�o�C�g�Ԃ�̃f�[�^
	 * @exception java.io.IOException
	 */
	protected int read()
		throws IOException
	{
		int c = -1;
		
		if( match_len == 0 ){
			
			if( block_size > 0 ){
				block_size--;
			}else{
				if( block_size == 0 ){
					//�u���b�N�T�C�Y�̓ǂݍ���.
					block_size = ( ((0xFF)&cutter.getBits(8)) << 8 );
					block_size |= (0xFF)&cutter.getBits(8);
					//���̃u���b�N�p�̃n�t�}���e�[�u���̓ǂݍ���.
					loadStaticHuffmanTable();
					block_size--;
				}
			}

			c = 0x1FF & hufC.decode(cutter);
			if( (c & 0x100) == 0 ){

				dic[now_pos++] = (byte)c;	//�����ɓo�^
				now_pos &= dic_mask;	//pos���������ɐ���

				
				count++;
			}
			else{
				//��v�������o��
  				match_len = (c & 0x0FF) + THRESHOLD;
					
				//��v�ꏊ�����o��
				int match_pos_wk = hufP.decode(cutter);
				match_pos = (now_pos - match_pos_wk - 1) & dic_mask;

				count++;
			}
		}
		if( match_len > 0 ){
			c = (0xFF & dic[match_pos]);

			match_pos++;
			match_pos &= dic_mask;

			dic[now_pos] = (byte)c;
			now_pos++;
			now_pos &= dic_mask;
			match_len--;

			count++;
		}
		return c;
	}
	
	/**
	 * �f�[�^��W�J���z��Ɏ��o��.
	 * 
	 * @param b    �f�[�^��ǂݍ��ރo�C�g�̔z��
	 * @param off  �f�[�^�̓ǂݍ��݊J�n�ʒu
	 * @param len  �f�[�^��ǂݍ��ޒ���
	 * @return ���o�����P�o�C�g�Ԃ�̃f�[�^
	 * @exception java.io.IOException
	 */
	protected void read( byte[] b, int off, int len )
		throws IOException
	{
		int c = -1;
		int tail = off + len;
		
		do{
			while( match_len == 0 && off < tail ){
				
				if( block_size > 0 ){
					block_size--;
				}else{
					if( block_size == 0 ){
						//�u���b�N�T�C�Y�̓ǂݍ���.
						block_size = ( ((0xFF)&cutter.getBits(8)) << 8 );
						block_size |= (0xFF)&cutter.getBits(8);
						//���̃u���b�N�p�̃n�t�}���e�[�u���̓ǂݍ���.
						loadStaticHuffmanTable();
						block_size--;
					}
				}

				c = hufC.decode(cutter);
				if( (c & 0x100) == 0 ){
					b[off++] = dic[now_pos++] = (byte)c;	//�����ɓo�^
					now_pos &= dic_mask;		//pos���������ɐ���
					count++;
				}
				else{
					//��v�������o��
	  				match_len = (c & 0x0FF) + THRESHOLD;
					//��v�ꏊ�����o��
					match_pos = (now_pos - hufP.decode(cutter) - 1) & dic_mask;
					count++;
				}
			}

			if( match_len > 0 ){
				int copy_len = Math.min( match_len, tail - off );
				int lest;
				count += copy_len;
                match_len -= copy_len;
				if( copy_len <= (lest = dic.length - match_pos) ){
					while( copy_len > 0 ){
						dic[now_pos++] = b[off++] = dic[match_pos++];
						now_pos &= dic_mask;
						copy_len--;
					}
				}
				else{
				    copy_len -= lest;
					while( lest > 0 ){
						dic[now_pos++] = b[off++] = dic[match_pos++];
						now_pos &= dic_mask;
						lest--;
					}
					match_pos = 0;
					while( copy_len > 0 ){
						dic[now_pos++] = b[off++] = dic[match_pos++];
						now_pos &= dic_mask;
						copy_len--;
					}
				}
			}
		}while( off < tail );
	}

	protected long getCount(){
		return count;
	}
}
