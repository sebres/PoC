@set JDKPATH=C:\Program Files (x86)\Java\jdk1.8.0_181\bin

@pushd %~dp0
@mkdir java 2> NUL
@del /q java\sebres\PoC_0day_WinExec\test_dump_inv.* 2> NUL
@"%JDKPATH%\javac.exe" -g -d java test_dump_inv.java

@chcp 1252 > NUL
"%JDKPATH%\java.exe" -cp ./java sebres.PoC_0day_WinExec.test_dump_inv

@popd
