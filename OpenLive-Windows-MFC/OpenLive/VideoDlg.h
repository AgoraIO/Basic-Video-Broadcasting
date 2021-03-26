#pragma once
#include "AGButton.h"
#include "AGVideoWnd.h"
#include "DeviceDlg.h"
#include "ChatDlg.h"
#include "SEIDlg.h"
#include "AGScreenCaptureDlg.h"

// CVideoDlg �Ի���

class CVideoDlg : public CDialogEx
{
	DECLARE_DYNAMIC(CVideoDlg)

public:
	CVideoDlg(CWnd* pParent = NULL);   // ��׼���캯��
	virtual ~CVideoDlg();

// �Ի�������
	enum { IDD = IDD_VIDEO_DIALOG };

	enum { 
		SCREEN_VIDEO1 = 0,	// ����
		SCREEN_VIDEO4,		// 4����
		SCREEN_VIDEOMULTI,	// 1��4С
	};

	HWND GetRemoteVideoWnd(int nIndex);
	HWND GetLocalVideoWnd() { return m_wndLocal.GetSafeHwnd(); };

	void RebindVideoWnd();

	void ShowControlButton(BOOL bShow = TRUE);

	
protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV ֧��
	virtual BOOL OnInitDialog();
	virtual BOOL PreTranslateMessage(MSG* pMsg);

	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg void OnSize(UINT nType, int cx, int cy);
	afx_msg void OnPaint();
	afx_msg void OnShowWindow(BOOL bShow, UINT nStatus);
	afx_msg LRESULT OnNcHitTest(CPoint point);
	afx_msg LRESULT OnShowModeChanged(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnShowBig(WPARAM wParam, LPARAM lParam);

	afx_msg void OnBnClickedBtnmin();
	afx_msg void OnBnClickedBtnclose();
	afx_msg void OnBnClickedBtnrest();

	afx_msg void OnBnClickedBtnmessage();
	afx_msg void OnBnClickedBtnmode();
	afx_msg void OnBnClickedBtnaudio();
	afx_msg void OnBnClickedBtntip();
	afx_msg void OnBnClickedBtnScreenCapture();
	afx_msg void OnBnClickedBtnMore();

	afx_msg void OnBnClickedBtnfullscr();
	afx_msg void OnCbnSelchangeCmbRole();

	afx_msg void OnBnClickedScreenshare();
	afx_msg void OnBnClickedWindowshare();

	afx_msg void OnBnClickedBtnsetup();
	afx_msg void OnBnClickedBtSEIPush();

	// ���ڴ�������Ļص���Ϣ
	afx_msg LRESULT OnEIDJoinChannelSuccess(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDReJoinChannelSuccess(WPARAM wParam, LPARAM lParam);
	
	afx_msg LRESULT OnEIDFirstLocalFrame(WPARAM wParam, LPARAM lParam);
	
	afx_msg LRESULT OnEIDFirstRemoteVideoFrame(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDUserJoined(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDUserOffline(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDConnectionLost(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDVideoDeviceChanged(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnRemoteVideoStat(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnApiCallExecuted(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnWindowShareStart(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnNetworkQuality(WPARAM wParam, LPARAM lParam);

	afx_msg LRESULT OnEIDError(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDAudioQuality(WPARAM wParam, LPARAM lParam);
		afx_msg LRESULT OnEIDAudioDeviceStateChanged(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDVideoDeviceStateChanged(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDLeaveChannel(WPARAM wParam, LPARAM lParam);

	
	afx_msg LRESULT OnEIDUserMuteAudio(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnEIDUserMuteVideo(WPARAM wParam, LPARAM lParam);

	afx_msg LRESULT OnEIDLocalVideoStat(WPARAM wParam, LPARAM lParam);
	
	afx_msg LRESULT OnEIDApiExecuted(WPARAM wParam, LPARAM lParam);

	//EID_ERROR
	//EID_AUDIO_QUALITY
	//EID_LEAVE_CHANNEL

	//EID_AUDIO_DEVICE_STATE_CHANGED
	//EID_VIDEO_DEVICE_STATE_CHANGED
	//EID_FIRST_REMOTE_VIDEO_FRAME
	//EID_USER_MUTE_AUDIO
	//EID_USER_MUTE_VIDEO
	//EID_LOCAL_VIDEO_STAT
	//EID_REFREASH_RCDSRV
	DECLARE_MESSAGE_MAP()

protected:
	BOOL NeedShowSizeBox(CPoint point);
	void EnableSize(BOOL bEnable);
	void CreateScreenShareMenu();
	void CreateMoreMenu();

	void DrawHead(CDC *pDC);

	void InitCtrls();

	void ShowVideo1();
	void ShowVideo4();
	void ShowMulti();

	void ShowButtonsNormal();

	void AdjustButtonsNormal(int cx, int cy);

	void AdjustSizeVideo1(int cx, int cy);
	void AdjustSizeVideo4(int cx, int cy);
	void AdjustSizeVideoMulti(int cx, int cy);

private:
	CBrush			m_brHead;
	CFont			m_ftDes;

	CAGButton		m_btnMin;
	CAGButton		m_btnRst;
	CAGButton		m_btnClose;
	
//	CAGButton		m_btnSetup;
	
	CAGButton		m_btnMode;
	CAGButton		m_btnAudio;
	CAGButton		m_btnEndCall;
	CComboBox		m_cbxRole;
	CAGButton		m_btnShow;
	CAGButton		m_btnTip;
	CAGButton		m_btnScrCap;

	CAGButton       m_btnMore;

	BOOL			m_bLastStat;
	UINT			m_nScreenMode;
	UINT			m_nBigShowedUID;
	
	CAGVideoWnd		m_wndLocal;
	CAGVideoWnd		m_wndVideo[4];
	CAGVideoWnd		*m_lpBigShowed;

	CDeviceDlg		m_dlgDevice;
	CChatDlg        m_dlgChat;
	CSEIDlg			m_dlgSEI;

	CRect			m_rcVideoArea;
	CRect			m_rcChildVideoArea;

	CBitmap         m_bitMenuDevice;
	CBitmap			m_bitMenuSEI;

	CAGScreenCaptureDlg	m_dlgScreenCapture;

private:	// data	

	typedef struct _AGVIDEO_WNDINFO
	{
		UINT	nUID;
		int		nIndex;

		UINT	nWidth;
		UINT	nHeight;
		int		nBitrate;
		int		nFramerate;
		int		nCodec;

	} AGVIDEO_WNDINFO, *PAGVIDEO_WNDINFO, *LPAGVIDEO_WNDINFO;

	CList<AGVIDEO_WNDINFO>	m_listWndInfo;

	BOOL			m_bRecording;
	BOOL			m_bFullScreen;
	int				m_nTimeCounter;
};
