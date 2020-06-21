pushd Release
pushd Language

del *.exp
del *.pdb
del *.lib

popd Language

del *.pdb
del *.log

IF NOT EXIST msvcp120.dll (
  copy C:\Windows\SysWOW64\msvcp120.dll
)

IF NOT EXIST msvcr120.dll (
  copy C:\Windows\SysWOW64\msvcr120.dll
)

IF NOT EXIST mfc120u.dll (
  copy C:\Windows\SysWOW64\mfc120u.dll 
)
popd Release
pause