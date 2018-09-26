// CodecDlg.cpp : 实现文件
//

#include "stdafx.h"
#include "AgoraVideoCall.h"
#include "CodecDlg.h"
#include "afxdialogex.h"


// CCodecDlg 对话框

IMPLEMENT_DYNAMIC(CCodecDlg, CDialogEx)

CCodecDlg::CCodecDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CCodecDlg::IDD, pParent)
{

}

CCodecDlg::~CCodecDlg()
{
}

void CCodecDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_BTNCONFIRM_SETUP, m_btnConfirm);
}


BEGIN_MESSAGE_MAP(CCodecDlg, CDialogEx)
	ON_BN_CLICKED(IDC_BTNCONFIRM_SETUP, &CCodecDlg::OnBnClickedBtnconfirmSetup)

	ON_WM_PAINT()
END_MESSAGE_MAP()


// CCodecDlg 消息处理程序


BOOL CCodecDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// TODO:  在此添加额外的初始化
	m_ftHead.CreateFont(18, 0, 0, 0, FW_BOLD, FALSE, FALSE, 0, ANSI_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, DEFAULT_PITCH | FF_SWISS, _T("Arial"));
	m_ftDes.CreateFont(20, 0, 0, 0, FW_BOLD, FALSE, FALSE, 0, ANSI_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, DEFAULT_PITCH | FF_SWISS, _T("Arial"));
	m_ftBtn.CreateFont(22, 0, 0, 0, FW_BOLD, FALSE, FALSE, 0, ANSI_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, DEFAULT_PITCH | FF_SWISS, _T("Arial"));

	m_cbxCodec.Create(WS_CHILD | WS_VISIBLE | CBS_DROPDOWNLIST | CBS_OWNERDRAWVARIABLE, CRect(0, 0, 300, 40), this, IDC_CMBCODEC_SETUP);
	SetBackgroundColor(RGB(0xFF, 0xFF, 0xFF), TRUE);
	InitCtrls();

	return TRUE;  // return TRUE unless you set the focus to a control
	// 异常:  OCX 属性页应返回 FALSE
}

void CCodecDlg::OnBnClickedBtnconfirmSetup()
{
	// TODO:  在此添加控件通知处理程序代码
	GetParent()->SendMessage(WM_GOBACK, 0, 0);
}

void CCodecDlg::InitCtrls()
{
	CRect ClientRect;

	MoveWindow(0, 0, 320, 380, 1);
	GetClientRect(&ClientRect);

	m_cbxCodec.MoveWindow(10, ClientRect.Height() - 345, 300, 40, TRUE);
	m_btnConfirm.MoveWindow(10, ClientRect.Height() - 58, 300, 48, TRUE);

	m_cbxCodec.SetButtonImage(IDB_CMBBTN, 40, 40, RGB(0xFF, 0x00, 0xFF));
	m_cbxCodec.InsertString(0, _T("e264"));
	m_cbxCodec.InsertString(1, _T("evp"));
	m_cbxCodec.InsertString(2, _T("vp8"));
	m_cbxCodec.SetListMaxHeight(120);
	m_cbxCodec.SetCurSel(0);
	m_cbxCodec.SetFont(&m_ftDes);

	m_btnConfirm.SetBackColor(RGB(0, 160, 239), RGB(0, 160, 239), RGB(0, 160, 239), RGB(192, 192, 192));
	m_btnConfirm.SetFont(&m_ftBtn);
	m_btnConfirm.SetTextColor(RGB(0xFF, 0xFF, 0xFF), RGB(0xFF, 0xC8, 0x64), RGB(0xFF, 0xC8, 0x64), RGB(0xCC, 0xCC, 0xCC));
	m_btnConfirm.SetWindowText(LANG_STR("IDS_SET_BTCONFIRM"));
}


void CCodecDlg::DrawClient(CDC *lpDC)
{
	CRect	rcText;
	CRect	rcClient;
	LPCTSTR lpString = NULL;

	GetClientRect(&rcClient);

	CFont* defFont = lpDC->SelectObject(&m_ftHead);
	lpDC->SetBkColor(RGB(0xFF, 0xFF, 0xFF));
	lpDC->SetTextColor(RGB(0x00, 0x9E, 0xEB));
	lpString = LANG_STR("IDS_CODEC_TITLE");
	lpDC->TextOut(12, 5, lpString, _tcslen(lpString));
	
	lpDC->SelectObject(defFont);
}


void CCodecDlg::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO:  在此处添加消息处理程序代码
	// 不为绘图消息调用 CDialogEx::OnPaint()
	DrawClient(&dc);
}

int CCodecDlg::GetCodecType()
{
	return m_cbxCodec.GetCurSel();
}