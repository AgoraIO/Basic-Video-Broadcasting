// AGEdit.cpp : implement file
//

#include "stdafx.h"
#include "AGEdit.h"


// CAGEdit

IMPLEMENT_DYNAMIC(CAGEdit, CEdit)

CAGEdit::CAGEdit()
: m_crBorder(RGB(0xD8, 0xD8, 0xD8))
, m_crBack(RGB(0xFF, 0xFF, 0xFF))
, m_crText(RGB(0x00, 0xA0, 0xE9))
, m_crTip(RGB(0xD8, 0xD8, 0xD8))
, m_strTip(_T("ÇëÊäÈëÎÄ±¾"))
, m_bTrackMouseEvent(FALSE)
, m_bOverControl(FALSE)
, m_bEmpty(TRUE)
, m_bTexting(FALSE)
{
	m_penBorder.CreatePen(PS_SOLID, 1, m_crBorder);
	m_brushBack.CreateSolidBrush(m_crBack);
}

CAGEdit::~CAGEdit()
{
	m_penBorder.DeleteObject();
	m_brushBack.DeleteObject();
}


BEGIN_MESSAGE_MAP(CAGEdit, CEdit)
	ON_WM_ERASEBKGND()
	ON_WM_CTLCOLOR_REFLECT()
	ON_WM_MOUSEHOVER()
	ON_WM_MOUSEMOVE()
	ON_WM_MOUSELEAVE()
    ON_WM_SETFOCUS()
    ON_WM_KILLFOCUS()
    ON_WM_CHAR()
    ON_CONTROL_REFLECT(EN_CHANGE, &CAGEdit::OnEnChange)
    ON_WM_CREATE()
END_MESSAGE_MAP()



// CAGEdit message handle
void CAGEdit::SetTip(LPCTSTR lpTip)
{
    m_strTip = lpTip;

    Invalidate(TRUE);
}

void CAGEdit::SetColor(COLORREF crBorder, COLORREF crBack, COLORREF crText, COLORREF crTip)
{
    if (crBorder != m_crBorder){
        m_penBorder.DeleteObject();
        m_crBorder = crBorder;
        m_penBorder.CreatePen(PS_SOLID, 1, m_crBorder);
    }

    if (crBack != m_crBack){
        m_brushBack.DeleteObject();
        m_crBack = crBack;
        m_brushBack.CreateSolidBrush(m_crBack);
    }

	Invalidate();
}

BOOL CAGEdit::OnEraseBkgnd(CDC* pDC)
{
	// TODO: add message handle here or call default value
	CRect rcWindow;
	
	GetWindowRect(&rcWindow);

	CPen *lpDefaultPen = pDC->SelectObject(&m_penBorder);
	pDC->Rectangle(&rcWindow);
	pDC->SelectObject(lpDefaultPen);

	return CEdit::OnEraseBkgnd(pDC);
}

HBRUSH CAGEdit::CtlColor(CDC* pDC, UINT nCtlColor)
{
	// TODO:  update dc any attribute
	pDC->SetBkColor(m_crBack);

    if (m_bEmpty)
        pDC->SetTextColor(m_crTip);
    else
        pDC->SetTextColor(m_crText);

	// TODO:  Non-null brush if the parent's handler should not be called
	return (HBRUSH)m_brushBack;	//Edit box background brush
}

void CAGEdit::OnMouseHover(UINT nFlags, CPoint point)
{
	// TODO:  add message handles code and/ or call default value here

	CEdit::OnMouseHover(nFlags, point);
}


void CAGEdit::OnMouseMove(UINT nFlags, CPoint point)
{
	// TODO:  add message handles code and/ or call default value here
	if (!m_bTrackMouseEvent) {
		TRACKMOUSEEVENT TrackMouseEvent;
		TrackMouseEvent.cbSize = sizeof(TRACKMOUSEEVENT);
		TrackMouseEvent.hwndTrack = GetSafeHwnd();
		TrackMouseEvent.dwFlags = TME_LEAVE | TME_HOVER;
		TrackMouseEvent.dwHoverTime = HOVER_DEFAULT;

		m_bTrackMouseEvent = ::TrackMouseEvent(&TrackMouseEvent);
	}

	CEdit::OnMouseMove(nFlags, point);
}


void CAGEdit::OnMouseLeave()
{
	// TODO:  add message handle code and/ or call default value here
	m_bTrackMouseEvent = FALSE;

	CEdit::OnMouseLeave();
}


void CAGEdit::OnSetFocus(CWnd* pOldWnd)
{
    CEdit::OnSetFocus(pOldWnd);

    // TODO:  
    if (m_bEmpty) {
        m_bTexting = FALSE;
        SetWindowText(_T(""));
    }

}


void CAGEdit::OnKillFocus(CWnd* pNewWnd)
{
    CEdit::OnKillFocus(pNewWnd);

    // TODO:  add message handle code here
    m_bTexting = FALSE;
    if (m_bEmpty)
        SetWindowText(m_strTip);
}


void CAGEdit::OnChar(UINT nChar, UINT nRepCnt, UINT nFlags)
{
    // TODO:  add message handle code and /or call default value here
    m_bTexting = TRUE;

    if (GetWindowTextLength() <= 2)
        Invalidate();

    CEdit::OnChar(nChar, nRepCnt, nFlags);
}


void CAGEdit::OnEnChange()
{
    // TODO: 

    // TODO:  add message handle code here
    if (m_bTexting)
        m_bEmpty = (GetWindowTextLength() == 0) ? TRUE : FALSE;
}


int CAGEdit::OnCreate(LPCREATESTRUCT lpCreateStruct)
{
    if (CEdit::OnCreate(lpCreateStruct) == -1)
        return -1;

    // TODO:  Add your own creation code here
    m_bTexting = FALSE;
    if (m_bEmpty)
        SetWindowText(m_strTip);

    return 0;
}
