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