set sdkversion=small

if %sdkversion% == origin ( exit )

set Machine=%~1
set absolute_path=%~2
cd %absolute_path%
echo %absolute_path%
if not exist sdk (
  echo %~dp0
  mkdir sdk
)

if exist sdk ( 
  pushd sdk
  if not exist dll (
    mkdir dll
  )
 
  if not exist lib (
    mkdir lib
  )
  if not exist include (
    mkdir include
  )
  popd
)

if %sdkversion% == small (
  if exist libs\include (
    copy libs\include\*.h sdk\include
  )

  if %Machine%==x86 (
    copy libs\x86\*.lib sdk\lib
    copy libs\x86\*.dll sdk\dll
  ) else (
    copy libs\x86_64\*.lib sdk\lib
    copy libs\x86_64\*.dll sdk\dll
  )

)
pause