/*
 * $RCSfile: BitPacker.java,v $ $Date: 2001/11/15 16:38:23 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.3 $
 *
 * Copyright 2000 by TURNER.
 */
package jp.gr.java_conf.turner.util.lha;

import java.io.*;

/**
 * �C�ӂ̃r�b�g���̃f�[�^�������o����o�̓X�g���[��.
 *
 * @author TURNER
 */
class BitPacker extends OutputStream{

	protected OutputStream os;

	protected static final int BYTE_BITS = 8;
	protected int bitbuf = 0;
	protected int bitbuf_count = 0;
	protected long write_counter = 0L;

	protected static final int PACK_MAX = 32 - (BYTE_BITS) + 1;

	protected BitPacker( OutputStream arg_os ){
		os = arg_os;
	}

	protected BitPacker( RandomAccessFile raf ) throws IOException{
		os = new FileOutputStream( raf.getFD() );
	}

	/**
	 *  �P�o�C�g�ɖ����Ȃ����[�ȃr�b�g���O�Ŗ��߂���A�P�o�C�g��������.
	 * 
	 * @param b    �������ރf�[�^
	 * 
	 * @author  TURNER
	 */
	public void write( int b )
		throws IOException
	{
		adjustByteAlignment();
		os.write( b );
		write_counter ++;
	}


	/**
	 *  �P�o�C�g�ɖ����Ȃ����[�ȃr�b�g���O�Ŗ��߂���A�����o�C�g��������.
	 * 
	 * @param b     �������ރf�[�^�̃o�C�g�z��
	 * @param off   �������ރf�[�^�̊J�n�I�t�Z�b�g
	 * @param len   �������ރf�[�^�̒���
	 * 
	 * @author      TURNER
	 */
	public void write( byte[] b, int off, int len )
		throws IOException
	{
		adjustByteAlignment();
		os.write( b, off, len );
		write_counter += len;
	}

	/**
	 *  �P�o�C�g�ɖ����Ȃ����[�ȃr�b�g���O�Ŗ��߂���A�����o�C�g��������.
	 * 
	 * @param b     �������ރf�[�^�̃o�C�g�z��
	 * 
	 * @author      TURNER
	 */
	public void write( byte[] b )
		throws IOException
	{
		this.write( b, 0, b.length );
	}

	/**
	 *  �C�ӂ̃r�b�g���f�[�^����������.
	 * 
	 * @param b �������ރf�[�^
	 * @param n �������ރr�b�g��
	 * 
	 * @author TURNER
	 */
	 public void putBits( int b, int n )
	 	throws IOException
	 {
		if( n < 0 || n > PACK_MAX ){
			throw new 
				IllegalArgumentException("arg="+n+" limit:0<=arg<="+PACK_MAX);
		}

		int mask = (( 0xFFFFFFFF >>> bitbuf_count ));
		bitbuf = (bitbuf & ~mask) | ((b << (32-(bitbuf_count+n))) & mask);
		bitbuf_count += n;

		while( bitbuf_count >= BYTE_BITS ){
			writeByte();
		}
	 }

	/**
	 *  1�r�b�g���f�[�^����������.
	 * 
	 * @param b �������ރr�b�g
	 * 
	 * @author TURNER
	 */
	 public void putBit( int b )
	 	throws IOException
	 {	
		if( (b & ~(1)) != 0 ){
			throw new IllegalArgumentException( "bit=0or1" );
		}

		int mask = (( 0xFFFFFFFF >>> bitbuf_count ));
		bitbuf = (bitbuf & ~mask) | (b << (32-(bitbuf_count+1)));
		bitbuf_count ++;

		if( bitbuf_count >= BYTE_BITS ){
			writeByte();
		}
	}

	/**
	 *  �����r�b�g�̌ł܂����������.
	 * 
	 * @param b     �������ރr�b�g
	 * @param len   �������ޒ���
	 * 
	 * @author TURNER
	 */
	public void putCluster( int b, int len )
		throws IOException
	{
		if( len < 0 || len > PACK_MAX ){
			throw new 
				IllegalArgumentException("len="+len+" limit:0<=len<="+PACK_MAX);
		}
		if( (b & ~(1)) != 0 ){
			throw new IllegalArgumentException( "bit=0or1" );
		}

		int mask = (( 0xFFFFFFFF >>> bitbuf_count ));
		if( b == 1 ){
			bitbuf |= mask;
		}else{
			bitbuf &= (~mask);
		}

		bitbuf_count += len;

		while( bitbuf_count >= BYTE_BITS ){
			writeByte();
		}
	}


	/**
	 *  �X�g���[�����N���[�Y����.
	 * 
	 * @author TURNER
	 */
	public void close()
		throws IOException
	{
		this.flush();
		os.close();
	}

	/**
	 *  ���[�ȃr�b�g���������񂾌�X�g���[�����t���b�V������.
	 * 
	 * @author TURNER
	 */
	public void flush()
		throws IOException
	{
		adjustByteAlignment();
		os.flush();
	}


	/**
	 * �P�o�C�g��������.
	 * 
	 * @author TURNER
	 */
	private void writeByte()
		throws IOException
	{
		os.write( bitbuf>>>(BYTE_BITS*3) );
		bitbuf <<= BYTE_BITS;
		bitbuf_count -= BYTE_BITS;
		write_counter++;
	}

	/**
	 *  �o�b�t�@�Ɏc���Ă���P�o�C�g�ɖ����Ȃ����[�ȃr�b�g�̂�����ɂO�𖄂߂ď�������.
	 * 
	 * @author TURNER
	 */
	protected void adjustByteAlignment()
		throws IOException
	{
		if( bitbuf_count > 0 ){
			int mask = ( 0xFFFFFFFF >>> bitbuf_count ) ;
			bitbuf &= (~mask);
			os.write( bitbuf >>> (BYTE_BITS*3) );
			bitbuf_count = 0;
			write_counter++;
		}
	}

	/**
	 *  �������݃o�C�g���J�E���^���擾����.
	 * 
	 * @return �������݃o�C�g��
	 * 
	 * @author TURNER
	 */
	public long getWriteCounter() {
		return write_counter;
	}

	/**
	 *  �������݃o�C�g���J�E���^�����Z�b�g����.
	 *j
	 * @author TURNER
	 */
	public void resetWriteCounter() {
		write_counter = 0L;
	}

}
