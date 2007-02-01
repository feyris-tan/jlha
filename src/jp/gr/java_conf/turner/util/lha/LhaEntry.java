/*
 * $RCSfile: LhaEntry.java,v $ $Date: 2002/03/09 14:53:06 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.9 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

import java.util.Date;
import java.io.*;

/**
 * ���̃N���X��LHA�̃t�@�C���G���g������\���܂��B
 *
 */
public class LhaEntry implements Cloneable, LhaConstants{

	private static final String FSP = System.getProperty("file.separator");

	private String  cmp_method = METHOD_LH5;//���k�@�̎�ރf�t�H���g��lh5
	private long    cmp_size;               //���k���ꂽ�t�@�C���T�C�Y
	private long    org_size;               //�I���W�i���̃t�@�C���T�C�Y
	private long    time;                   //�ŏI�X�V���ԁ{���t
	private byte    attrib;                 //�t�@�C���̑����iMS-DOS�j
	private int     hd_level;               //�w�b�_�̃��x��
	private String  name;                   //�t�@�C����

	private String  dir;                    //�f�B���N�g����
	private String  comment;                //�R�����g�i���x���P�ȍ~�̊g���w�b�_�j
	private int     hd_crc;                 //�w�b�_��CRC16

	private int     crc16;                  //�G���g���{�̂�CRC16
	private byte[]  extra = null;           //�g�������i��{�w�b�_���j
	private boolean isDir;                  //�f�B���N�g���t���O
	private char    os_type = OSTYPE_GENERIC;//�A�[�J�C�u�����ꂽOS

	private int checksum = 0;               //�w�b�_�`�F�b�N�T��

	private static final String SJIS_ENCODING = "SJIS";

	/**
	 * �w�肵�����O��LhaEntry�I�u�W�F�N�g�𐶐����܂�.
	 *
	 * @param name �G���g����(�t�@�C����)
	 * @exception NullPointerException �G���g������null�̂Ƃ�
	 * @exception IllegalArgumentException �G���g������������(�Œ�0xFFFE)
	 */
	public LhaEntry(String name) 
	{
		if (name == null) {
			throw new NullPointerException();
		}
		if (name.length() > 0xFFFE) {
			throw new IllegalArgumentException("entry name too long");
		}
		this.name = name;
	}

	/**
	 * ���łɂ���G���g���Ɠ����̃G���g���𐶐����܂�.
	 * @param e LhaEntry�I�u�W�F�N�g
	 */
	public LhaEntry(LhaEntry e) {
		name = e.name;
	}

	/**
	 * LhaEntry�𐶐����܂�.
	 */
	public LhaEntry() {
	}

	/**
	 * �G���g�����i�t�@�C�����j��Ԃ��܂�. <br>
	 * �f�B���N�g���͊܂݂܂���.<br>
	 * @return �G���g�����i�t�@�C�����j
	 */
	public String getName() {
		return name;
	}

	/**
	 * �f�B���N�g�����ǂ�����\���t���O���擾.
	 * @return �f�B���N�g����\���G���g���̂Ƃ���true.
	 */
	public boolean isDirectory() {
		return isDir;
	}

	/**
	 * �G���g�����i�t�@�C�����j��Ԃ��܂�. <br>
	 * �f�B���N�g���͊܂݂܂���.<br>
	 * @return �G���g�����i�t�@�C�����j
	 */
	public String toString() {
		return getName();
	}

	/**
	 * ���k�̃��\�b�h��Ԃ��܂�.("-lh0-", "-lh5-", ��)
	 * @return ���k���\�b�h
	 * @see #METHOD_LH0
	 * @see #METHOD_LH1
	 * @see #METHOD_LH2
	 * @see #METHOD_LH3
	 * @see #METHOD_LH4
	 * @see #METHOD_LH5
	 * @see #METHOD_LH6
	 * @see #METHOD_LH7
	 * @see #METHOD_LZS
	 * @see #METHOD_LZ4
	 * @see #METHOD_LZ5
	 */
	public String getCompressMethod()
	{
		return cmp_method;
	}
	
