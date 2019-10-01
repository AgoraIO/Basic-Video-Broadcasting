#ifndef MAINWINDOW_H
#define MAINWINDOW_H

class roomsettings;
#include <Memory>
#include <QMainWindow>
#include "roomsettings.h"
#include "enterroom.h"
#include "inroom.h"

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

protected:
    void initWindow();

private slots:
    void OnClickSettings();
    void OnClickMin();
    void OnClickClose();
    void OnClickLive();
    void OnClickGuide();
    void OnClickJoin();
    void OnlineEditChanged();
    void OnlineEditEnter();

    void receive_exitChannel();

signals:
    void joinchannel(QMainWindow* pMainWnd,const QString &qsChannel,uint uid);
    void leavechannel();

protected:
    virtual void mousePressEvent(QMouseEvent *e);
    virtual void mouseMoveEvent(QMouseEvent *e);
    virtual void mouseReleaseEvent(QMouseEvent *e);

private:
    const int lnGapWidth = 18;
    const int lnGapHeight = 12;
    const int lnTitleWidth = 718;
    const int lnTitleHeight = 30;
    QPoint m_mousePosition;
    bool   m_bMousePressed;

private:
    Ui::MainWindow *ui;
    QString     m_strRoomId;
    std::unique_ptr<roomsettings> m_upRs;
    std::unique_ptr<InRoom> m_upIr;
};

#endif // MAINWINDOW_H
