/*
 * $RCSfile: LhaConstants.java,v $ $Date: 2000/04/15 17:28:07 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.1.1.1 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

/**
 * LHA�̊e��萔���`���܂��B
 *
 * @version 	0.1, 2000/03/13
 */
public interface LhaConstants{

	/** 
	 * ���[�h�I�����[�����l�p�}�X�N.
	 * @see LhaEntry#getAttribute() 
	 * @see LhaEntry#setAttribute(byte)
	 */
	public static final byte FA_RDONLY= 1;

	/** 
	 * �B���t�@�C�������l�p�}�X�N.
	 * @see LhaEntry#getAttribute() 
	 * @see LhaEntry#setAttribute(byte)
	 */
	public static final byte FA_HIDDEN= 2;

	/** 
	 * �V�X�e���t�@�C�������l�p�}�X�N.
	 * @see LhaEntry#getAttribute() 
	 * @see LhaEntry#setAttribute(byte)
	 */
	public static final byte FA_SYSTEM= 4;


	/** 
	 * �{�����[�����x�������l�p�}�X�N�i�����g��Ȃ��H�j.
	 * @see LhaEntry#getAttribute() 
	 * @see LhaEntry#setAttribute(byte)
	 */
	public static final byte FA_LABEL = 8;

	/** 
	 * �f�B���N�g�������l�p�}�X�N.
	 * @see LhaEntry#getAttribute() 
	 * @see LhaEntry#setAttribute(byte)
	 */
	public static final byte FA_DIREC =16;

	/** 
	 * �A�[�J�C�u�t���O�����l�p�}�X�N.
	 * @see LhaEntry#getAttribute() 
	 * @see LhaEntry#setAttribute(byte)
	 */
	public static final byte FA_ARCH  =32;

	/*
	 *OSTYPE�萔�ɂ��Ă͈ȉ��̃T�C�g�̏������ɂ��Ă��܂��B
	 *�iDolphin's �z�[���y�[�W�j
	 * http://www2m.biglobe.ne.jp/~dolphin/
	 *
	 * �����ɗ\�񂳂�Ă������ 
	 * �����ł��������Ƃ́ALHA�̊J���҂ł���g�莁��
	 * CMAGAZINE1991/1�̋L���Ō��\�������̂Ƃ����Ӗ��������B
	 */

	/** 
	 * OS�^�C�vMS-DOS.
	 * <p>�l='M'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_MSDOS = 'M';
	/** 
	 * OS�^�C�vOS/2.
	 * <p>�l='2'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_OS2   = '2';
	/** 
	 * OS�^�C�vOS-9.(MacOS�ł͂Ȃ�).
	 * <p>�l='9'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_OS9   = '9';
	/** 
	 * OS�^�C�vOS68K.
	 * <p>�l='K'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_OS68K = 'K';
	/** 
	 * OS�^�C�vOS386.
	 * <p>�l='3'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_OS386 = '3';
	/** 
	 * OS�^�C�vHUMAN68K.
	 * <p>�l='H'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_HUMAN = 'H';
	/** 
	 * OS�^�C�vUNIX.
	 * <p>�l='U'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_UNIX  = 'U';
	/** 
	 * OS�^�C�vCP/M.
	 * <p>�l='C'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_CPM   = 'C';
	/** 
	 * OS�^�C�vFLEX.
	 * <p>�l='F'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_FLEX  = 'F';
	/** 
	 * OS�^�C�vMacOS.
	 * <p>�l='m'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_MAC   = 'm';
	/** 
	 * OS�^�C�vRUNSER.
	 * <p>�l='R'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_RUNSER= 'R';

	/** 
	 * OS�^�C�vWindowsNT. <p>
	 * �����.(�g�莁��CMAGAZINE1991/1�̋L���ŏ��������̂łȂ��Ƃ����Ӗ�)
	 * <p>OSTYPE_MSDOS���g���邱�Ƃ������B<p>
	 * <p>�l='W'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_NT    = 'W';
	/** 
	 * OS�^�C�vWindows95. <p>
	 * �����.(�g�莁��CMAGAZINE1991/1�̋L���ŏ��������̂łȂ��Ƃ����Ӗ�)
	 * <p>OSTYPE_MSDOS���g���邱�Ƃ������B<p>
	 * <p>�l='w'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_95    = 'w';
	/** 
	 * OS�^�C�vTOWNSOS. <p>
	 * �����.(�g�莁��CMAGAZINE1991/1�̋L���ŏ��������̂łȂ��Ƃ����Ӗ�)
	 * <p>�l='T'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_TOWNSOS='T';  
	/** 
	 * OS�^�C�vXOSK. <p>
	 * �����.(�g�莁��CMAGAZINE1991/1�̋L���ŏ��������̂łȂ��Ƃ����Ӗ�)
	 * <p>�l='X'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_XOSK  = 'X';  

	/** 
	 * OS�^�C�vGENERIC(�ėp/���w��). <p>
	 * �������ݎ���OSTYPE�͂��̒l���f�t�H���g(�\��).<p>
	 * <p>�l='\0'<p>
	 * @see LhaEntry#getOSType() 
	 * @see LhaEntry#setOSType(char)
	 */
	public static final char OSTYPE_GENERIC = '\0';

