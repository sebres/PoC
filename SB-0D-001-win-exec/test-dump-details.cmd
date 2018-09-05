@pushd %~dp0
@rem echo %*
@echo CMDCMDLINE: > test-result.txt
@echo ^  %CMDCMDLINE% >> test-result.txt
@echo JOINED (%%*): >> test-result.txt
@echo ^  %* >> test-result.txt
@echo SINGLY (%%1..%%9): >> test-result.txt
@echo ^  1:%1 >> test-result.txt
@echo ^  2:%2 >> test-result.txt
@echo ^  3:%3 >> test-result.txt
@echo ^  4:%4 >> test-result.txt
@echo ^  5:%5 >> test-result.txt
@echo ^  6:%6 >> test-result.txt
@echo ^  7:%7 >> test-result.txt
@echo ^  8:%8 >> test-result.txt
@echo ^  9:%9 >> test-result.txt
@echo EN-QUOTED ("%%~1".."%%~9"): >> test-result.txt
@echo ^  1:"%~1" >> test-result.txt
@echo ^  2:"%~2" >> test-result.txt
@echo ^  3:"%~3" >> test-result.txt
@echo ^  4:"%~4" >> test-result.txt
@echo ^  5:"%~5" >> test-result.txt
@echo ^  6:"%~6" >> test-result.txt
@echo ^  7:"%~7" >> test-result.txt
@echo ^  8:"%~8" >> test-result.txt
@echo ^  9:"%~9" >> test-result.txt
@test-dump.exe 9:%9 8:%8 7:%7 6:%6 5:%5 4:%4 3:%3 2:%2 1:%1 && echo ^ - revers ^(details^)
@test-dump.exe %9 %8 %7 %6 %5 %4 %3 %2 %1 && echo ^ - revers
@test-dump.exe 1:%1 2:%2 3:%3 4:%4 5:%5 6:%6 7:%7 8:%8 9:%9 && echo ^ - normal ^(details^)
@test-dump.exe %1 %2 %3 %4 %5 %6 %7 %8 %9 && echo ^ - normal
@test-dump.exe %*
@popd
