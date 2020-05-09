#include "stdafx.h"
#include "roomsettings.h"
#include "ui_roomsettings.h"
#include "agoraobject.h"
#include <QtWidgets/QScrollbar>
#include <QtWidgets/QScrollArea>
roomsettings::roomsettings(QWidget *parent):
    QMainWindow(parent),
    ui(new Ui::roomsettings)
{
    ui->setupUi(this);
	
	ui->scrollArea->verticalScrollBar()->setStyleSheet(QLatin1String(""
		"QScrollBar:vertical {border: none;background-color: rgb(255,255,255);width: 10px;margin: 0px 0 0px 0;}"
		" QScrollBar::handle:vertical { background:  rgba(240, 240, 240, 255); min-height: 20px;width: 6px;border: 1px solid  rgba(240, 240, 240, 255);border-radius: 3px;margin-left:2px;margin-right:2px;}"
		" QScrollBar::add-line:vertical {background-color: rgb(255,255,255);height: 4px;}"
		" QScrollBar::sub-line:vertical {background-color: rgb(255,255,255);height: 4px;}"
		" QScrollBar::up-arrow:vertical, QScrollBar::down-arrow:vertical {height: 0px;}"
		" QScrollBar::add-page:vertical, QScrollBar::sub-page:vertical {background: none;}"));
    this->setWindowFlags(Qt::FramelessWindowHint | Qt::WindowSystemMenuHint | Qt::WindowMinMaxButtonsHint);
    this->setAttribute(Qt::WA_TranslucentBackground);
}

