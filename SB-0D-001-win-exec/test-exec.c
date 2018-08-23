/*
 * Usage:
     set bld=gcc -mconsole test-exec.c 
     %bld% -o test-dump.exe && %bld% -DTST_INV_DUMP=1 -o test-dump-inv.exe && test-dump-inv
 * Build:
     set bld=gcc -mconsole test-exec.c
     %bld% -o test-dump.exe && %bld% -DTST_INV_DUMP=1 -o test-dump-inv.exe
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

/* avoid wildcard-expanding of arguments (under mingw, by default, wildcards are expanded in command line arguments). */
#if defined (__GNUC__) || defined(__MINGW32__) || defined(__MINGW64__)
  /* I saw already several versions of "CRT_noglob.o" with different variants, so: */
  #ifdef _CRT_glob
    int _CRT_glob = 0;
  #else
    int _dowildcard = 0;
  #endif
  static void   setargv(int *argcPtr, char ***argvPtr);
#endif

#if !defined(TST_INV_DUMP) || !TST_INV_DUMP /* test-dump.exe -- */
#else                                       /* test-dump-inv.exe -- */
static void
__printfcmd(int c, char *exec, char *cmd, ...) {
  va_list args;
  int i;

  printf("\n** test %d) :: %s -- %s", c, "__exec", cmd);
  va_start(args, cmd);
  while (1) {
    char *arg = va_arg (args, char *);
    if (!arg) break;
    printf(" %s", arg);
  }
  va_end(args);
  printf("\n");
}

static int
__exec(char *cmd, ...) {
  va_list args;
  char cmdLine[32768];
  int i;
  PROCESS_INFORMATION processInfo;
  STARTUPINFO startInfo;

  ZeroMemory(&startInfo, sizeof(startInfo));
  startInfo.cb = sizeof(startInfo);
  va_start(args, cmd);
  strcpy(cmdLine, cmd);
  while (1) {
    char *arg = va_arg (args, char *);
    if (!arg) break;
    strcat(cmdLine, " ");
    strcat(cmdLine, arg);
  }
  va_end(args);
  //printf("********* %s\n", cmdLine);
  if (CreateProcess(NULL, cmdLine, NULL, NULL, TRUE, 0, NULL, NULL, &startInfo, &processInfo)) {
    WaitForSingleObject(processInfo.hProcess, INFINITE);
    CloseHandle(processInfo.hProcess);
    CloseHandle(processInfo.hThread);
  } else {
    printf("ERROR: start failed: %d\n", GetLastError());
  }
}
#endif

int __cdecl
main(int argc, char **argv)
{
  SetConsoleOutputCP(1252); /* windows-1252 */

#if !defined(TST_INV_DUMP) || !TST_INV_DUMP /* test-dump.exe -- */

  #if defined(TST_MULTILINE) && TST_MULTILINE
    for (int arg = 0; arg < argc; arg++) {
      printf("%4d: \x60%s\xb4\n", arg, argv[arg]);
    }
  #else
    char *argv0;

    argv0 = strrchr(argv[0], '\\');
    argv0 = argv0 ? argv0+1 : argv[0];
    printf("    \x60%s\xb4", argv0);
    for (int arg = 1; arg < argc; arg++) {
      printf(" \x60%s\xb4", argv[arg]);
    }
  #endif

  fflush(stdout);
  exit(0);

#else                                       /* test-dump-inv.exe -- */

  int c = 0;

  #define _pp_get_cmd(cmd,...) (cmd)
  #define _pp_execlp(cmd,...) \
    __printfcmd(c, "__exec", cmd, ##__VA_ARGS__); \
    fflush(stdout); \
    __exec(cmd, __VA_ARGS__); \
    // _spawnlp(_P_WAIT, cmd, cmd, __VA_ARGS__); \
    fflush(stdout);

  #define _pp_execdump2(...) \
    _pp_execlp("test-dump.exe", ##__VA_ARGS__); \
    _pp_execlp("test-dump.CMD", ##__VA_ARGS__); \
    printf("\n========================\n"); \
    c++;

  /* unquoted / unpaired quotes */

  printf("\n========================\n"
     "====== * BROKEN * ======\n"
     "========================\n");

  _pp_execdump2("*", "\"test 1\"",         "test&whoami", NULL);
  _pp_execdump2("*", "\"test\\1\\\"\\\"",  "\"echo test < whoami\"", NULL);
  _pp_execdump2("*", "\"test \\\"1\"",     "\"test & whoami\"", NULL);
  _pp_execdump2("*", "\"test \\ 1\\\"\"",  "\"test | whoami\"", NULL);
  _pp_execdump2("*", "\"test 1\"",            "\"test %USERDOMAIN%\\%USERNAME%\"", NULL);
  _pp_execdump2("*", "\"te\\\"\\\"\\\"st\"",  "\"test %USERDOMAIN%\\%USERNAME%\"", NULL);
  
  _pp_execdump2("\"test\"\\\\&\\\\test\"", NULL);
  _pp_execdump2("\"test\"\\.\\&\\\\test\"", NULL);

  /* quoted (considering unpaired quotes) */

  printf(
     "====== * HEALTH * ======\n"
     "========================\n");

  c = 0;

  _pp_execdump2("*", "\"test 1\"",         "\"test&whoami\"", NULL);
  _pp_execdump2("*", "\"test\\1\\\"\\\\\"","\"echo test \"<\" whoami\"", NULL);
  _pp_execdump2("*", "\"test \\\"1\"",     "\"test \"&\" whoami\"", NULL);
  _pp_execdump2("*", "\"test \\ 1\\\"\"",  "\"test \"|\" whoami\"", NULL);
  _pp_execdump2("*", "\"test 1\"",            "\"test \"%\"USERDOMAIN\"%\\%\"USERNAME\"%\"\"", NULL);
  _pp_execdump2("*", "\"te\\\"\\\"\\\"st\"",  "\"test \"%\"USERDOMAIN\"%\\%\"USERNAME\"%\"\"", NULL);
  
  _pp_execdump2("\"test\\\"\\\\\\\\\"&\"\\\\test\"", NULL);
  _pp_execdump2("\"test\\\"\\.\\\\\"&\"\\\\test\"", NULL);

#endif
}
