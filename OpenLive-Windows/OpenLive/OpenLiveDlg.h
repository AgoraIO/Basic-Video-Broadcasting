
// OpenLiveDlg.h : header file
//

#pragma once

#include "AGHyperlink.h"
#include "EnterChannelDlg.h"
#include "SetupDlg.h"
#include "VideoDlg.h"

// COpenLiveDlg dialog
class COpenLiveDlg : public CDialogEx
{
// Construction
public:
    COpenLiveDlg(CWnd* pParent = NULL);	// standard constructor
	~COpenLiveDlg();
// Dialog Data
	enum { IDD = IDD_AGORAVIDEOCALL_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support


// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL PreTranslateMessage(MSG* pMsg);
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	afx_msg LRESULT OnNcHitTest(CPoint point);

	afx_msg void OnBnClickedBtnmin();
	afx_msg void OnBnClickedBtnclose();

	afx_msg LRESULT OnBackPage(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnNextPage(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnJoinChannel(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnLeaveChannel(WPARAM wParam, LPARAM lParam);

	afx_msg LRESULT OnNetworkQuality(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDApiExecuted(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDError(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDLeaveChannel(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnLastMileQuality(WPARAM wParam, LPARAM lParam);
	DECLARE_MESSAGE_MAP()

protected:
	void InitCtrls();
	void DrawClient(CDC *lpDC);
	void InitChildDialog();

private:
	CAGButton		m_btnMin;
	CAGButton		m_btnClose;

	CAGHyperLink	m_linkAgora;

	CDialogEx			*m_lpCurDialog;
	CEnterChannelDlg	m_dlgEnterChannel;
	CSetupDlg			m_dlgSetup;

	CFont		m_ftTitle;
	CFont		m_ftLink;
	CFont		m_ftVer;
	CImageList	m_imgNetQuality;

private:
	CVideoDlg		m_dlgVideo;
	CAgoraObject	*m_lpAgoraObject;
	IRtcEngine		*m_lpRtcEngine;

private:	// data
    int m_nVideoProfile;
	int m_nNetworkQuality;
public:
    afx_msg void OnClose();
};
