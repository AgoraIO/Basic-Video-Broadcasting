#include "stdafx.h"
#include "roomsettings.h"
#include "ui_roomsettings.h"
#include "agoraobject.h"

roomsettings::roomsettings(QWidget *parent):
    QMainWindow(parent),
    ui(new Ui::roomsettings)
{
    ui->setupUi(this);

    this->setWindowFlags(Qt::FramelessWindowHint | Qt::WindowSystemMenuHint | Qt::WindowMinMaxButtonsHint);
    this->setAttribute(Qt::WA_TranslucentBackground);
}

roomsettings::roomsettings(QMainWindow* pLastWnd,QWidget *parent) :
    QMainWindow(parent),
    m_pLastWnd(pLastWnd),
    m_bEnableAudio(true),
    m_bEnableVideo(true),
    ui(new Ui::roomsettings)
{
    ui->setupUi(this);

    connect(ui->btnlastpage,&QPushButton::clicked,this,&roomsettings::OnClickLastPage);

    connect(ui->optAudio,&QPushButton::clicked,this,&roomsettings::OnOptAudio);
    connect(ui->optVideo,&QPushButton::clicked,this,&roomsettings::OnOptVideo);

    this->setWindowFlags(Qt::FramelessWindowHint | Qt::WindowSystemMenuHint | Qt::WindowMinMaxButtonsHint);
    this->setAttribute(Qt::WA_TranslucentBackground);
}

roomsettings::~roomsettings()
{
    delete ui;
}

void roomsettings::initWindow(const QString& qsChannel)
{
    ui->lbName->setText(qsChannel);
    m_bEnableAudio = gAgoraConfig.getEnableAudio();
    if(!m_bEnableAudio) {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }";

        ui->optAudio->setStyleSheet(str);
    }
    else {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-open.png);\
                background-image: url(:/uiresource/switch-open.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-open.png);\
                background-image: url(:/uiresource/switch-open.png);\
                }";
        ui->optAudio->setStyleSheet(str);
    }

    m_bEnableVideo = gAgoraConfig.getEnableVideo();
    if(!m_bEnableVideo) {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }";

        ui->optVideo->setStyleSheet(str);
    }
    else {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-open.png);\
                background-image: url(:/uiresource/switch-open.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-open.png);\
                background-image: url(:/uiresource/switch-open.png);\
                }";

        ui->optVideo->setStyleSheet(str);
    }

    //videoprofile
	ui->cbVideoProfile->clear();
    ui->cbVideoProfile->addItem("160x120");
    ui->cbVideoProfile->addItem("320x180");
    ui->cbVideoProfile->addItem("320x240");
    ui->cbVideoProfile->addItem("640x360");
    ui->cbVideoProfile->addItem("640x480");
    ui->cbVideoProfile->addItem("1280x720");
    ui->cbVideoProfile->addItem("1920x1080");
    ui->cbVideoProfile->addItem("3840x2160");
    ui->cbVideoProfile->setCurrentIndex(3);

    //microphone
	ui->cbRecordDevices->clear();
    QString qDeviceName;
    qSSMap devicelist = CAgoraObject::getInstance()->getRecordingDeviceList();
	for (qSSMap::iterator it = devicelist.begin(); devicelist.end() != it; it++) {
        ui->cbRecordDevices->addItem(it.key());
	}

    //playout
	ui->cbPlayDevices->clear();
    devicelist.clear();
    devicelist = CAgoraObject::getInstance()->getPlayoutDeviceList();
    for(qSSMap::iterator it = devicelist.begin(); devicelist.end() != it; it++) {
        ui->cbPlayDevices->addItem(it.key());
    }

    //cameralist
	ui->cbVideoDevices->clear();
    devicelist.clear();
    devicelist = CAgoraObject::getInstance()->getVideoDeviceList();
    for(qSSMap::iterator it = devicelist.begin(); devicelist.end() != it; it++) {
        ui->cbVideoDevices->addItem(it.key());
    }
}

void roomsettings::OnClickLastPage()
{
    this->hide();
    m_pLastWnd->show();
}

void roomsettings::OnOptAudio()
{
    m_bEnableAudio = !m_bEnableAudio;
    if(!m_bEnableAudio) {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }";

        ui->optAudio->setStyleSheet(str);
    }
    else {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-open.png);\
                background-image: url(:/uiresource/switch-open.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-open.png);\
                background-image: url(:/uiresource/switch-open.png);\
                }";
        ui->optAudio->setStyleSheet(str);
    }

    gAgoraConfig.setEnableAudio(m_bEnableAudio);

    //engine to do
    CAgoraObject::getInstance()->enableAudio(m_bEnableAudio);
}

void roomsettings::OnOptVideo()
{
    m_bEnableVideo = !m_bEnableVideo;
    if(!m_bEnableVideo) {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }";

        ui->optVideo->setStyleSheet(str);
    }
    else {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-open.png);\
                background-image: url(:/uiresource/switch-open.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-open.png);\
                background-image: url(:/uiresource/switch-open.png);\
                }";

        ui->optVideo->setStyleSheet(str);
    }

    gAgoraConfig.setEnableVideo(m_bEnableVideo);

    // engine to do.
    CAgoraObject::getInstance()->enableVideo(m_bEnableVideo);
}

void roomsettings::OnCbVPIndexChanged()
{
    QString sText = ui->cbVideoProfile->currentText();
    int nWidth,hHeight = 0;
    sscanf(sText.toUtf8().data(),"%dx%d",nWidth,hHeight);
}

void roomsettings::mousePressEvent(QMouseEvent *e)
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

void roomsettings::mouseMoveEvent(QMouseEvent *e)
{
    if(m_bMousePressed == true) {
        QPoint movePos = e->globalPos() - m_mousePosition;
        move(movePos);
        e->accept();
    }
}

void roomsettings::mouseReleaseEvent(QMouseEvent *e)
{
   m_bMousePressed = false;
}

void roomsettings::on_cbVideoProfile_activated(const QString &arg1)
{
    int nWidth,nHeight = 0;
	sscanf(arg1.toUtf8().data(),"%dx%d", &nWidth, &nHeight);
	CAgoraObject::getInstance()->setVideoProfile(nWidth, nHeight);
}

void roomsettings::on_cbRecordDevices_activated(int index)
{
    CAgoraObject::getInstance()->setRecordingIndex(index);
}

void roomsettings::on_cbVideoDevices_activated(int index)
{
    CAgoraObject::getInstance()->setVideoIndex(index);
}

void roomsettings::on_cbPlayDevices_activated(int index)
{
    CAgoraObject::getInstance()->setPlayoutIndex(index);
}
