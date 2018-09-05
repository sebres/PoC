@pushd %~dp0
@rem echo %*
@test-dump.exe 9:%9 8:%8 7:%7 6:%6 5:%5 4:%4 3:%3 2:%2 1:%1 && echo ^ - revers ^(details^)
@test-dump.exe %9 %8 %7 %6 %5 %4 %3 %2 %1 && echo ^ - revers
@test-dump.exe 1:%1 2:%2 3:%3 4:%4 5:%5 6:%6 7:%7 8:%8 9:%9 && echo ^ - normal ^(details^)
@test-dump.exe %1 %2 %3 %4 %5 %6 %7 %8 %9 && echo ^ - normal
@rem test-dump.exe "9:%~9" "8:%~8" "7:%~7" "6:%~6" "5:%~5" "4:%~4" "3:%~3" "2:%~2" "1:%~1" && echo ^ - revers ^(unquoted^)
@rem test-dump.exe "1:%~1" "2:%~2" "3:%~3" "4:%~4" "5:%~5" "6:%~6" "7:%~7" "8:%~8" "9:%~9" && echo ^ - normal ^(unquoted^)
@test-dump.exe %*
@popd
