
// OpenLiveDlg.cpp : implementation file
//

#include "stdafx.h"
#include "OpenLive.h"
#include "OpenLiveDlg.h"
#include "afxdialogex.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CAboutDlg dialog used for App About

class CAboutDlg : public CDialogEx
{
public:
	CAboutDlg();

// Dialog Data
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

// Implementation
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialogEx(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialogEx)
END_MESSAGE_MAP()


// COpenLiveDlg dialog



COpenLiveDlg::COpenLiveDlg(CWnd* pParent /*=NULL*/)
    : CDialogEx(COpenLiveDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);

    m_nVideoProfile = 0;
	m_lpAgoraObject = NULL;
	m_lpRtcEngine = NULL;

	m_nNetworkQuality = 0;
}

void COpenLiveDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_BTNMIN, m_btnMin);
	DDX_Control(pDX, IDC_BTNCLOSE, m_btnClose);

	DDX_Control(pDX, IDC_LINKAGORA, m_linkAgora);
}

BEGIN_MESSAGE_MAP(COpenLiveDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_WM_NCHITTEST()
    ON_MESSAGE(WM_GOBACK, &COpenLiveDlg::OnBackPage)
    ON_MESSAGE(WM_GONEXT, &COpenLiveDlg::OnNextPage)
    ON_MESSAGE(WM_JOINCHANNEL, &COpenLiveDlg::OnJoinChannel)
    ON_MESSAGE(WM_LEAVECHANNEL, &COpenLiveDlg::OnLeaveChannel)
	
    ON_BN_CLICKED(IDC_BTNMIN, &COpenLiveDlg::OnBnClickedBtnmin)
    ON_BN_CLICKED(IDC_BTNCLOSE, &COpenLiveDlg::OnBnClickedBtnclose)

    ON_MESSAGE(WM_MSGID(EID_NETWORK_QUALITY), &COpenLiveDlg::OnNetworkQuality)
	
	ON_MESSAGE(WM_MSGID(EID_APICALL_EXECUTED), &COpenLiveDlg::OnEIDApiExecuted)
	ON_MESSAGE(WM_MSGID(EID_ERROR), &COpenLiveDlg::OnEIDError)

	ON_MESSAGE(WM_MSGID(EID_LEAVE_CHANNEL), &COpenLiveDlg::OnEIDLeaveChannel)
	ON_MESSAGE(WM_MSGID(EID_LASTMILE_QUALITY), &COpenLiveDlg::OnLastMileQuality)
    ON_WM_CLOSE()
END_MESSAGE_MAP()


// COpenLiveDlg message handlers
BOOL COpenLiveDlg::PreTranslateMessage(MSG* pMsg)
{
	if (pMsg->message == WM_KEYDOWN){
		switch (pMsg->wParam){
		case VK_ESCAPE:
		case VK_RETURN:
			return FALSE;
		}
	}

	return CDialogEx::PreTranslateMessage(pMsg);
}

BOOL COpenLiveDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// Add "About..." menu item to system menu.

	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		BOOL bNameValid;
		CString strAboutMenu;
		bNameValid = strAboutMenu.LoadString(IDS_ABOUTBOX);
		ASSERT(bNameValid);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	// TODO: Add extra initialization here
	m_ftTitle.CreateFont(16, 0, 0, 0, FW_BOLD, FALSE, FALSE, 0, ANSI_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, DEFAULT_PITCH | FF_SWISS, _T("Arial"));
	m_ftLink.CreateFont(16, 0, 0, 0, FW_BOLD, FALSE, TRUE, 0, ANSI_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, DEFAULT_PITCH | FF_SWISS, _T("Arial"));
	m_ftVer.CreateFont(16, 0, 0, 0, FW_NORMAL, FALSE, FALSE, 0, ANSI_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, DEFAULT_PITCH | FF_SWISS, _T("Arial"));

	CString strAppID = CAgoraObject::LoadAppID();

	m_lpAgoraObject = CAgoraObject::GetAgoraObject(strAppID);
	m_lpRtcEngine = CAgoraObject::GetEngine();
   

	if (strAppID.GetLength() == 0) {
        MessageBox(_T("Please apply your own App ID to AppID int AppID.ini"), _T("Notice"), MB_ICONINFORMATION);
        PostQuitMessage(0);
    }

	m_lpAgoraObject->SetLogFilePath(NULL);
	m_lpAgoraObject->SetMsgHandlerWnd(GetSafeHwnd());
	CAgoraObject::GetEngine()->setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
	CAgoraObject::GetAgoraObject()->EnableVideo(TRUE);
	CAgoraObject::GetAgoraObject()->SetClientRole(CLIENT_ROLE_BROADCASTER);
	CAgoraObject::GetAgoraObject()->EnableLastmileTest(TRUE);
	SetBackgroundImage(IDB_DLG_MAIN);
	InitCtrls();
	InitChildDialog();

	return TRUE;  // return TRUE  unless you set the focus to a control
}

