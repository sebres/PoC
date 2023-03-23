/*
 * Build:
     gcc -mconsole test-cltaw.c -o test-cltaw.exe
 *
 *   - options:
 *       TST_MULTILINE - if multi-line output needed specify -DTST_MULTILINE=1 parameter to gcc.
 * Test:
 *   test-dump-inv
 */
#include <stdio.h>
#include <string.h>
#include <process.h>
#include <windows.h>

int __cdecl
main()
{
  int argc;
  LPWSTR * argv = CommandLineToArgvW(GetCommandLineW(), &argc);
  if (!argv) {
    wprintf(L"CommandLineToArgvW failed\n");
    exit(1);
  }
  #if defined(TST_MULTILINE) && TST_MULTILINE
    for (int arg = 0; arg < argc; arg++) {
      wprintf(L"%4d: \x60%s\xb4\n", arg, argv[arg]);
    }
  #else
    wchar_t *argv0;

    argv0 = wcsrchr(argv[0], L'\\');
    argv0 = argv0 ? argv0+1 : argv[0];
    wprintf(L"    \x60%s\xb4", argv0);
    for (int arg = 1; arg < argc; arg++) {
      wprintf(L" \x60%s\xb4", argv[arg]);
    }
  #endif

  LocalFree(argv);

  fflush(stdout);
  exit(0);
}
