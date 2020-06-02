#pragma once
#include "AGButton.h"
#include "afxlistctrl.h"
#include "afxwin.h"


// CSEIDlg 对话框

class CSEIDlg : public CDialogEx
{
	DECLARE_DYNAMIC(CSEIDlg)

public:
	CSEIDlg(CWnd* pParent = NULL);   // 标准构造函数
	virtual ~CSEIDlg();

// 对话框数据
	enum { IDD = IDD_SEIPUSH_DIALOG };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持
	virtual BOOL OnInitDialog();

	afx_msg void OnPaint();
	afx_msg void OnShowWindow(BOOL bShow, UINT nStatus);
	afx_msg void OnBnClickedOk();
	afx_msg void OnBnClickedApply();
	afx_msg void OnBnClickedSet();
	afx_msg void OnLvnItemchangedLstinfoSei(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnBnClickedCkenableSei();

	DECLARE_MESSAGE_MAP()

	void InitCtrls();
	void DrawClient(CDC *lpDC);

private:
	CMFCListCtrl	m_ctrUIDInfo;

	CAGButton		m_btnSet;
	CAGButton		m_btnCancel;
	CAGButton		m_btnConfirm;
	CAGButton		m_btnApply;
	CButton			m_ckSEI;

	CFont			m_ftDes;
	CFont			m_ftBtn;		// button
	
	UINT m_nSEIIndex;
	UINT m_nSEIPosX;
	UINT m_nSEIPosY;
	UINT m_nSEIWidth;
	UINT m_nSEIHeight;
};
