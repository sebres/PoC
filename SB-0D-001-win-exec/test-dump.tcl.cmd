@rem set TCLSH=tclsh.exe
@pushd %~dp0
@rem echo %*
@%TCLSH% test-dump.tcl %*
@popd
