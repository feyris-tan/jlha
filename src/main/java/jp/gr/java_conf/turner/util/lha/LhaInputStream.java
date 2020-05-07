/*
 * $RCSfile: LhaInputStream.java,v $ $Date: 2001/04/10 18:01:37 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.6 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.io.*;

/**
 * LHA�t�@�C���f�R�[�_�X�g���[���N���X.
 * �Ή����\�b�h -lh0-, -lh1-, -lh4-,-lh5-,-lh6- ,-lh7-
 *
 * @author		TURNER
 * @version 	0.2
 */
public class LhaInputStream extends FilterInputStream
{

	private LhaEntry lha_entry;			//LHA�G���g���N���X
	private CRC16 crc = new CRC16();
	private byte[] tmpbuf = new byte[256];

	private long remaining; 			//�W�J��̃f�[�^�̂̂���̒����B

	private boolean closed = false;
	private LZHDecoder lzhDecoder;		//�̐S�v�̃f�R�[�_�N���X

	// �G���g���[���Ō�ɒB���Ă��邩�ǂ����̃t���O�B
	private boolean lha_entryEOF = false;

	
	static final int CMP_TYPE_LH0 = 0;
	static final int CMP_TYPE_LH1 = 1;
	static final int CMP_TYPE_LH2 = 2;
	static final int CMP_TYPE_LH3 = 3;
	static final int CMP_TYPE_LH4 = 4;
	static final int CMP_TYPE_LH5 = 5;
	static final int CMP_TYPE_LH6 = 6;
	static final int CMP_TYPE_LH7 = 7;
	private int cmp_type;

	/**
	 * �X�g���[����close���Ă��Ȃ����`�F�b�N����.
	 * 
	 * @exception java.io.IOException IO�G���[���N�����Ƃ�
	 */
	private void ensureOpen()
		throws IOException
	{
		if (closed) {
			throw new LhaException("Stream closed");
		}
	}

	/**
	 * �k�g�`�f�R�[�_�X�g���[���̃R���X�g���N�^.
	 * 
	 * @param in �k�g�`�t�@�C���t�H�[�}�b�g�̓��̓X�g���[��
	 */
	public LhaInputStream(InputStream in)
	{
		super( new BitCutter( in ) );
		if(in == null) {
			throw new NullPointerException("InputStream in null");
		}
	}

	/**
	 * LHA�t�@�C���G���g����ǂݍ��݁A�G���g���f�[�^�̍ŏ��ɃX�g���[����z�u���܂�.
	 * <p>
	 * �t�@�C���G���g���̓A�[�J�C�u���̂P�t�@�C����\���Ă��܂��B
	 * ���̊֐����Ăяo�����Ƃɂ��A�[�J�C�u���̂P�̃f�[�^���X�g���[���Ƃ���
	 * �ǂݍ��݂ł���悤�ɂȂ�܂��B
	 * <p>
	 * �܂��A�P�̃G���g����EOF�ɒB���Ă�����ɃG���g���������Ă������A
	 * ���̊֐����Ăяo���Ď��̃G���g���f�[�^�̍ŏ��ɃX�g���[����z�u�ł��܂��B
	 * <p>
	 * �A�[�J�C�u�̍Ō�ɒB�����Ƃ���null��Ԃ��܂��B
	 *
	 * @return	�t�@�C���G���g�����܂�����Ύ���LhaEntry�I�u�W�F�N�g
	 * @exception java.io.IOException IO�G���[���N�����Ƃ�
	 */
	public synchronized LhaEntry getNextEntry() 
		throws IOException 
	{
		ensureOpen();
		if (lha_entry != null) {
			closeEntry();
		}
		crc.reset();

		if ((lha_entry = readHeader()) == null) {
			return null;
		}
		remaining = lha_entry.getSize();
		lha_entryEOF = false;
		if( lha_entry.getCompressMethod().equals(LhaEntry.METHOD_LH0) ){
			cmp_type = CMP_TYPE_LH0;
		}else if( lha_entry.getCompressMethod().equals(LhaEntry.METHOD_LH1) ){
			cmp_type = CMP_TYPE_LH1;
   			lzhDecoder = new LZHDecoder( CMP_TYPE_LH1, (BitCutter)in );
		}else if( lha_entry.getCompressMethod().equals(LhaEntry.METHOD_LH4) ){
			cmp_type = CMP_TYPE_LH4;
   			lzhDecoder = new LZHDecoder( CMP_TYPE_LH4, (BitCutter)in );
		}else if( lha_entry.getCompressMethod().equals(LhaEntry.METHOD_LH5) ){
			cmp_type = CMP_TYPE_LH5;
   			lzhDecoder = new LZHDecoder( CMP_TYPE_LH5, (BitCutter)in );
		}else if( lha_entry.getCompressMethod().equals(LhaEntry.METHOD_LH6) ){
			cmp_type = CMP_TYPE_LH6;
   			lzhDecoder = new LZHDecoder( CMP_TYPE_LH6, (BitCutter)in );
		}else if( lha_entry.getCompressMethod().equals(LhaEntry.METHOD_LH7) ){
			cmp_type = CMP_TYPE_LH7;
   			lzhDecoder = new LZHDecoder( CMP_TYPE_LH7, (BitCutter)in );
		}else{
			throw new LhaException("unsupported method:"+lha_entry.getCompressMethod());
		}
		return lha_entry;
	}