void COpenLiveDlg::InitCtrls()
{
	CRect ClientRect;
	CBitmap	bmpNetQuality;

	MoveWindow(0, 0, 720, 600, 1);
	GetClientRect(&ClientRect);

	bmpNetQuality.LoadBitmap(IDB_NETWORK_QUALITY);

	m_imgNetQuality.Create(32, 32, ILC_COLOR24 | ILC_MASK, 6, 1);
	m_imgNetQuality.Add(&bmpNetQuality, RGB(0xFF, 0, 0xFF));

	m_btnMin.MoveWindow(ClientRect.Width() - 46, 1, 22, 22, TRUE);
	m_btnClose.MoveWindow(ClientRect.Width() - 23, 1, 22, 22, TRUE);
	m_linkAgora.MoveWindow(ClientRect.Width() / 2 - 30, ClientRect.Height()-55, 80, 20, TRUE);

	m_btnMin.SetBackImage(IDB_BTNMIN, RGB(0xFF, 0, 0xFF));
	m_btnClose.SetBackImage(IDB_BTNCLOSE, RGB(0xFF, 0, 0xFF));

	m_linkAgora.SetFont(&m_ftLink);
	m_linkAgora.SetURL(_T("http://www.agora.io"));
	m_linkAgora.SetWindowText(LANG_STR("IDS_LOGO_AGORAWEB"));
	CMFCButton::EnableWindowsTheming(FALSE);
}

void COpenLiveDlg::InitChildDialog()
{
	CString str;

	m_dlgEnterChannel.Create(CEnterChannelDlg::IDD, this);
	m_dlgSetup.Create(CSetupDlg::IDD, this);
	m_dlgVideo.Create(CVideoDlg::IDD, this);

	m_dlgEnterChannel.MoveWindow(110, 70, 500, 450, TRUE);
	m_dlgSetup.MoveWindow(110, 70, 500, 450, TRUE);

	m_dlgEnterChannel.ShowWindow(SW_SHOW);
	m_dlgEnterChannel.SetCtrlPos();
	m_lpCurDialog = &m_dlgEnterChannel;

//    m_dlgSetup.SetVideoSolution(15);
	m_dlgEnterChannel.SetVideoString(m_dlgSetup.GetVideoSolutionDes());
}

void COpenLiveDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialogEx::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void COpenLiveDlg::OnPaint()
{
	CPaintDC dc(this); // device context for painting

	if (IsIconic())
	{
		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		DrawClient(&dc);
//		CDialogEx::OnPaint();
	}
}

// The system calls this function to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR COpenLiveDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}



LRESULT COpenLiveDlg::OnNcHitTest(CPoint point)
{
	// TODO:  在此添加消息处理程序代码和/或调用默认值
	LRESULT lResult = CDialogEx::OnNcHitTest(point);
	if (lResult == HTCLIENT && ::GetAsyncKeyState(MK_LBUTTON) < 0)
			lResult = HTCAPTION;
		
	return lResult;
}

void COpenLiveDlg::DrawClient(CDC *lpDC)
{
	CRect	rcText;
	CRect	rcClient;
	LPCTSTR lpString = NULL;
	CFont* defFont = lpDC->SelectObject(&m_ftTitle);

	m_imgNetQuality.Draw(lpDC, m_nNetworkQuality, CPoint(16, 40), ILD_NORMAL);

	GetClientRect(&rcClient);
	lpDC->SetBkColor(RGB(0x00, 0x9E, 0xEB));
	lpDC->SetTextColor(RGB(0xFF, 0xFF, 0xFF));
	lpString = LANG_STR("IDS_TITLE");
	lpDC->TextOut(12, 3, lpString, _tcslen(lpString));
	
	lpDC->SelectObject(&m_ftVer);
	lpDC->SetTextColor(RGB(0x91, 0x96, 0xA0));
	lpDC->SetBkColor(RGB(0xFF, 0xFF, 0xFF));

	CString strVer = CAgoraObject::GetSDKVersionEx();
	
	rcText.SetRect(0, rcClient.Height() - 30, rcClient.Width(), rcClient.Height() - 5);
	lpDC->DrawText(strVer, strVer.GetLength(), &rcText, DT_CENTER | DT_SINGLELINE);
	lpDC->SelectObject(defFont);
}

void COpenLiveDlg::OnBnClickedBtnmin()
{
	// TODO:  在此添加控件通知处理程序代码
	ShowWindow(SW_MINIMIZE);
}


void COpenLiveDlg::OnBnClickedBtnclose()
{
	CAgoraObject::GetAgoraObject()->CloseAgoraObject();
	// TODO:  在此添加控件通知处理程序代码
	CDialogEx::OnCancel();
}

