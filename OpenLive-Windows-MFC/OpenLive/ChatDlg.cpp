// ChatDlg.cpp : Implement file
//

#include "stdafx.h"
#include "OpenLive.h"
#include "ChatDlg.h"
#include "afxdialogex.h"


// CChatDlg dialog

IMPLEMENT_DYNAMIC(CChatDlg, CDialogEx)

CChatDlg::CChatDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CChatDlg::IDD, pParent)
{

}

CChatDlg::~CChatDlg()
{
}

void CChatDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_EDCHAT_MESSAGE, m_edtChatBox);
}


BEGIN_MESSAGE_MAP(CChatDlg, CDialogEx)
	ON_WM_SIZE()
	ON_WM_PAINT()
END_MESSAGE_MAP()


// CChatDlg Message deal with app
BOOL CChatDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// TODO:
	LONG lExStyle = ::GetWindowLong(GetSafeHwnd(), GWL_EXSTYLE);
	::SetWindowLong(GetSafeHwnd(), GWL_EXSTYLE, lExStyle | WS_EX_LAYERED);

	SetBackgroundColor(RGB(0, 0, 0));
	SetLayeredWindowAttributes(0, 130, LWA_ALPHA);
//	m_nStreamID = CAgoraObject::GetAgoraObject()->CreateMessageStream();

	return TRUE;  // return TRUE unless you set the focus to a control
	// Error:  OCX Attribute page return  FALSE
}

void CChatDlg::OnSize(UINT nType, int cx, int cy)
{
	CDialogEx::OnSize(nType, cx, cy);

	// TODO: 
	if (::IsWindow(m_edtChatBox.GetSafeHwnd()))
		m_edtChatBox.MoveWindow(1, cy - 21, cx - 2, 20);
}


BOOL CChatDlg::PreTranslateMessage(MSG* pMsg)
{
	// TODO:
	CString str;

	if (pMsg->message == WM_KEYDOWN){
		if (pMsg->wParam == VK_RETURN) {
			m_edtChatBox.GetWindowText(str);
			if (str.GetLength() > 0) {
//				CAgoraObject::GetAgoraObject()->SendChatMessage(m_nStreamID, str);
				AddChatMessage(0, str);
				m_edtChatBox.SetWindowText(_T(""));
			}
		}

		return FALSE;
	}

	return CDialogEx::PreTranslateMessage(pMsg);
}


void CChatDlg::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO: 

	int y = 100;

	POSITION pos = m_strMsgList.GetHeadPosition();
	dc.SetTextColor(RGB(0xFF, 0xFF, 0xFF));
	dc.SetBkColor(RGB(0, 0, 0));

	while (pos != NULL) {
		CString str = m_strMsgList.GetNext(pos);
		dc.TextOut(20, y, str);
		y -= 20;
	}
}

void CChatDlg::AddChatMessage(UINT nUID, LPCTSTR lpMessage)
{
	CString str;

	if (nUID != 0)
		str.Format(_T("%d: %s"), nUID, lpMessage);
	else
		str.Format(_T("me: %s"), lpMessage);

	if (m_strMsgList.GetCount() >= 5)
		m_strMsgList.RemoveTail();

	m_strMsgList.AddHead(str);

	Invalidate(TRUE);
}

void CChatDlg::ClearHistory()
{
	m_strMsgList.RemoveAll();

	Invalidate(TRUE);
}