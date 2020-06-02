#-------------------------------------------------
#
# Project created by QtCreator 2019-09-04T11:52:50
#
#-------------------------------------------------

QT       += core gui
CONFIG   += c++11
greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = OpenLive
TEMPLATE = app


SOURCES += main.cpp\
        mainwindow.cpp \
    agoraconfig.cpp \
    roomsettings.cpp \
    agoraobject.cpp \
    enterroom.cpp \
    inroom.cpp \
    agoraqtjson.cpp

HEADERS  += mainwindow.h \
    stdafx.h \
    agoraconfig.h \
    roomsettings.h \
    agoraobject.h \
    enterroom.h \
    inroom.h \
    agoraqtjson.h 

FORMS    += mainwindow.ui \
    roomsettings.ui \
    enterroom.ui \
    inroom.ui

RC_FILE = openlive.rc

RESOURCES += \
    openlive.qrc

DISTFILES += \
    openlive.rc

exists( $$PWD/../../sdk) {
  AGORASDKPATH = $$PWD/../../sdk
  AGORASDKDLLPATH =..\..\sdk\dll
} else {
  AGORASDKPATH = $$PWD/sdk
  AGORASDKDLLPATH = .\sdk\dll
}

win32: {
!contains(QMAKE_TARGET.arch, x86_64) {
  message(x86)
  INCLUDEPATH += $${AGORASDKPATH}/include
  LIBS += -L$${AGORASDKPATH}/lib -lagora_rtc_sdk
  LIBS += User32.LIB
  CONFIG(debug, debug|release) {
    QMAKE_POST_LINK +=  copy $${AGORASDKDLLPATH}\*.dll .\Debug
  } else {
    QMAKE_POST_LINK +=  copy $${AGORASDKDLLPATH}\*.dll .\Release
    QMAKE_POST_LINK += && windeployqt Release\OpenLive.exe
  }
}else {
  message(x64)
  INCLUDEPATH += $${AGORASDKPATH}/include
  LIBS += -L$${AGORASDKPATH}/lib -lagora_rtc_sdk
  LIBS += User32.LIB
  CONFIG(debug, debug|release) {
    QMAKE_POST_LINK +=  copy $${AGORASDKDLLPATH}\*.dll .\Debug
  } else {
    QMAKE_POST_LINK +=  copy $${AGORASDKDLLPATH}\*.dll .\Release
    QMAKE_POST_LINK += && windeployqt Release\OpenLive.exe
  }
}

}