LRESULT COpenLiveDlg::OnBackPage(WPARAM wParam, LPARAM lParam)
{
	if (m_lpCurDialog == &m_dlgSetup) {
		m_lpCurDialog->ShowWindow(SW_HIDE);
		m_lpCurDialog = &m_dlgEnterChannel;
	}

    m_nVideoProfile = m_dlgSetup.GetVideoSolution();
    m_dlgEnterChannel.SetVideoString(m_dlgSetup.GetVideoSolutionDes());

	m_lpCurDialog->ShowWindow(SW_SHOW);

	return 0;
}

LRESULT COpenLiveDlg::OnNextPage(WPARAM wParam, LPARAM lParam)
{
	m_lpCurDialog->ShowWindow(SW_HIDE);
	if (m_lpCurDialog == &m_dlgEnterChannel)
			m_lpCurDialog = &m_dlgSetup;

	m_lpCurDialog->ShowWindow(SW_SHOW);

	return 0;
}

LRESULT COpenLiveDlg::OnJoinChannel(WPARAM wParam, LPARAM lParam)
{
	IRtcEngine		*lpRtcEngine = CAgoraObject::GetEngine();
	CAgoraObject	*lpAgoraObject = CAgoraObject::GetAgoraObject();

	CString strChannelName = m_dlgEnterChannel.GetChannelName();

	m_dlgVideo.MoveWindow(0, 0, 960, 720, 1);
	m_dlgVideo.ShowWindow(SW_SHOW);
	m_dlgVideo.CenterWindow();

	VideoCanvas vc;

	vc.uid = 0;
	vc.view = m_dlgVideo.GetLocalVideoWnd();
	vc.renderMode = RENDER_MODE_TYPE::RENDER_MODE_FIT;

	//cancel setVideoProfile bitrate since version 2.1.0
	int nVideoSolution = m_dlgSetup.GetVideoSolution();
	//lpRtcEngine->setVideoProfile((VIDEO_PROFILE_TYPE)nVideoSolution, m_dlgSetup.IsWHSwap());
	lpAgoraObject->EnableLastmileTest(FALSE);
	VideoEncoderConfiguration config;
	config.bitrate = m_dlgSetup.GetBirate();
	config.frameRate = (FRAME_RATE)m_dlgSetup.GetFPS();
	SIZE resolution = m_dlgSetup.GetVideoResolution();
	config.dimensions.width = resolution.cx;
	config.dimensions.height = resolution.cy;
	lpRtcEngine->setVideoEncoderConfiguration(config);

	m_dlgVideo.SetWindowText(strChannelName);
	lpRtcEngine->setupLocalVideo(vc);
	lpRtcEngine->startPreview();
	
	lpAgoraObject->JoinChannel(strChannelName);

    lpAgoraObject->SetMsgHandlerWnd(m_dlgVideo.GetSafeHwnd());
    
	return 0;
}

LRESULT COpenLiveDlg::OnLeaveChannel(WPARAM wParam, LPARAM lParam)
{
	CAgoraObject	*lpAgoraObject = CAgoraObject::GetAgoraObject();

	lpAgoraObject->LeaveCahnnel();
	lpAgoraObject->SetMsgHandlerWnd(GetSafeHwnd());
	lpAgoraObject->EnableLastmileTest(TRUE);
	return 0;
}

LRESULT COpenLiveDlg::OnNetworkQuality(WPARAM wParam, LPARAM lParam)
{
	LPAGE_NETWORK_QUALITY lpData = (LPAGE_NETWORK_QUALITY)wParam;

	if (m_nNetworkQuality != lpData->quality) {
		m_nNetworkQuality = lpData->quality;
		InvalidateRect(CRect(16, 40, 48, 72), TRUE);
	}

	delete lpData;
	return 0;
}

void COpenLiveDlg::OnClose()
{
    // TODO:  在此添加消息处理程序代码和/或调用默认值
	CAgoraObject::GetAgoraObject()->CloseAgoraObject();
    CDialogEx::OnClose();
}


LRESULT COpenLiveDlg::OnEIDApiExecuted(WPARAM wParam, LPARAM lParam)
{
	LPAGE_APICALL_EXECUTED lpData = (LPAGE_APICALL_EXECUTED)wParam;

	delete lpData;
	lpData = NULL;
	return 0;
}

LRESULT COpenLiveDlg::OnEIDError(WPARAM wParam, LPARAM lParam)
{
	LPAGE_ERROR lpData = (LPAGE_ERROR)wParam;
	delete[] lpData->msg; lpData->msg = NULL;
	delete lpData;
	lpData = NULL;
	return 0;
}

LRESULT COpenLiveDlg::OnEIDLeaveChannel(WPARAM wParam, LPARAM lParam)
{
	LPAGE_LEAVE_CHANNEL lpData = (LPAGE_LEAVE_CHANNEL)wParam;
	delete lpData;
	lpData = NULL;
	return 0;
}

LRESULT COpenLiveDlg::OnLastMileQuality(WPARAM wParam, LPARAM lParam)
{
	int quality = wParam;

	if (m_nNetworkQuality != quality) {
		m_nNetworkQuality = quality;
		InvalidateRect(CRect(16, 40, 48, 72), TRUE);
	}
	return 0;
}