roomsettings::roomsettings(QMainWindow* pLastWnd,QWidget *parent) :
    QMainWindow(parent),
    m_pLastWnd(pLastWnd),
    m_bEnableAudio(true),
    m_bEnableVideo(true),
	m_bEnableBeauty(false),
    ui(new Ui::roomsettings)
{
    ui->setupUi(this);
    int i = 0;
    fps[i++] = FRAME_RATE_FPS_7;
    fps[i++] = FRAME_RATE_FPS_10;
    fps[i++] = FRAME_RATE_FPS_15;
    fps[i++] = FRAME_RATE_FPS_24;
    fps[i++] = FRAME_RATE_FPS_30;

    bitrate[0] = STANDARD_BITRATE;
    bitrate[1] = COMPATIBLE_BITRATE;
    bitrate[2] = DEFAULT_MIN_BITRATE;
    connect(ui->btnlastpage,&QPushButton::clicked,this,&roomsettings::OnClickLastPage);

    connect(ui->optAudio,&QPushButton::clicked,this,&roomsettings::OnOptAudio);
    connect(ui->optVideo,&QPushButton::clicked,this,&roomsettings::OnOptVideo);
	connect(ui->optVideo_Beauty, &QPushButton::clicked, this, &roomsettings::OnOptBeauty);

	connect(ui->horizontalSlider_Redness, &QSlider::valueChanged, this, &roomsettings::on_valueChanged_horizontalSlider_Redness);
	connect(ui->horizontalSlider_Smoothness, &QSlider::valueChanged, this, &roomsettings::on_valueChanged_horizontalSlider_Smoothness);
	connect(ui->horizontalSliderLightening, &QSlider::valueChanged, this, &roomsettings::on_valueChanged_horizontalSlider_Lightening);

	ui->scrollArea->verticalScrollBar()->setStyleSheet(QLatin1String(""
		"QScrollBar:vertical {border: none;background-color: rgb(255,255,255);width: 10px;margin: 0px 0 0px 0;}"
		" QScrollBar::handle:vertical { background:  rgba(240, 240, 240, 255); min-height: 20px;width: 6px;border: 1px solid  rgba(240, 240, 240, 255);border-radius: 3px;margin-left:2px;margin-right:2px;}"
		" QScrollBar::add-line:vertical {background-color: rgb(255,255,255);height: 4px;}"
		" QScrollBar::sub-line:vertical {background-color: rgb(255,255,255);height: 4px;}"
		" QScrollBar::up-arrow:vertical, QScrollBar::down-arrow:vertical {height: 0px;}"
		" QScrollBar::add-page:vertical, QScrollBar::sub-page:vertical {background: none;}"));
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

	m_bEnableBeauty = gAgoraConfig.getEnableBeauty();

	if (!m_bEnableBeauty) {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }";

        ui->optVideo_Beauty->setStyleSheet(str);
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

		ui->optVideo_Beauty->setStyleSheet(str);
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

    ui->cbVideoFPS->clear();
    ui->cbVideoFPS->addItem("FRAME_RATE_FPS_7");
    ui->cbVideoFPS->addItem("FRAME_RATE_FPS_10");
    ui->cbVideoFPS->addItem("FRAME_RATE_FPS_15");
    ui->cbVideoFPS->addItem("FRAME_RATE_FPS_24");
    ui->cbVideoFPS->addItem("FRAME_RATE_FPS_30");
    ui->cbVideoFPS->setCurrentIndex(2);

    ui->cbVideoBitrate->clear();
    ui->cbVideoBitrate->addItem("STANDARD_BITRATE");
    ui->cbVideoBitrate->addItem("COMPATIBLE_BITRATE");
    ui->cbVideoBitrate->addItem("DEFAULT_MIN_BITRATE");
    ui->cbVideoBitrate->setCurrentIndex(0);
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
	//beauty
	ui->cbContrastLevel->clear();
	ui->cbContrastLevel->addItem(QString("Lightening Contrast Low"));
	ui->cbContrastLevel->addItem(QString("Lightening Contrast Normal"));
	ui->cbContrastLevel->addItem(QString("Lightening Contrast High"));

	ui->cbContrastLevel->setCurrentIndex(gAgoraConfig.getLigtheningContrastLevel()); 

	ui->horizontalSlider_Redness->setValue(gAgoraConfig.getRedness());
	ui->horizontalSliderLightening->setValue(gAgoraConfig.getLightenging());
	ui->horizontalSlider_Smoothness->setValue(gAgoraConfig.getSmoothness());
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

void roomsettings::enableVideoBeutyControl(bool bEnable)
{
	ui->cbContrastLevel->setDisabled(bEnable);
	ui->horizontalSliderLightening->setDisabled(bEnable);
	ui->horizontalSlider_Redness->setDisabled(bEnable);
	ui->horizontalSlider_Smoothness->setDisabled(bEnable);
}

void roomsettings::OnOptBeauty()
{
	m_bEnableBeauty = !m_bEnableBeauty;
	enableVideoBeutyControl(!m_bEnableBeauty);
	updateBeautyOptions();
	if (!m_bEnableBeauty) {
        QString str = "QPushButton:!hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }\
                \
                QPushButton:hover {\
                border-image: url(:/uiresource/switch-off.png);\
                background-image: url(:/uiresource/switch-off.png);\
                }";

        ui->optVideo_Beauty->setStyleSheet(str);
	
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

		ui->optVideo_Beauty->setStyleSheet(str);
    }

	gAgoraConfig.setEnableBeauty(m_bEnableBeauty);
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

void roomsettings::setVideoProfile(const QString argResolution)
{
    int nWidth,nHeight = 0;
    sscanf(argResolution.toUtf8().data(),"%dx%d", &nWidth, &nHeight);
    int index = ui->cbVideoFPS->currentIndex();
    int idx   = ui->cbVideoBitrate->currentIndex();
    CAgoraObject::getInstance()->setVideoProfile(nWidth, nHeight, (FRAME_RATE)fps[index], bitrate[idx]);
}

void roomsettings::on_cbVideoProfile_activated(const QString &arg1)
{
   setVideoProfile(arg1);
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

void roomsettings::updateBeautyOptions()
{
	BeautyOptions options;
	options.lighteningContrastLevel = (BeautyOptions::LIGHTENING_CONTRAST_LEVEL)ui->cbContrastLevel->currentIndex();
	options.lighteningLevel         = ui->horizontalSliderLightening->value()/100.0f;
	options.rednessLevel            = ui->horizontalSlider_Redness->value()/100.0f;
	options.smoothnessLevel         = ui->horizontalSlider_Smoothness->value()/100.0f;

	CAgoraObject::getInstance()->setBeautyEffectOptions(m_bEnableBeauty, options);
}

void roomsettings::on_cbContrastLevel_activated(int index)
{
	updateBeautyOptions();
}

void roomsettings::on_valueChanged_horizontalSlider_Redness(int value)
{
	QString redness ;
	redness.sprintf("Redness(%.02f)", value / 100.0f);
	ui->labelRedness->setText(redness);
	gAgoraConfig.setRedness(value);
	updateBeautyOptions();
}

void roomsettings::on_valueChanged_horizontalSlider_Smoothness(int value)
{
	QString smooth;
	smooth.sprintf("Smoothness(%.02f)", value / 100.0f);
	ui->labelSmoothness->setText(smooth);
	gAgoraConfig.setSmoothness(value);
	updateBeautyOptions();
}

void roomsettings::on_valueChanged_horizontalSlider_Lightening(int value)
{
	QString lightening ;
	lightening.sprintf("Lightening(%.02f)", value / 100.0f);
	ui->labelLightening->setText(lightening);
	gAgoraConfig.setLightenging(value);
	updateBeautyOptions();
}

void roomsettings::on_cbVideoFPS_currentIndexChanged(int index)
{
    QString arg1 = ui->cbVideoProfile->currentText();
    setVideoProfile(arg1);
}

void roomsettings::on_cbVideoBitrate_currentIndexChanged(int index)
{
    QString arg1 = ui->cbVideoProfile->currentText();
    setVideoProfile(arg1);
}