	/**
	 * ���݂�LHA�G���g���[���N���[�Y���A���̃G���g���̒��O�܂ŃX�L�b�v���܂�.
	 *
	 * @exception java.io.IOException IO�G���[���N�����Ƃ�
	 */
	public synchronized void closeEntry()
		throws IOException
	{
		ensureOpen();
		while (read(tmpbuf, 0, tmpbuf.length) != -1) ;
		if( lha_entry.getCRC() != crc.getValue() ){
			throw new LhaException( "CRC check error. at:"+lha_entry.getName() );
		}
		lha_entryEOF = true;
	}

	/**
	 * ���݂̓��̓f�[�^�� EOF �ɒB�������ƂŌĂяo�����ꍇ�� 0 ��Ԃ��܂�,
	 * �����łȂ��ꍇ�͏�� 1 ��Ԃ��܂�.
	 * <p>
	 * �{��InputStream��available()�֐��̓u���b�N�����ɓǂ݂��݉\��
	 * �o�C�g����Ԃ����߂̂��̂ł��B
	 * <p>
	 * �ł����u���b�N�Ȃ��œǂݍ��߂���ۂ̃o�C�g���͓W�J���Ă݂Ȃ��Ƃ킩��Ȃ��̂�
	 * �����ł͂Ƃ肠�������̂悤�Ȓl��Ԃ��܂��B(ZipInputStream�̎d�l��^���Ă��܂�)
	 * 
	 * @return	   ���݂̓��̓f�[�^�� EOF �ɒB���Ă��Ȃ��ꍇ�͏�� 1.
	 * @exception java.io.IOException
	 */
	public synchronized int available() 
		throws IOException 
	{
		ensureOpen();
		if (lha_entryEOF) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * �ǂݍ��ݒ���LHA�G���g������P�o�C�g�ǂݍ��݂܂�.
	 *
	 * @return �ǂݍ��܂ꂽ�l
	 * 		EOF�ɒB���Ă����Ƃ���-1��Ԃ��B
	 * @exception java.io.IOException IO�G���[���N�����Ƃ�
	 */
	public synchronized int read()
		throws IOException
	{
		int result = 0;
		
		ensureOpen();
		if ( lha_entryEOF ) {
			return -1;
		}
		if ( remaining <= 0 ) {
			lha_entryEOF = true;
			return -1;
		}
		
		switch( cmp_type )
		{
		case CMP_TYPE_LH0:
			result = in.read();
			break;
		case CMP_TYPE_LH1:
		case CMP_TYPE_LH4:
		case CMP_TYPE_LH5:
		case CMP_TYPE_LH6:
		case CMP_TYPE_LH7:
			result = lzhDecoder.read();
			break;
		default:
			throw new InternalError();
		}

		if(result == -1){
			throw new EOFException("Unexpected end of LHA input stream.");
		}

		crc.update( (byte)result );
		remaining --;
		if( remaining == 0 ){
			lha_entryEOF = true;
		}
		return result;
	}

	/**
	 * �ǂݍ��ݒ��̃G���g������f�[�^��ǂݍ��݂܂�.
	 * �w�肳�ꂽbyte�z���
	 * �z��̑傫���Ԃ�܂ŉ\�Ȍ���ǂݍ��݂܂��B
	 *
	 * @param b �ǂݍ��܂ꂽ�f�[�^���i�[���邽�߂�byte�^�̔z��
	 * @return �ǂݍ��܂ꂽ�L���ȃo�C�g���B�܂��G���g����
	 * 				EOF�ɒB���Ă����Ƃ���-1��Ԃ��B
	 * @exception java.io.IOException IO�G���[���N�����Ƃ� 
	 */
	public synchronized int read( byte[] b )
		throws IOException
	{
		return read( b, 0, b.length );
	}

	/**
	 * �ǂݍ��ݒ��̃G���g������f�[�^��ǂݍ��݂܂�.
	 * �����Ŏw�肳�ꂽ�Ԃ�܂�
	 * �\�Ȍ���byte�̔z��ɓǂݍ��݂܂��B
	 *
	 * @param b �ǂݍ��܂ꂽ�f�[�^���i�[���邽�߂�byte�^�̔z��
	 * @param off �z���̓ǂݍ��݊J�n�C���f�b�N�X�B
	 * @param len �ő�ǂݍ��݃o�C�g��
	 * @return �ǂݍ��܂ꂽ�L���ȃo�C�g���B�܂��G���g����
	 * 				EOF�ɒB���Ă����Ƃ���-1��Ԃ��B
	 * @exception java.io.IOException IO�G���[���N�����Ƃ� 
	 */
	public synchronized int read(byte[] b, int off, int len)
		throws IOException
	{
		if( lha_entryEOF ){
			return -1;
		}
		if ( len >= remaining ) {
			lha_entryEOF = true;
			len -= (int)(len - remaining);
		}

		switch( cmp_type )
		{
		case CMP_TYPE_LH0:
			in.read( b, off, len );
			break;
		case CMP_TYPE_LH1:
		case CMP_TYPE_LH4:
		case CMP_TYPE_LH5:
		case CMP_TYPE_LH6:
		case CMP_TYPE_LH7:
			lzhDecoder.read( b, off, len );
			break;
		default:
			throw new InternalError();
		}

		crc.update( b, off,len );
		remaining -= len;
		return len;
	}

	/**
	 * ���ݓǂݍ��ݒ��̃X�g���[���������Ŏw�肵���o�C�g�������X�L�b�v���܂�.
	 * EOF�ɒB�����ꍇ�͂����Œ�~���܂�.
	 * @param n �X�L�b�v���鐔
	 * @return ���ۂɃX�L�b�v������
	 * @exception java.io.IOException IO�G���[���N�����Ƃ� 
	 * @exception IllegalArgumentException	n < 0 �̂Ƃ�
	 */
	public synchronized long skip(long n) throws IOException {
		if (n < 0) {
			throw new IllegalArgumentException("negative skip length");
		}
		ensureOpen();

		long total = 0;
		long len;
		while (total < n) {
			len = n - total;
			if (len > tmpbuf.length) {
				len = tmpbuf.length;
			}
			len = read(tmpbuf, 0, (int)len);
			if (len == -1) {
				lha_entryEOF = true;
				break;
			}
			total += len;
		}
		return total;
	}

	/**
	 * LHAInputStream���N���[�Y���܂�.
	 * �G���g���ł͂Ȃ��f�[�^�̌��̃X�g���[�����N���[�Y���܂�.
	 * @exception java.io.IOException IO�G���[���N�����Ƃ�
	 */
	public synchronized void close() throws IOException {
		in.close();
		closed = true;
	}

	/*
	 * ���̃G���g����LHA�t�@�C���w�b�_��ǂݍ��݂܂��B
	 */
	private LhaEntry readHeader()
		throws IOException
	{
		LhaEntry e = null;
		e = new LhaEntry();
		if( e.loadFrom( in ) == true ){
			return e;
		}else{
			return null;
		}
	}

	/**
	 * �V���� <code>LhaEntry</code> �I�u�W�F�N�g���t�@�C�������w�肵��
	 * �������܂�.
	 *
	 * @param name the LHA file entry name
	 */
	protected LhaEntry createLhaEntry(String name) {
		return new LhaEntry(name);
	}

}
