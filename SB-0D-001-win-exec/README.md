## [SB-0D-001-win-exec] - PoC (Proof of Concept)

### &#x1F6A8; 0-day vulnerability "insufficient escape of special chars for quoting of arguments by exec process" by almost all languages for windows.

#### Prehistory:

Windows does not really provide a proper way to execute process with arguments (as list or array).<br/>
The major function that windows API (and many primitives in several languages) internally use is [CreateProcess](https://msdn.microsoft.com/en-us/ms682425), 
where parameter `lpCommandLine` is a complete command line (that should be proper combined, quoted and escaped from the caller),
which will be typically parsed hereafter inside child process (**after child process is started**).

There is not really ArgvToCommandLine or similar function.<br/>
The CRT-functions like [_spawn-family](https://msdn.microsoft.com/en-us/library/20y988d2.aspx) are not really useable,
because just doing simple join of arguments or argument-array without any escape/quoting, so they have a problem even by spaces inside argument:
> Spaces embedded in strings may cause unexpected behavior; for example, passing _spawn the string "hi there" will result in the new process getting two arguments, "hi" and "there". 
If the intent was to have the new process open a file named "hi there", the process would fail. You can avoid this by quoting the string: "\"hi there\"".

See for example 
[Everyone quotes command line arguments the wrong way – Twisty Little Passages, All Alike](https://blogs.msdn.microsoft.com/twistylittlepassagesallalike/2011/04/23/everyone-quotes-command-line-arguments-the-wrong-way/) 
for several methods how people doing this.<br/>
BTW. The method provided there as solution is in my opinion also not completely proper: escape using circumflex (`^`) does not work in all quoting cases, 
and one should differentiate between invocation of exe- and batch-file.

Therefore the issue is rather a matter the weird windows unescape/unquote behavior in case of process execution and invocation of command processor,
so many languages seem to be affected similar way also by same vulnerability.

#### Current state:

Language | Level | Fixed | Confirmed | Ticket | Test | Result
--- | --- | --- | --- | --- | --- | ---
**TCL** | :no_entry_sign: <sub>middle (Batch-Only)</sub> | :heavy_check_mark: <sub>[[diff]](https://core.tcl-lang.org/tcl/vdiff?from=core-8-5-branch&to=0-day-21b0629c81)</sub> | :heavy_check_mark: | <sub>[[21b0629c81fbe38a]](https://core.tcl-lang.org/tcl/info/21b0629c81fbe38a)</sub> | <sub>tclsh [test-dump-inv.tcl](test-dump-inv.tcl)</sub> | <sub>[result](results/tcl.diff)</sub>
**Python** | :no_entry_sign: <sub>middle (Batch-Only)</sub> | - | - | - | <sub>python [test-dump-inv.py](test-dump-inv.py)</sub> | <sub>[result](results/python.diff)</sub>
**Java** / JVM | :no_entry: <sub>grave</sub> | - | - | - | <sub>[test-dump-inv.java](test-dump-inv.java) <br/> [test-dump-inv.java.cmd](test-dump-inv.java.cmd)</sub> | <sub>[result](results/jvm.diff)</sub>
**Scala** / JVM | :no_entry: <sub>grave</sub> | - | - | - | <sub>scala [test-dump-inv.scala](test-dump-inv.scala)</sub> | <sub>[result](results/jvm.diff)</sub>
**Your preferred lang** | - | - | - | - | - | -

#### How to test:

Build `test-dump.exe` using MSVC or MINGW-gcc, which will be used together with [test-dump.cmd](test-dump.cmd) in tests for dump of arguments:
```bash
gcc -mconsole test-exec.c -o test-dump.exe
```
If no toolchain for build is available, simply download and unpack it from [test-dump.zip](https://github.com/sebres/PoC/files/2316009/test-dump.zip).

Hereafter just execute the test-script (see column "Test" in table above).
Each step of the test-case executes `test-dump.exe` as well as `test-dump.cmd` with some test arguments 
and compares the output of dump with original supplied arguments.

**Note:**
The tests will show if the arguments gets from `test-dump` are completely different (like most from the [results/jvm.diff](results/jvm.diff)) or vulnerable only on invocation of batch-files (see [results/tcl.diff](results/tcl.diff) or [results/python.diff](results/python.diff), where the execution of exe-file is not affected).

So in case of JVM (java, scala) because the execution of exe-files is affected by insufficient escape/quoting it is classified as **grave**.

The results are provided as diff-files for better readability of different/vulnerable places (red-marked) on github resp. most external diff-viewer.
