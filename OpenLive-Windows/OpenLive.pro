#-------------------------------------------------
#
# Project created by QtCreator 2019-09-04T11:52:50
#
#-------------------------------------------------

QT       += core gui
CONFIG   += c++11
greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

SDKPATHNAME=sdk
SDKLIBPATHNAME=lib
SDKDLLPATHNAME=dll

!contains(QMAKE_TARGET.arch, x86_64) {
  SDKLIBPATHNAME=lib
  SDKDLLPATHNAME=dll
} else {
  SDKLIBPATHNAME=lib
  SDKDLLPATHNAME=dll
}

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

exists( $$PWD/$${SDKPATHNAME}) {
  AGORASDKPATH = $$PWD/$${SDKPATHNAME}
  AGORASDKDLLPATH = .\\$${SDKPATHNAME}\\$${SDKDLLPATHNAME}
} else {
  AGORASDKPATH = $$PWD/../../$${SDKPATHNAME}
  AGORASDKDLLPATH =..\\..\\$${SDKPATHNAME}\\$${SDKDLLPATHNAME}
}

win32: {
INCLUDEPATH += $${AGORASDKPATH}/include
LIBS += -L$${AGORASDKPATH}/$${SDKLIBPATHNAME} -lagora_rtc_sdk
LIBS += User32.LIB

CONFIG(debug, debug|release) {
  QMAKE_POST_LINK +=  copy $${AGORASDKDLLPATH}\*.dll .\Debug
} else {
  QMAKE_POST_LINK +=  copy $${AGORASDKDLLPATH}\*.dll .\Release
  QMAKE_POST_LINK += && windeployqt Release\OpenLive.exe
}

}

