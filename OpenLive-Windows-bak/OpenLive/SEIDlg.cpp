// SEIDlg.cpp : implement file
//

#include "stdafx.h"
#include "OpenLive.h"
#include "SEIDlg.h"
#include "afxdialogex.h"


// CSEIDlg dialog

IMPLEMENT_DYNAMIC(CSEIDlg, CDialogEx)

CSEIDlg::CSEIDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CSEIDlg::IDD, pParent)
	, m_nSEIIndex(0)
	, m_nSEIPosX(0)
{

}

CSEIDlg::~CSEIDlg()
{
}

void CSEIDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_LSTINFO_SEI, m_ctrUIDInfo);

	DDX_Control(pDX, IDCANCEL, m_btnCancel);
	DDX_Control(pDX, IDOK, m_btnConfirm);
	DDX_Control(pDX, IDC_APPLY, m_btnApply);
	DDX_Control(pDX, IDC_BTSET_SEI, m_btnSet);

	DDX_Text(pDX, IDC_EDINDEX_SEI, m_nSEIIndex);
	DDX_Text(pDX, IDC_EDXPOS_SEI, m_nSEIPosX);
	DDX_Text(pDX, IDC_EDYPOS_SEI, m_nSEIPosY);
	DDX_Text(pDX, IDC_EDWIDTH_SEI, m_nSEIWidth);
	DDX_Text(pDX, IDC_EDHEIGHT_SEI, m_nSEIHeight);
	DDX_Control(pDX, IDC_CKENABLE_SEI, m_ckSEI);
}


BEGIN_MESSAGE_MAP(CSEIDlg, CDialogEx)
	ON_BN_CLICKED(IDC_APPLY, &CSEIDlg::OnBnClickedApply)
	ON_BN_CLICKED(IDOK, &CSEIDlg::OnBnClickedOk)
	ON_BN_CLICKED(IDC_BTSET_SEI, &CSEIDlg::OnBnClickedSet)
	ON_WM_SHOWWINDOW()
	ON_WM_PAINT()
	ON_NOTIFY(LVN_ITEMCHANGED, IDC_LSTINFO_SEI, &CSEIDlg::OnLvnItemchangedLstinfoSei)
	ON_BN_CLICKED(IDC_CKENABLE_SEI, &CSEIDlg::OnBnClickedCkenableSei)
END_MESSAGE_MAP()


// CSEIDlg message deal with app


BOOL CSEIDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// TODO:  Add code to initialize
	InitCtrls();

	return TRUE;  // return TRUE unless you set the focus to a control
	// error:  OCX attribute page return FALSE
}


void CSEIDlg::OnBnClickedApply()
{
	// TODO:  Add control notification handler code here
	UpdateData(TRUE);

	CAgoraObject *lpAgoraObject = CAgoraObject::GetAgoraObject();

	lpAgoraObject->EnableSEIPush(m_ckSEI.GetCheck(), RGB(0xFF, 0, 0));
}


void CSEIDlg::OnBnClickedOk()
{
	// TODO: Add control notification handler code here
	CDialogEx::OnOK();

	CAgoraObject *lpAgoraObject = CAgoraObject::GetAgoraObject();

	lpAgoraObject->EnableSEIPush(m_ckSEI.GetCheck(), RGB(0xFF, 0, 0));

}


void CSEIDlg::OnShowWindow(BOOL bShow, UINT nStatus)
{
	CDialogEx::OnShowWindow(bShow, nStatus);

	// TODO:  Add control notification handler code here
	if (!bShow)
		return;

	CAgoraObject *lpAgoraObject = CAgoraObject::GetAgoraObject();

	int nListCount = m_ctrUIDInfo.GetItemCount();
	int nVideoCount = lpAgoraObject->GetSEICount();
	SEI_INFO	seiInfo;
	CString		strInfo;

	if (nListCount > nVideoCount) {
		for (int nIndex = 0; nIndex < nListCount - nVideoCount; nIndex++)
			m_ctrUIDInfo.DeleteItem(0);
	}

	for (int nIndex = 0; nIndex < nVideoCount; nIndex++) {

		lpAgoraObject->GetSEIInfoByIndex(nIndex, &seiInfo);
		strInfo.Format(_T("%08X"), seiInfo.nUID);
		if (nIndex >= nListCount)
			m_ctrUIDInfo.InsertItem(nIndex, strInfo);
		else
			m_ctrUIDInfo.SetItemText(nIndex, 0, strInfo);

		strInfo.Format(_T("%d"), seiInfo.nIndex);
		m_ctrUIDInfo.SetItemText(nIndex, 1, strInfo);

		strInfo.Format(_T("(%d, %d)%d*%d"), seiInfo.x, seiInfo.y, seiInfo.nWidth, seiInfo.nHeight);
		m_ctrUIDInfo.SetItemText(nIndex, 2, strInfo);
		m_ctrUIDInfo.SetItemData(nIndex, seiInfo.nUID);
	}
}