	/**
	 * ���k�̃��\�b�h���G���g���ɐݒ肵�܂�.
	 * @param method ���k���\�b�h
	 * @see #METHOD_LH0
	 * @see #METHOD_LH1
	 * @see #METHOD_LH2
	 * @see #METHOD_LH3
	 * @see #METHOD_LH4
	 * @see #METHOD_LH5
	 * @see #METHOD_LH6
	 * @see #METHOD_LH7
	 * @see #METHOD_LZS
	 * @see #METHOD_LZ4
	 * @see #METHOD_LZ5
	 */
	public void setCompressMethod( String method )
	{
		this.cmp_method = method;
	}

	/**
	 * ���k��̃T�C�Y��Ԃ��܂�
	 * @return ���k��̃T�C�Y
	 */
	public long getCompressedSize(){
		return cmp_size;
	}

	/**
	 * ���k��̃T�C�Y���G���g���ɐݒ肵�܂�
	 * @param cmp_size ���k��̃T�C�Y
	 */
	public void setCompressedSize( long cmp_size ){
		this.cmp_size = cmp_size;
	}
	
	/**
	 * ���k�O�̖{���̃T�C�Y��Ԃ��܂�
	 * @return �I���W�i���̃T�C�Y
	 */
	public long getSize(){
		return org_size;
	}

	/**
	 * ���k�O�̖{���̃T�C�Y���G���g���ɐݒ肵�܂�
	 * @param org_size �I���W�i���̃T�C�Y
	 */
	public void setSize( long org_size ){
		this.org_size = org_size;
		System.out.println( "setOrgSize:" + this.org_size );
	}

	/**
	 * �ŏI�X�V������Ԃ��܂�
	 * @return 1970 �N 1 �� 1 �� 00:00:00 ����̃~���b�ŕ\�����ŏI�X�V����
	 */
	public long getDate(){
		return time;
	}

	/**
	 * �ŏI�X�V������ݒ肵�܂�
	 * @param time 1970 �N 1 �� 1 �� 00:00:00 ����̃~���b�ŕ\�����ŏI�X�V����
	 */
	public void setDate( long time ){
		this.time = time;
	}

	/**
	 * �t�@�C��������Ԃ��܂�(MS-DOS�݊�).
	 * �֘A���ڂɋ������萔�̃r�b�g���Ƃ̘_���a���Ƃ������ʂ��Ԃ���܂�.<p>
	 * MS-DOS�ȊO�ō쐬���ꂽ�A�[�J�C�u�̏ꍇ�A���̃t�B�[���h�̒l��
	 * �������t�@�C�������𔽉f���Ă��Ȃ����Ƃ�����܂�.<p>
	 *
	 * @see LhaEntry#FA_RDONLY
	 * @see LhaEntry#FA_HIDDEN
	 * @see LhaEntry#FA_SYSTEM
	 * @see LhaEntry#FA_LABEL
	 * @see LhaEntry#FA_DIREC
	 * @see LhaEntry#FA_ARCH
	 */
	public byte getAttribute(){
		return attrib;
	}

	/**
	 * �t�@�C��������ݒ肵�܂�(MS-DOS�݊�).
	 * �֘A���ڂɋ������萔��C�ӂ̑g�ݍ��킹�Ńr�b�g���Ƃ̘_���a�Ƃ�A
	 * ���̌��ʂ������ɓn���܂�.<p>
	 *
	 * @param attrib �t�@�C������
	 *
	 * @see LhaEntry#FA_RDONLY
	 * @see LhaEntry#FA_HIDDEN
	 * @see LhaEntry#FA_SYSTEM
	 * @see LhaEntry#FA_DIREC
	 * @see LhaEntry#FA_ARCH
	 */
	public void setAttribute( byte attrib ){
		this.attrib = attrib;
	}
	
	/**
	 * LHA�̃w�b�_�̌`�����擾���܂�.
	 * <p>
	 * <table>
	 * <tr><td>0</td><td>�FLHArc�Ŏg���Ă����w�b�_�i�t�@�C�����̒������ɐ�������j</td></tr>
	 * <tr><td>1</td><td>�F0�ƌ݊�����ۂ��g��(Lharc�ł��t�@�C���ꗗ�����͌����j</td></tr>
	 * <tr><td>2</td><td>�F1,0�Ƃ̌݊����Ȃ�.</td></tr>
	 * </table>
	 * @return LHA�G���g���̃w�b�_�̌`��
	 */
	public int getHeaderLevel(){
		return hd_level;
	}

