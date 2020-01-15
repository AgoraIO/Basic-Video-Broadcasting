pushd Release
pushd Language

del *.iobj
del *.ipdb

popd Language

del *.ipdb
del *.iobj

IF NOT EXIST msvcp140.dll (
  copy C:\Windows\SysWOW64\msvcp140.dll
)

IF NOT EXIST mfc140u.dll (
  copy C:\Windows\SysWOW64\mfc140u.dll 
)

popd Release
pause