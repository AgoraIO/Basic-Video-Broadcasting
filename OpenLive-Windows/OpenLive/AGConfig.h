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

    BOOL IsCustomFPS();
    BOOL IsCustomBitrate();
    BOOL IsCustomRsolution();
   
    void SetCustomFPS(int fps);
    int  GetCustomFPS();

    void SetResolution(int w, int h);
    BOOL GetResolution(int& w, int& h);
  
    void SetCustomBitrate(int bitrate);
    int  GetCustomBitrate();

private:
	TCHAR m_szConfigFile[MAX_PATH];
};