	/**
	 * �f�B���N�g���p�X������Ύ擾���܂�.
	 *
	 * @return �f�B���N�g���p�X
	 */
	public String getDir(){
		return dir;
	}

	/**
	 * �f�B���N�g���p�X��ݒ肵�܂�.
	 *
	 * @param str �f�B���N�g���p�X
	 */
	public void setDir( String str ){
		dir = str;
	}

	/**
	 * �w�b�_���̃R�����g���擾���܂�.
	 *
	 * @return �R�����g
	 */
	public String getComment(){
		return comment;
	}

	/**
	 * �w�b�_���ɃR�����g��ݒ肵�܂�.
	 *
	 * @param str �R�����g
	 */
	public void setComment( String str ){
		comment = str;
	}

	/**
	 * CRC�̒l���擾���܂�.
	 *
	 * @return crc16
	 */
	public int getCRC(){
		return crc16;
	}

	/**
	 * CRC�̒l��ݒ肵�܂�.
	 *
	 * @param crc crc16
	 */
	public void setCRC( int crc ){
		this.crc16 = crc;
	}

	/**
	 * �g����񂪂���Ύ擾���܂�.
	 *
	 * @return �g�����܂���null
	 */
	public byte[] getExtra(){
		if( extra == null ){
			return null;
		}
		byte[] ret = new byte[extra.length];
		System.arraycopy( extra, 0, ret, 0, extra.length );
		return ret;
	}

	/**
	 * �g������ݒ肵�܂�.
	 *
	 * @param extra �g�����
	 */
	public void setExtra( byte[] extra ){
		this.extra = extra;
	}
	
	/**
	 * OS�����擾���܂�.
	 *
	 * �֘A���ڂ̒萔�ȊO�̒l���ݒ肳��Ă���\���͗L��܂�.
	 *
	 * @return OS���
	 *
	 * @see LhaEntry#OSTYPE_MSDOS
	 * @see LhaEntry#OSTYPE_OS2  
	 * @see LhaEntry#OSTYPE_OS9  
	 * @see LhaEntry#OSTYPE_OS68K
	 * @see LhaEntry#OSTYPE_OS386
	 * @see LhaEntry#OSTYPE_HUMAN
	 * @see LhaEntry#OSTYPE_UNIX 
	 * @see LhaEntry#OSTYPE_CPM  
	 * @see LhaEntry#OSTYPE_FLEX 
	 * @see LhaEntry#OSTYPE_MAC  
	 * @see LhaEntry#OSTYPE_RUNSER
	 * @see LhaEntry#OSTYPE_NT	 
	 * @see LhaEntry#OSTYPE_95	 
	 * @see LhaEntry#OSTYPE_TOWNSOS
	 * @see LhaEntry#OSTYPE_XOSK 
	 * @see LhaEntry#OSTYPE_GENERIC 
	 */
	public char getOSType(){
		return os_type;
	}
	
	/**
	 * OS����ݒ肵�܂�.
	 *
	 * @param os OS���
	 * @see LhaEntry#OSTYPE_MSDOS
	 * @see LhaEntry#OSTYPE_OS2  
	 * @see LhaEntry#OSTYPE_OS9  
	 * @see LhaEntry#OSTYPE_OS68K
	 * @see LhaEntry#OSTYPE_OS386
	 * @see LhaEntry#OSTYPE_HUMAN
	 * @see LhaEntry#OSTYPE_UNIX 
	 * @see LhaEntry#OSTYPE_CPM  
	 * @see LhaEntry#OSTYPE_FLEX 
	 * @see LhaEntry#OSTYPE_MAC  
	 * @see LhaEntry#OSTYPE_RUNSER
	 * @see LhaEntry#OSTYPE_NT	 
	 * @see LhaEntry#OSTYPE_95	 
	 * @see LhaEntry#OSTYPE_TOWNSOS
	 * @see LhaEntry#OSTYPE_XOSK 
	 * @see LhaEntry#OSTYPE_GENERIC 
	 */
	public void setOSType( char os ){
		os_type = os;
	}

