/*
 * $RCSfile: TreeNode.java,v $ $Date: 2000/05/04 15:43:23 $ 
 * Ver. $Name:  $  Source revision. $Revision: 1.2 $
 *
 * Copyright 2000 by TURNER.
 */

package jp.gr.java_conf.turner.util.lha;

/**
 * �n�t�}���c���[�p�̃m�[�h.
 */
abstract class TreeNode
{
	Branch parent = null;//�e(DynamicHuffman�Ŏg�p)
	int freq = 0;       //�n�t�}�������̕p�x�J�E���^(DynamicHuffman�Ŏg�p)
	int index = 0;      //�p�x���Ń\�[�g���ꂽ���̃C���f�b�N�X
	                    //��(DynamicHuffman�Ŏg�p)
}
