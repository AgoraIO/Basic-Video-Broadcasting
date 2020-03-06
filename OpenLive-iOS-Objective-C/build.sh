rm -f *.ipa
rm -rf *.app
rm -f *.zip
rm -rf dSYMs
rm -rf *.dSYM
rm -f *dSYMs.zip
rm -rf *.xcarchive

PROJECT_NAME="OpenLive.xcodeproj"
SCHEME_NAME="OpenLive"
BUILD_DATE=`date +%Y-%m-%d-%H.%M.%S`
ArchivePath=${SCHEME_NAME}-${BUILD_DATE}.xcarchive

xcodebuild clean -project ${PROJECT_NAME} -scheme ${SCHEME_NAME} -configuration Release
xcodebuild -project ${PROJECT_NAME} -scheme ${SCHEME_NAME} -archivePath ${ArchivePath} archive
xcodebuild -exportArchive -exportOptionsPlist exportPlist.plist -archivePath ${ArchivePath} -exportPath .
