#pragma once
class CAGConfig
{
public:
	CAGConfig();
	~CAGConfig();

	int GetSolution();
	BOOL SetSolution(int nCodec);

	BOOL EnableAutoSave(BOOL bEnable);
	BOOL IsAutoSaveEnabled();

private:
	TCHAR m_szConfigFile[MAX_PATH];
};