	/** 
	 * ���k�`��lh0(�����k). <p>
	 * �l="-lh0-"<p>
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LH0 = "-lh0-";

	/** 
	 * ���k�`��lh1. <p>
	 * �l="-lh1-"<p>
     * 4k sliding dictionary(max 60 bytes) + dynamic Huffman
     * + fixed encoding of position.
     *
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LH1 = "-lh1-";

	/** 
	 * ���k�`��lh2. <p>
	 * �l="-lh2-"<p>
	 * 8k sliding dictionary(max 256 bytes) + dynamic Huffman
	 * <p>
	 * <STRONG>���ݖ��T�|�[�g</STRONG>
	 * <p>
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LH2 = "-lh2-";

	/** 
	 * ���k�`��lh3. <p>
	 * �l="-lh3-"<p>
	 * 8k sliding dictionary(max 256 bytes) + static Huffman
	 + <p>
	 * <STRONG>���ݖ��T�|�[�g</STRONG>
	 * <p>
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LH3 = "-lh3-";

	/** 
	 * ���k�`��lh4. <p>
	 * �l="-lh4-"<p>
	 * 4k sliding dictionary(max 256 bytes) + static Huffman
	 *  + improved encoding of position and trees
	 *
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LH4 = "-lh4-";
              

	/** 
	 * ���k�`��lh5. <p>
	 * �l="-lh5-"<p>
     * 8k sliding dictionary(max 256 bytes) + static Huffman
     *         + improved encoding of position and trees
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LH5 = "-lh5-";

	/** 
	 * ���k�`��lh6. <p>
	 * �l="-lh6-"<p>
     *  32k sliding dictionary(max 256 bytes) + static Huffman
	 *  + improved encoding of position and trees
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LH6 = "-lh6-";

	/** 
	 * ���k�`��lh7. <p>
	 * �l="-lh7-"<p>
     *  64k sliding dictionary(max 256 bytes) + static Huffman
	 *	  + improved encoding of position and trees.
     *
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LH7 = "-lh7-";

	/** 
	 * ���k�`��lzs. <p>
	 *"-lzs-"<p>
	 * 2k sliding dictionary(max 17 bytes)
	 * <p>
	 * <STRONG>���ݖ��T�|�[�g</STRONG>
	 * <p>
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LZS = "-lzs-";

	/** 
	 * ���k�`��lz4(�����k). <p>
	 * �l="-lz4-"<p>
	 * no compression
	 * <p>
	 * <STRONG>���ݖ��T�|�[�g</STRONG>
	 * <p>
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LZ4 = "-lz4-";

	/** 
	 * ���k�`��lz5. <p>
	 * �l="-lz5-"<p>
	 * 4k sliding dictionary(max 17 bytes)
	 * <p>
	 * <STRONG>���ݖ��T�|�[�g</STRONG>
	 * <p>
	 * @see LhaEntry#getCompressMethod() 
	 * @see LhaEntry#setCompressMethod(String)
	 */
	public static final String METHOD_LZ5 = "-lz5-";

}