	private static long dosToJavaTime(long dtime) {
		Date d = new Date(
				(int)(((dtime >> 25) & 0x7f) + 80),
				(int)(((dtime >> 21) & 0x0f) - 1),
				(int)((dtime >> 16) & 0x1f),
				(int)((dtime >> 11) & 0x1f),
				(int)((dtime >> 5) & 0x3f),
				(int)((dtime << 1) & 0x3e) );
		return d.getTime();
	}

	private static long javaToDosTime(long time) {
		Date d = new Date(time);
		int year = d.getYear() + 1900;
		if (year < 1980) {
			return (1 << 21) | (1 << 16);
		}
		return (year - 1980) << 25 | (d.getMonth() + 1) << 21 |
			   d.getDate() << 16 | d.getHours() << 11 | d.getMinutes() << 5 |
			   d.getSeconds() >> 1;
	}

	/**
	 * �n�b�V���l��Ԃ��܂�.
	 * @return ���̃G���g���̃n�b�V���l
	 */
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * �X�g���[������w�b�_�����ۂɓǂݍ��݂܂�.
	 */
	protected boolean loadFrom( InputStream is )
		throws IOException
	{
		//�ŏ��̂Q�P�o�C�g�i�w�b�_�̃��x�������܂Łj��ǂݍ��ށB
		byte work[] = new byte[21];
		int ret;
		
	   	ret = readBytes( is, work, 0, 1 );
		if( ret <= 0 || work[0] == 0x00 ){
				//�t�@�C���̏I����LHA�G���g�����I���
				return false;
		}

		checksum = 0;
		ret = readBytes( is, work, 1, work.length-1 );
		if( ret < work.length-1 ){
			//�t�@�C���͂����s���Ă���̂ɂ����ɗ��Ă��܂����B
			throw new EOFException("Unexpected EOF. LHA Header was broken.");
		}

		int hd_size;	//�w�b�_�̃T�C�Y(���x���O�A�P�ł͊�{�w�b�_�݂̂̑傫���j
						//���x���Q�ł͑S�w�b�_�̑傫��
		int hd_sum; 	//�w�b�_�̃`�F�b�N�T��
		int fname_len;	//�t�@�C�����̒���
		long skip_size; //�X�L�b�v�T�C�Y
		int extlen = 0; //�g���w�b�_�P�̑傫��

		hd_level = work[20];
		switch( hd_level ){
		case 0:
			hd_size = (0xFF & work[0]);
			hd_sum	= (0xFF & work[1]);
			cmp_method = newString( work, 2, 5, null );
			skip_size = cmp_size = get32( work, 7 );
			org_size = get32( work, 11 );

			time = dosToJavaTime( get32( work, 15 ) );
			attrib = (byte)(0xFF&work[19]);
			
			if( (fname_len = is.read()) == -1 ){	//�t�@�C�����̒���
				throw new EOFException("Unexpected EOF. LHA Header was broken.");
			}
			work = new byte[fname_len+2];
			if( readBytes( is, work, 0, work.length ) < work.length ){	//�t�@�C�����{CRC
				throw new EOFException("Unexpected EOF. LHA Header was broken.");
			}
			{
				int i;
				for( i = fname_len - 1; i >= 0; i-- ){
					if( (char)work[i] == '/' ) break;
				}
				if( i < 0 ){
					name = newString( work, 0, fname_len, SJIS_ENCODING );
				}
				else{
					//String ssp = System.getProperty("file.separator");
					byte sp = (byte) ( 0xFF & FSP.charAt(0) );
					for( int j = 0; j < fname_len; j++ ){
						if( work[j] == '/' ){
							work[j] = sp;
						}
					}
					name = newString( work, i + 1, fname_len - (i + 1), SJIS_ENCODING );
					dir = newString( work, 0, (i + 1), SJIS_ENCODING );
				}
			}
			crc16 = get16( work,  fname_len );

			os_type = OSTYPE_GENERIC;
			extra = new byte[hd_size - (22 + fname_len)];
			if( readBytes( is, extra, 0, extra.length ) < extra.length ){
				throw new EOFException("Unexpected EOF. LHA Header was broken.");
			}
			break;
		case 1:
			hd_size = (0xFF & work[0]);
			hd_sum	= (0xFF & work[1]);
			cmp_method = newString( work, 2, 5, null );
			skip_size = cmp_size = get32( work, 7 );
			org_size = get32( work, 11 );
			time = dosToJavaTime( get32( work, 15 ) );
			attrib = (byte)(0xFF&work[19]);		   //level1�w�b�_�ł͖{��������0x20�Œ�

			fname_len = read8( is );
			name = readString( is, fname_len );
			crc16 = read16( is );
			os_type = (char)read8( is );

			extra = new byte[hd_size - (25 + fname_len)];
			if( readBytes( is, extra, 0, extra.length ) < extra.length ){
				throw new EOFException("Unexpected EOF. LHA Header was broken.");
			}
			extlen = read16( is );	   //�ŏ��̊g���w�b�_�̒����B
			if( (0xFF & checksum) != hd_sum ){
				throw new LhaException("Header checksum error. Header was broken.");
			}
			while( extlen > 0 ){
				cmp_size -= extlen;
				extlen = readExtHeader( is, extlen );
			}
			break;
		case 2:
			hd_size = get16( work, 0 );
			cmp_method = newString( work, 2, 5, null );
			skip_size = cmp_size = get32( work, 7 );
			org_size = get32( work, 11 );
			time = get32( work, 15 )*1000;
			attrib = (byte)(0xFF&work[19]);			//level2�w�b�_�ł͖{��������0x20�Œ�
			int wk_len = work.length;
			crc16 = read16( is ); wk_len += 2;
			os_type = (char)read8( is ); wk_len ++;
			extlen = read16( is ); wk_len += 2;
			while( extlen > 0 ){
				extlen = readExtHeader( is, extlen );
				wk_len += extlen;
			}
			is.skip( hd_size - wk_len );
			break;
		default:
			throw new LhaException("Unsupported header type level:"+hd_level+"(supported 0-2)");
		}
		return true;
	}

	/**
	 * �X�g���[������g���w�b�_��ǂݍ��݂܂�.
	 */
	private int readExtHeader( InputStream is, int len )
		throws IOException
	{
		len -= 2;
		byte[] b = new byte[len];
		if( readBytes( is, b, 0, len ) < len ){
			throw new EOFException("Unexpected EOF. LHA Header was broken.");
		}
		switch( (0xFF & b[0]) ){
		case 0x00://����
			hd_crc = get16( b, 1 );
			if( len > 3 ){
				extra = new byte[len-3];
				System.arraycopy( b, 3, extra, 0, len - 3 );
			}
			System.out.println( "read hd_crc:" + hd_crc );
			break;
		case 0x01://�t�@�C����
			name = newString( b, 1, b.length - 1, SJIS_ENCODING );
			break;
		case 0x02://�f�B���N�g����
			//String ssp = System.getProperty("file.separator");
			byte sp = (byte) ( 0xFF & FSP.charAt(0) );
			for( int i=1; i<b.length; i++ ){
				if( (0xFF & b[i]) == 0xFF ){
					b[i] = sp;
				}
			}
			dir = newString( b, 1, b.length - 1, SJIS_ENCODING );
			break;
		case 0x3f://�R�����g
			comment = newString( b, 1, b.length - 1, SJIS_ENCODING );
			break;
		/*
		 * �ȉ��n�r�ˑ��g���w�b�_�i�������j
		 */
		case 0x40://MS-DOS����
			attrib = (byte)(0xFF&b[1]);
			break;
		case 0x50://UNIX ������
			//�i�������j
			break;
		case 0x51://UNIX GID/UID
			//�i�������j
			break;
		case 0x52://UNIX �O���[�v��
			//�i�������j
			break;
		case 0x53://UNIX ���[�U�[��
			//�i�������j
			break;
		case 0x54://UNIX �ŏI�X�V����
			//�i�������j
			break;
		}
		return read16( is );
	}


	/**
	 * �X�g���[���փw�b�_���������݂܂�.
	 */
	protected void saveTo( OutputStream os )
		throws IOException
	{
		//*** level2 �w�b�_ **********

		// 0 �S�w�b�_�̑傫��  2  
		// 2 ���k�@�̎��  5  
		// 7 ���k��̃t�@�C���T�C�Y  4  
		//11 ���̃t�@�C���T�C�Y  4  
		//15 �t�@�C���̍ŏI�X�V����  4  
		//19 �\��ς݁i0x20 �Œ�j  1  
		//20 �w�b�_�̃��x�� (0x02)  1  
		//21 �t�@�C���� CRC-16  2  
		//23 ���ɂ��쐬���� OS �̎��ʎq  1  
		//24 �ŏ��̊g���w�b�_�̃T�C�Y  2  
		//26
		int work_len = 26;
		byte[] b_name;
		byte[] b_dir = null;
		byte[] b_comment = null;
		if( name == null ){
			throw new LhaException("no filename.");
		}
		b_name = name.getBytes( SJIS_ENCODING );
		work_len += ( 3 + 2 );//crc for header
		if( extra != null ){
			work_len += extra.length;
		}
		work_len += ( 3 + b_name.length );
		if( dir != null ){
			b_dir = dir.getBytes( SJIS_ENCODING );
			byte fsp = (byte)FSP.charAt(0);
			for( int i=0; i<b_dir.length; i++ ){
				if( b_dir[i] == fsp ){
					b_dir[1] = (byte)0xFF;
				}
			}
			if( b_dir[b_dir.length-1] != (byte)0xFF ){
				byte[] tmp = new byte[b_dir.length+1];
				System.arraycopy( b_dir, 0 , tmp, 0, b_dir.length );
				tmp[b_dir.length] = (byte)0xFF;
				b_dir = tmp;
			}
			work_len += ( 3 + b_dir.length );
		}
		if( comment != null ){
			b_comment = comment.getBytes( SJIS_ENCODING );
			work_len += ( 3 + b_comment.length );
		}
		if( (work_len & 0xFF) == 0 ){
			work_len++;
		}
		byte[] work = new byte[work_len];
		set16( work_len, work, 0 );
		System.out.println( "work_len:"+ work_len );
		getBytes( cmp_method, work, 2, 5 );//���k���\�b�h
		set32( cmp_size, work, 7 );
		System.out.println( "writeOrgSize:" + org_size );
		set32( org_size, work, 11 );
		set32( time/1000, work, 15 );
		work[19] = (byte)0x20;//attrib
		work[20] = (byte)0x02;
		set16( crc16, work, 21 );
		work[23] = (byte)os_type;//generic

		//�g���w�b�_�E����	
		if( extra != null ){
			set16( 1+2+extra.length+2, work, 24 );
		}else{
			set16( 1+2+2, work, 24 );
		}
		int ext_pt = 26;
		work[ext_pt++] = 0x00;
		int hd_crc16_pt = ext_pt;
		set16( 0, work, ext_pt );ext_pt += 2;//�w�b�_�pcrc16(��)
		if( extra != null ){
			System.arraycopy( extra, 0, work, ext_pt, extra.length );
			ext_pt += extra.length;
		}

		//�g���w�b�_ �t�@�C����
		set16( 1+b_name.length+2, work, ext_pt );ext_pt += 2;
		work[ext_pt++] = 0x01;
		System.out.println( "ext_pt:" +ext_pt + " b_name:" + b_name.length );
		System.arraycopy( b_name, 0, work, ext_pt, b_name.length );
		ext_pt+=b_name.length;

		//�g���w�b�_ �f�B���N�g����
		if( b_dir != null ){
			set16( 1+b_dir.length+2, work, ext_pt );ext_pt += 2;
			work[ext_pt++] = 0x02;
			System.arraycopy( b_dir, 0, work, ext_pt, b_dir.length );
			ext_pt+=b_dir.length;
		}

		//�g���w�b�_ �R�����g
		if( b_comment != null ){
			set16( 1+b_comment.length+2, work, ext_pt );ext_pt += 2;
			work[ext_pt++] = 0x3F;
			System.arraycopy( b_comment, 0, work, ext_pt, b_comment.length );
			ext_pt+=b_comment.length;
		}

		set16( 0, work, ext_pt );

		CRC16 hd_crc = new CRC16();
		hd_crc.update( work );
		set16( hd_crc.getValue(), work, hd_crc16_pt );

		os.write( work );
	}

	private static final int get16(byte b[], int off){
		return (b[off] & 0xff) | ((b[off+1] & 0xff) << 8);
	}

	private static final void set16(int x, byte[] b, int off){
		b[off]   = (byte)(0x00FF & x);
		b[off+1] = (byte)((0xFF00 & x) >>> 8);
	}

	private static final long get32(byte b[], int off){
		return get16(b, off) | ((long)get16(b, off+2) << 16);
	}

	private static final void set32( long x, byte[] b, int off){
		set16((int)(0x0000FFFF&x), b, off);
		set16((int)((0xFFFF0000&x) >>> 16), b, off+2);
	}

	private int read8( InputStream is )
		throws IOException
	{
		int ret;
		ret = is.read();
		if( ret == -1 ){
			throw new EOFException("Unexpected EOF. LHA Header was broken.");
		}
		checksum += (0xFF&ret);
		return ret;
	}

	private void write8( int x, OutputStream os )
		throws IOException
	{
		os.write( 0xFF & x );
		checksum += (0xFF&x);
	}


	private int read16( InputStream is )
		throws IOException
	{
		int lo = read8( is );
		int hi = read8( is );
		return ((hi<<8) & 0xFF00)|(lo & 0xFF);
	}

	private void write16( int x, OutputStream os )
		throws IOException
	{
		write8( 0x00FF & x, os );
		write8((0xFF00 & x) >>> 8, os );
	}

	private String readString( InputStream is, int len )
		throws IOException
	{
		byte[] read_work = new byte[len];
		int read_len = readBytes( is, read_work, 0, len );
		if( read_len < len ){
			throw new EOFException("Unexpected EOF. LHA Header was broken.");
		}
		String ret = newString(read_work, 0, len, SJIS_ENCODING );
		return ret;
	}

	private void writeString( String str, OutputStream os, int len )
		throws IOException
	{
		byte[] b = new byte[len];
		getBytes( str, b, 0, len );
		writeBytes( os, b, 0, len );
	}

	private int readBytes( InputStream is, byte[] b, int pos, int len )
		throws IOException
	{
		int ret = 0;
		int pos_wk = pos;
		int len_wk = len;

		while( len_wk > 0 ){
			ret = is.read( b, pos_wk, len_wk );
			if( ret <= 0 )break; 
			pos_wk += ret;
			len_wk -= ret;
		}

		for( int i=pos; i< pos_wk; i++ ){
			checksum += (0xFF&b[i]);
		}

		if( (ret == -1) || (len == len_wk && len > 0) ){
			return -1;
		}
		else{
			return (pos_wk - pos);
		}
	}

	private void writeBytes( OutputStream os, byte[] b, int pos, int len )
		throws IOException
	{
		os.write( b, pos, len );
		for( int i=pos; i < pos + len; i++ ){
			checksum += (0xFF&b[i]);
		}
	}

	private static String newString( byte[] b, int off, int len, String enc )
		throws IOException
	{
		String ret;
		try{
			if( enc != null ){
				ret = new String(b, off, len, enc );
			}
			else{
				ret =  new String(b, 0, off, len);
			}
		}catch( NoSuchMethodError e ){
			ret =  new String(b, 0, off, len);
		}
		return ret;
	}

	private static String newString( byte[] b, String enc )
		throws IOException
	{
		return newString( b, 0, b.length, enc );
	}

	private static void getBytes( String str, byte[] b, int off, int len )
		throws IOException
	{
		try{
			byte[] work;
			work = str.getBytes( SJIS_ENCODING );
			System.arraycopy( work, 0, b, off, Math.min( work.length, len ) );
		}catch( NoSuchMethodError e ){
			str.getBytes( 0, Math.min( str.length(), len ), b, off );
		}
	}

}
