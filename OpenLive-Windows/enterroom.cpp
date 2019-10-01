#include "stdafx.h"
#include "enterroom.h"
#include "ui_enterroom.h"
#include "agoraobject.h"
#include "qgraphicseffect.h"

 EnterRoom::EnterRoom(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::wgEnterRoom),
    m_pLastWnd(Q_NULLPTR)
{
    ui->setupUi(this);

    this->setWindowFlags(Qt::FramelessWindowHint | Qt::WindowSystemMenuHint | Qt::WindowStaysOnTopHint);
    this->setAttribute(Qt::WA_TranslucentBackground);
    this->setAttribute(Qt::WA_TransparentForMouseEvents,false);

    connect(this,SIGNAL(clicked()),this,SLOT(on_mouseclicked()));
}

EnterRoom::~EnterRoom()
{
    delete ui;
}

void EnterRoom::mousePressEvent(QMouseEvent *e)
{
    if(e->button() == Qt::LeftButton) {
        m_mousePosition = e->pos();
        if(m_mousePosition.x() < lnTitleWidth &&
           m_mousePosition.y() < lnTitleHeight)
            m_bMousePressed = true;
    }
}

void EnterRoom::mouseMoveEvent(QMouseEvent *e)
{
    if(m_bMousePressed == true) {
        QPoint movePos = e->globalPos() - m_mousePosition;
        move(movePos);
        e->accept();
    }
}

void EnterRoom::mouseReleaseEvent(QMouseEvent *e)
{
   m_bMousePressed = false;
}

void EnterRoom::focusInEvent(QFocusEvent *event)
{
    //qDebug(__FUNCTION__);
    this->show();
}

void EnterRoom::focusOutEvent(QFocusEvent *event)
{
    //qDebug(__FUNCTION__);
    this->hide();
}

void EnterRoom::joinchannel(QMainWindow* pLastWnd,const QString& qsChannel,uint uid)
{
    //qDebug(__FUNCTION__);
    m_pLastWnd = pLastWnd;
    ui->lbChannel->setText(qsChannel);
    this->show();
}

void EnterRoom::leavechannel()
{
    //qDebug(__FUNCTION__);
}

void EnterRoom::setChannelName(const QString& qsChannelInfo)
{
    ui->lbChannel->setText(qsChannelInfo);
}

void EnterRoom::setParam(const QString& qsParam)
{
    ui->lbParam->setText(qsParam);
}

int EnterRoom::getR1()
{
    return m_uidr1;
}

int EnterRoom::getR2()
{
    return m_uidr2;
}

int EnterRoom::getR3()
{
    return m_uidr3;
}

void EnterRoom::switchUidText(const QString &qsSrc,const QString &qsDest,int nIndex)
{
    QString qsInfo1;
    qsInfo1.sprintf("%s,%s,%d",qsSrc.toStdString().c_str(),qsDest.toStdString().c_str(),nIndex);
    //qDebug(qsInfo1.toStdString().c_str());
    ui->lbChannel->setText(qsSrc);
    if(1 == nIndex)
        ui->lb_r1->setText(qsDest);
    if(2 == nIndex)
        ui->lb_r2->setText(qsDest);
    if(3 == nIndex)
        ui->lb_r3->setText(qsDest);
}

void EnterRoom::on_btnExit_clicked()
{
    connect(this,SIGNAL(sender_exitChannel()),m_pLastWnd,SLOT(receive_exitChannel()));
    emit sender_exitChannel();
    this->hide();
}

void EnterRoom::on_rbCamera_stateChanged(int arg1)
{
    if(arg1 == Qt::Unchecked)
        CAgoraObject::getInstance()->MuteLocalVideo(FALSE);
    else if(arg1 == Qt::Checked)
        CAgoraObject::getInstance()->MuteLocalVideo(TRUE);
}

void EnterRoom::on_rbMic_stateChanged(int arg1)
{
    if(arg1 == Qt::Unchecked)
        CAgoraObject::getInstance()->MuteLocalAudio(FALSE);
    else if(arg1 == Qt::Checked)
        CAgoraObject::getInstance()->MuteLocalAudio(TRUE);
}

void EnterRoom::setAllRAtt(int nRCount)
{
    ui->lb_r1->setVisible(false);
    ui->lb_r2->setVisible(false);
    ui->lb_r3->setVisible(false);
    ui->lb_count->setVisible(false);

   if(nRCount >= 1) {
        QString qsText;
        qsText.sprintf("  uid:%u",m_uidr1);
        ui->lb_r1->setText(qsText);
        ui->lb_r1->setVisible(true);

        QRect qrt = ui->lb_count->geometry();
        QRect qrt1 = ui->lb_r1->geometry();
        qrt.setTop(qrt1.bottom() + 10);
        ui->lb_count->move(qrt.left(),qrt.top());
        ui->lb_count->setVisible(true);

   }
   if(nRCount >= 2) {
        QString qsText;
        qsText.sprintf("  uid:%u",m_uidr2);
        ui->lb_r2->setText(qsText);
        ui->lb_r2->setVisible(true);

        QRect qrt = ui->lb_count->geometry();
        QRect qrt1 = ui->lb_r2->geometry();
        qrt.setTop(qrt1.bottom() + 10);
        ui->lb_count->move(qrt.left(),qrt.top());
        ui->lb_count->setVisible(true);
   }
   if(nRCount >= 3) {
       QString qsText;
       qsText.sprintf("  uid:%u",m_uidr3);
       ui->lb_r3->setText(qsText);
       ui->lb_r3->setVisible(true);

       QRect qrt = ui->lb_count->geometry();
       QRect qrt1 = ui->lb_r3->geometry();
       qrt.setTop(qrt1.bottom() + 10);
       ui->lb_count->move(qrt.left(),qrt.top());
       ui->lb_count->setVisible(true);
   }

    QString qsCount;
    qsCount.sprintf("Total %d Person",nRCount + 1);
    ui->lb_count->setText(qsCount);
}

void EnterRoom::setR1(uid_t uid)
{
    m_uidr1 = uid;
}

void EnterRoom::setR2(uid_t uid)
{
    m_uidr2 = uid;
}

void EnterRoom::setR3(uid_t uid)
{
    m_uidr3 = uid;
}

void EnterRoom::on_mouseclicked()
{
    //qDebug(__FUNCTION__);
}
