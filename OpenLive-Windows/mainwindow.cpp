#include "stdafx.h"
#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "agoraobject.h"
#include "enterroom.h"
#include <QMessageBox>

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow),
    m_bMousePressed(false),
    m_upRs(new roomsettings(static_cast<QMainWindow*>(this),NULL))
{
    ui->setupUi(this);

    connect(ui->btnset,&QPushButton::clicked,this,&MainWindow::OnClickSettings);
    connect(ui->btnmin,&QPushButton::clicked,this,&MainWindow::OnClickMin);
    connect(ui->btnclose,&QPushButton::clicked,this,&MainWindow::OnClickClose);

    connect(ui->rbLive,&QRadioButton::toggled,this,&MainWindow::OnClickLive);
    connect(ui->rbguide,&QRadioButton::toggled,this,&MainWindow::OnClickGuide);

    connect(ui->btnjoin,&QPushButton::clicked,this,&MainWindow::OnClickJoin);
    connect(ui->leditRoomId,&QLineEdit::textChanged,this,&MainWindow::OnlineEditChanged);
    connect(ui->leditRoomId,&QLineEdit::returnPressed,this,&MainWindow::OnlineEditEnter);

    this->setWindowFlags(Qt::FramelessWindowHint | Qt::WindowSystemMenuHint | Qt::WindowMinMaxButtonsHint);
    this->setAttribute(Qt::WA_TranslucentBackground);

    initWindow();
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::initWindow()
{
    m_strRoomId.clear();
    QString strDir = QCoreApplication::applicationDirPath();
    strDir.append("\\AgoraSDK.log");
    CAgoraObject::getInstance(this)->setLogPath(strDir);

    CAgoraObject::getInstance()->enableAudio(true);
    CAgoraObject::getInstance()->enableVideo(true);
    CAgoraObject::getInstance()->EnableWebSdkInteroperability(true);
    CAgoraObject::getInstance()->SetChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
}

void MainWindow::OnClickSettings()
{
    this->hide();
    m_upRs->initWindow(m_strRoomId);
    m_upRs->show();
}

void MainWindow::OnClickMin()
{
    this->showMinimized();
}

void MainWindow::OnClickClose()
{
    this->close();
}

void MainWindow::OnClickLive()
{
    bool bChecked = ui->rbLive->isChecked();
    if(bChecked) {
        QString qStyle = "QLabel{\
                background-image: url(:/uiresource/pic-live 02.png);\
                }";
        ui->lbLiveLogo->setStyleSheet(qStyle);
        qStyle = "QLabel{\
                background-image: url(:/uiresource/white.png);\
                color:#44A2FC\
                }";
        ui->laLive->setStyleSheet(qStyle);
    }
    else {
        QString qStyle = "QLabel{\
                background-image: url(:/uiresource/pic-live01.png);\
                }";
        ui->lbLiveLogo->setStyleSheet(qStyle);
        qStyle = "QLabel{\
                background-image: url(:/uiresource/white.png);\
                color:#999999\
                }";
        ui->laLive->setStyleSheet(qStyle);
    }
}

void MainWindow::OnClickGuide()
{
    bool bChecked = ui->rbguide->isChecked();
    if(bChecked) {
        QString qStyle = "QLabel{\
                background-image: url(:/uiresource/pic-guide 02.png);\
                 }";
        ui->lbguideLogo->setStyleSheet(qStyle);
        qStyle = "QLabel{\
                background-image: url(:/uiresource/white.png);\
                color:#44A2FC\
                }";
        ui->lbguide->setStyleSheet(qStyle);
    }
    else {
        QString qStyle = "QLabel{\
                background-image: url(:/uiresource/pic-guide 01.png);\
                }";\
        ui->lbguideLogo->setStyleSheet(qStyle);
        qStyle = "QLabel{\
                background-image: url(:/uiresource/white.png);\
                color:#999999\
                }";\
        ui->lbguide->setStyleSheet(qStyle);
    }
}

void MainWindow::OnClickJoin()
{
    if(ui->rbLive->isChecked())
        CAgoraObject::getInstance()->SetClientRole(CLIENT_ROLE_BROADCASTER);
    if(ui->rbguide->isChecked())
        CAgoraObject::getInstance()->SetClientRole(CLIENT_ROLE_AUDIENCE);

    m_strRoomId = ui->leditRoomId->text();
    m_strRoomId = m_strRoomId.simplified();
    gAgoraConfig.setChannelName(m_strRoomId);
    if(m_strRoomId.isEmpty()) {
        QMessageBox::about(this,"Error","channel name is empty");
        return;
    }

    m_upIr.reset(new InRoom);
    InRoom* receive1 = m_upIr.get();
    connect(this,SIGNAL(joinchannel(QMainWindow*,QString,uint)),receive1,SLOT(joinchannel(QMainWindow*,QString,uint)));
    emit joinchannel(this,m_strRoomId,0);

    this->hide();
 }

void MainWindow::OnlineEditChanged()
{
    m_strRoomId = ui->leditRoomId->text();
    m_strRoomId = m_strRoomId.simplified();
    gAgoraConfig.setChannelName(m_strRoomId);
}

void MainWindow::OnlineEditEnter()
{
    OnClickJoin();
}

void MainWindow::mousePressEvent(QMouseEvent *e)
{
    if(e->button() == Qt::LeftButton) {
        m_mousePosition = e->pos();
        if(m_mousePosition.x() < lnTitleWidth &&
           m_mousePosition.x() >= lnGapWidth &&
           m_mousePosition.y() >= lnGapHeight &&
           m_mousePosition.y() < lnTitleHeight)
            m_bMousePressed = true;
    }
}

void MainWindow::mouseMoveEvent(QMouseEvent *e)
{
    if(m_bMousePressed == true) {
        QPoint movePos = e->globalPos() - m_mousePosition;
        move(movePos);
        e->accept();
    }
}

void MainWindow::mouseReleaseEvent(QMouseEvent *e)
{
   m_bMousePressed = false;
}

void MainWindow::receive_exitChannel()
{
    //qDebug(__FUNCTION__);
    m_upIr->leavechannel();
    this->show();
}
