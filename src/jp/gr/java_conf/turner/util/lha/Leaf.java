/*
 * $RCSfile: Leaf.java,v $ $Date: 2000/05/04 15:43:22 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.2 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

/**
 * �n�t�}���c���[�p�̗t�i���[�m�[�h�j.
 */
class Leaf extends TreeNode
{
	int code = 0;       //�n�t�}������			(StaticHuffman�Ŏg�p)
	int code_len = 0;   //�n�t�}�������̃r�b�g��(StaticHuffman�Ŏg�p)
	int real_code;      //�{���̃R�[�h
}