void CSEIDlg::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO:  Add control notification handler code here
	//  not call CDialogEx::OnPaint() for paint message

	DrawClient(&dc);
}

void CSEIDlg::InitCtrls()
{
	CRect ClientRect;

	SetBackgroundColor(RGB(0xFF, 0xFF, 0xFF), TRUE);

	m_ftDes.CreateFont(15, 0, 0, 0, FW_NORMAL, FALSE, FALSE, 0, ANSI_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, DEFAULT_PITCH | FF_SWISS, _T("Arial"));
	m_ftBtn.CreateFont(16, 0, 0, 0, FW_NORMAL, FALSE, FALSE, 0, ANSI_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, DEFAULT_PITCH | FF_SWISS, _T("Arial"));

	GetClientRect(&ClientRect);

	m_ctrUIDInfo.SetExtendedStyle(LVS_EX_FULLROWSELECT | LVS_EX_GRIDLINES | LVS_EX_INFOTIP);
	m_ctrUIDInfo.InsertColumn(0, _T("UID"), 0, 60, 0);
	m_ctrUIDInfo.InsertColumn(1, _T("Index"), 0, 60, 1);
	m_ctrUIDInfo.InsertColumn(2, _T("SEIInfo"), 0, 120, 2);

//	m_btnCancel.MoveWindow(10, ClientRect.Height() - 50, 100, 30, TRUE);

//	m_btnConfirm.MoveWindow(150, ClientRect.Height() - 50, 100, 30, TRUE);

//	m_ckSEI.MoveWindow(320, ClientRect.Height() - 123, 100, 30, TRUE);

//	m_btnSet.MoveWindow(320, ClientRect.Height() - 90, 60, 20, TRUE);
	m_btnSet.SetBackColor(RGB(0x00, 0xA0, 0xE9), RGB(0x05, 0x78, 0xAA), RGB(0x05, 0x78, 0xAA), RGB(0xE6, 0xE6, 0xE6));

//	m_btnApply.MoveWindow(290, ClientRect.Height() - 50, 100, 30, TRUE);

	m_btnCancel.SetBorderColor(RGB(0xD8, 0xD8, 0xD8), RGB(0x00, 0xA0, 0xE9), RGB(0x00, 0xA0, 0xE9), RGB(0xCC, 0xCC, 0xCC));
	m_btnCancel.SetBackColor(RGB(0xFF, 0xFF, 0xFF), RGB(0xFF, 0xFF, 0xFF), RGB(0xFF, 0xFF, 0xFF), RGB(0xFF, 0xFF, 0xFF));
	m_btnCancel.SetFont(&m_ftBtn);
	m_btnCancel.SetTextColor(RGB(0x55, 0x58, 0x5A), RGB(0x00, 0xA0, 0xE9), RGB(0x00, 0xA0, 0xE9), RGB(0xCC, 0xCC, 0xCC));
	m_btnCancel.SetWindowText(LANG_STR("IDS_DEVICE_CANCEL"));

	m_btnConfirm.SetBackColor(RGB(0x00, 0xA0, 0xE9), RGB(0x05, 0x78, 0xAA), RGB(0x05, 0x78, 0xAA), RGB(0xE6, 0xE6, 0xE6));
	m_btnConfirm.SetFont(&m_ftBtn);
	m_btnConfirm.SetTextColor(RGB(0xFF, 0xFF, 0xFF), RGB(0xFF, 0xFF, 0xFF), RGB(0xFF, 0xFF, 0xFF), RGB(0xCC, 0xCC, 0xCC));
	m_btnConfirm.SetWindowText(LANG_STR("IDS_DEVICE_CONFIRM"));

	m_btnApply.SetBackColor(RGB(0x00, 0xA0, 0xE9), RGB(0x05, 0x78, 0xAA), RGB(0x05, 0x78, 0xAA), RGB(0xE6, 0xE6, 0xE6));
	m_btnApply.SetFont(&m_ftBtn);
	m_btnApply.SetTextColor(RGB(0xFF, 0xFF, 0xFF), RGB(0xFF, 0xFF, 0xFF), RGB(0xFF, 0xFF, 0xFF), RGB(0xCC, 0xCC, 0xCC));
	m_btnApply.SetWindowText(LANG_STR("IDS_DEVICE_APPLY"));
}

