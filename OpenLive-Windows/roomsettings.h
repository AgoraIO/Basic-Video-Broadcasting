#ifndef ROOMSETTINGS_H
#define ROOMSETTINGS_H

#include "mainwindow.h"
#include <QMainWindow>
#include <memory>

namespace Ui {
class roomsettings;
}

class roomsettings : public QMainWindow
{
    Q_OBJECT

public:
    explicit roomsettings(QWidget *parent = 0);
    explicit roomsettings(QMainWindow* pLastWnd,QWidget *parent = 0);
    ~roomsettings();

    void initWindow(const QString& qsChannel);

private slots:
    void OnClickLastPage();
    void OnOptAudio();
    void OnOptVideo();
	void OnOptBeauty();
    void OnCbVPIndexChanged();

    void on_cbVideoProfile_activated(const QString &arg1);
    void on_cbRecordDevices_activated(int index);
    void on_cbVideoDevices_activated(int index);
    void on_cbPlayDevices_activated(int index);

	void on_cbContrastLevel_activated(int index);
	void on_valueChanged_horizontalSlider_Redness(int value);
	void on_valueChanged_horizontalSlider_Smoothness(int value);
	void on_valueChanged_horizontalSlider_Lightening(int value);

protected:
    virtual void mousePressEvent(QMouseEvent *e);
    virtual void mouseMoveEvent(QMouseEvent *e);
    virtual void mouseReleaseEvent(QMouseEvent *e);

	void enableVideoBeutyControl(bool bEnable);
	void updateBeautyOptions();
private:
    const int lnGapWidth = 18;
    const int lnGapHeight = 12;
    const int lnTitleWidth = 718;
    const int lnTitleHeight = 30;
    QPoint m_mousePosition;
    bool   m_bMousePressed;

private:
    Ui::roomsettings *ui;
    QMainWindow* m_pLastWnd;

private:
    bool    m_bEnableAudio;
    bool    m_bEnableVideo;
	bool    m_bEnableBeauty;
};

#endif // ROOMSETTINGS_H
