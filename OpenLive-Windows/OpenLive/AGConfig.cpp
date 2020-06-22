#include "stdafx.h"
#include "AGConfig.h"


CAGConfig::CAGConfig()
{
	::GetModuleFileName(NULL, m_szConfigFile, MAX_PATH);
	LPTSTR lpLastSlash = _tcsrchr(m_szConfigFile, _T('\\')) + 1;
	_tcscpy_s(lpLastSlash, MAX_PATH, _T("VideoConfig.ini"));

	if (::GetFileAttributes(m_szConfigFile) == INVALID_FILE_ATTRIBUTES){
		HANDLE hFile = ::CreateFile(m_szConfigFile, GENERIC_WRITE, FILE_SHARE_READ, NULL, OPEN_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
		::CloseHandle(hFile);
	}
}


CAGConfig::~CAGConfig()
{
}

int CAGConfig::GetSolution()
{
	CString strResolution;

	::GetPrivateProfileString(_T("VideoConfig"), _T("SolutionIndex"), _T("1"), strResolution.GetBuffer(MAX_PATH), MAX_PATH, m_szConfigFile);

	strResolution.ReleaseBuffer();

	return _ttoi(strResolution);
}

BOOL CAGConfig::SetSolution(int nResolution)
{
	CString strResolution;

	strResolution.Format(_T("%d"), nResolution);

	return ::WritePrivateProfileString(_T("VideoConfig"), _T("SolutionIndex"), strResolution, m_szConfigFile);
}

BOOL CAGConfig::EnableAutoSave(BOOL bEnable)
{
	CString strSave;

	if (bEnable)
		strSave = _T("1");
	else
		strSave = _T("0");

	return ::WritePrivateProfileString(_T("VideoConfig"), _T("SaveSetting"), strSave, m_szConfigFile);
}

BOOL CAGConfig::IsAutoSaveEnabled()
{
	CString strSaveSetting;

	::GetPrivateProfileString(_T("VideoConfig"), _T("SaveSetting"), _T("0"), strSaveSetting.GetBuffer(MAX_PATH), MAX_PATH, m_szConfigFile);

	strSaveSetting.ReleaseBuffer();

	return (_ttoi(strSaveSetting) == 1) ? TRUE : FALSE;
}

BOOL CAGConfig::IsCustomFPS()
{
    CString str;

    ::GetPrivateProfileString(_T("VideoConfig"), _T("CustomFPS"), _T("0"), str.GetBuffer(MAX_PATH), MAX_PATH, m_szConfigFile);

    str.ReleaseBuffer();

    return (_ttoi(str) == 1) ? TRUE : FALSE;
}

BOOL CAGConfig::IsCustomBitrate()
{
    CString str;

    ::GetPrivateProfileString(_T("VideoConfig"), _T("CustomBitrate"), _T("0"), str.GetBuffer(MAX_PATH), MAX_PATH, m_szConfigFile);

    str.ReleaseBuffer();

    return (_ttoi(str) == 1) ? TRUE : FALSE;
}

BOOL CAGConfig::IsCustomRsolution()
{
    CString str;

    ::GetPrivateProfileString(_T("VideoConfig"), _T("CustomRsolution"), _T("0"), str.GetBuffer(MAX_PATH), MAX_PATH, m_szConfigFile);
    str.ReleaseBuffer();

    return (_ttoi(str) == 1) ? TRUE : FALSE;
}

void CAGConfig::SetCustomFPS(int fps)
{
    CString str = _itot(fps, NULL, 10);
   
   ::WritePrivateProfileString(_T("VideoConfig"), _T("FPS"), str, m_szConfigFile);
}

int  CAGConfig::GetCustomFPS()
{
    CString str;

    ::GetPrivateProfileString(_T("VideoConfig"), _T("FPS"), _T("15"), str.GetBuffer(MAX_PATH), MAX_PATH, m_szConfigFile);

    str.ReleaseBuffer();

    return _ttoi(str);
}

void CAGConfig::SetResolution(int w, int h)
{

}

BOOL CAGConfig::GetResolution(int& w, int& h)
{
    CString str;
    ::GetPrivateProfileString(_T("VideoConfig"), _T("Width"), _T("640"), str.GetBuffer(MAX_PATH), MAX_PATH, m_szConfigFile);
    w = _ttoi(str);
    ::GetPrivateProfileString(_T("VideoConfig"), _T("Height"), _T("480"), str.GetBuffer(MAX_PATH), MAX_PATH, m_szConfigFile);
    h = _ttoi(str);
    str.ReleaseBuffer();

    return TRUE;
}

void CAGConfig::SetCustomBitrate(int bitrate)
{

}

int  CAGConfig::GetCustomBitrate()
{
    CString str;

    ::GetPrivateProfileString(_T("VideoConfig"), _T("Bitrate"), _T("800"), str.GetBuffer(MAX_PATH), MAX_PATH, m_szConfigFile);

    str.ReleaseBuffer();

    return _ttoi(str);
}