void CSEIDlg::DrawClient(CDC *lpDC)
{
	LPCTSTR lpString = NULL;

	CFont* defFont = lpDC->SelectObject(&m_ftDes);
	lpDC->SetBkColor(RGB(0xFF, 0xFF, 0xFF));
	lpDC->SetTextColor(RGB(0x00, 0x00, 0x00));

	lpString = LANG_STR("IDS_SEI_INDEX");
	lpDC->TextOut(370, 40, lpString, _tcslen(lpString));

	lpString = LANG_STR("IDS_SEI_XPOS");
	lpDC->TextOut(370, 80, lpString, _tcslen(lpString));

	lpString = LANG_STR("IDS_SEI_YPOS");
	lpDC->TextOut(370, 120, lpString, _tcslen(lpString));

	lpString = LANG_STR("IDS_SEI_WIDTH");
	lpDC->TextOut(370, 160, lpString, _tcslen(lpString));

	lpString = LANG_STR("IDS_SEI_HEIGHT");
	lpDC->TextOut(370, 200, lpString, _tcslen(lpString));
}

void CSEIDlg::OnBnClickedSet()
{
	// TODO:  Add control notification handler code here
	CAgoraObject *lpObject = CAgoraObject::GetAgoraObject();
	
	int nSel = m_ctrUIDInfo.GetSelectionMark();
	if (nSel == -1)
		return;

	UINT nUID = m_ctrUIDInfo.GetItemData(nSel);

	SEI_INFO	seiInfo;
	CString		strInfo;

	UpdateData(TRUE);
	seiInfo.nIndex = m_nSEIIndex;
	seiInfo.x = m_nSEIPosX;
	seiInfo.y = m_nSEIPosY;
	seiInfo.nWidth = m_nSEIWidth;
	seiInfo.nHeight = m_nSEIHeight;
	seiInfo.nUID = nUID;

	lpObject->SetSEIInfo(nUID, &seiInfo);

	strInfo.Format(_T("%u"), seiInfo.nUID);
	m_ctrUIDInfo.SetItemText(nSel, 0, strInfo);

	strInfo.Format(_T("%d"), seiInfo.nIndex);
	m_ctrUIDInfo.SetItemText(nSel, 1, strInfo);

	strInfo.Format(_T("(%d, %d)%d*%d"), seiInfo.x, seiInfo.y, seiInfo.nWidth, seiInfo.nHeight);
	m_ctrUIDInfo.SetItemText(nSel, 2, strInfo);
	m_ctrUIDInfo.SetItemData(nSel, seiInfo.nUID);
}


void CSEIDlg::OnLvnItemchangedLstinfoSei(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMLISTVIEW pNMLV = reinterpret_cast<LPNMLISTVIEW>(pNMHDR);
	// TODO:  Add control notification handler code here
	*pResult = 0;

	int nSel = m_ctrUIDInfo.GetSelectionMark();
	if (nSel == -1)
		return;

	CAgoraObject *lpObject = CAgoraObject::GetAgoraObject();
	UINT nUID = m_ctrUIDInfo.GetItemData(nSel);

	SEI_INFO seiInfo;
	
	if (!lpObject->GetSEIInfo(nUID, &seiInfo))
		return;

	m_nSEIIndex = seiInfo.nIndex;
	m_nSEIPosX = seiInfo.x;
	m_nSEIPosY = seiInfo.y;
	m_nSEIWidth = seiInfo.nWidth;
	m_nSEIHeight = seiInfo.nHeight;

	UpdateData(FALSE);
}


void CSEIDlg::OnBnClickedCkenableSei()
{
	// TODO:  Add control notification handler code here
	CAgoraObject *lpAgoraObject = CAgoraObject::GetAgoraObject();

	UINT nSelfID = CAgoraObject::GetAgoraObject()->GetSelfUID();
	BOOL bEnable = m_ckSEI.GetCheck();

	UpdateData(TRUE);

//	lpAgoraObject->EnableSEIPush(bEnable, m_nSEIWidth, m_nSEIHeight, RGB(0xFF, 0, 0));
}
