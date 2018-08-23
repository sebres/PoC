#!/usr/bin/env tclsh

set stopOnError 0
set testinv {test-dump.exe test-dump.CMD}
#set testinv [list [list [info nameofexecutable] test-dump.tcl] test-dump.tcl.CMD]
set ::env(TCLSH) [file nativename [info nameofexecutable]]

# e. g. to test another target language {perl test-dump-part.pl ...}
set testPartCall {}
if {[llength ::argv] && [lindex $::argv 0] eq "-external"} {
  set testPartCall [lrange $::argv 1 end]
}

set c 0
set cDiff 0
set cVuln 0
foreach args {
  {test"whoami}     {test""whoami}
  {test"""whoami}   {test""""whoami}

  "test\"whoami\\"     "test\"\"whoami\\"
  "test\"\"\"whoami\\" "test\"\"\"\"whoami\\"
  "test\"whoami\\\\"     "test\"\"whoami\\\\"
  "test\"\"\"whoami\\\\" "test\"\"\"\"whoami\\\\"

  {test\\&\\test}    {test"\\&\\test}
  {"test\\&\\test}   {"test"\\&\\"test"}
  {test\\"&"\\test}  {test"\\"&"\\test}
  {"test\\"&"\\test} {"test"\\"&"\\"test"}

  {test\"&whoami}    {test"\"&whoami}
  {test""\"&whoami}  {test"""\"&whoami}
  {test\"\&whoami}   {test"\"\&whoami}
  {test""\"\&whoami} {test"""\"\&whoami}

  {test&whoami}    {test|whoami}
  {"test&whoami}   {"test|whoami}
  {test"&whoami}   {test"|whoami}
  {"test"&whoami}  {"test"|whoami}
  {""test"&whoami} {""test"|whoami}

  {test&echo "}    {test|echo "}
  {"test&echo "}   {"test|echo "}
  {test"&echo "}   {test"|echo "}
  {"test"&echo "}  {"test"|echo "}
  {""test"&echo "} {""test"|echo "}

  {test&echo ""}    {test|echo ""}
  {"test&echo ""}   {"test|echo ""}
  {test"&echo ""}   {test"|echo ""}
  {"test"&echo ""}  {"test"|echo ""}
  {""test"&echo ""} {""test"|echo ""}

  {test>whoami}     {test<whoami}
  {"test>whoami}    {"test<whoami}
  {test">whoami}    {test"<whoami}
  {"test">whoami}   {"test"<whoami}
  {""test">whoami}  {""test"<whoami}
  {test(whoami)}    {test(whoami)}
  {test"(whoami)}   {test"(whoami)}
  {test^whoami}     {test^^echo ^^^}
  {test"^whoami}    {test"^^echo ^^^}
  {test"^echo ^^^"} {test""^echo" ^^^"}

  {test%USERDOMAIN%\%USERNAME%}
  {test" %USERDOMAIN%\%USERNAME%}
  {test%USERDOMAIN%\\%USERNAME%}
  {test" %USERDOMAIN%\\%USERNAME%}
  {test%USERDOMAIN%&%USERNAME%}
  {test" %USERDOMAIN%&%USERNAME%}
  {test%USERDOMAIN%\&\%USERNAME%}
  {test" %USERDOMAIN%\&\%USERNAME%}

  {test%USERDOMAIN%\&\test}
  {test" %USERDOMAIN%\&\test}
  {test%USERDOMAIN%\\&\\test}
  {test" %USERDOMAIN%\\&\\test}

  {test%USERDOMAIN%\&\"test}
  {test" %USERDOMAIN%\&\"test}
  {test%USERDOMAIN%\\&\\"test}
  {test" %USERDOMAIN%\\&\\"test}
} {
  puts [string repeat - 20]
  # enclose test-arg between 1st/3rd to be sure nothing is truncated 
  # (e. g. to cover unexpected trim by nts-zero case, and args don't recombined):
  set args [list "1st" $args "3rd"]
  unset -nocomplain prevr
  set efile [lindex $testinv 0 end]
  foreach cmd $testinv {
    puts -nonewline [format "*%3d) \x60%s\xb4" \
      $c [join [list test-dump[file extension [lindex $cmd 0]] {*}$args] "\xb4 \x60"]]
    # if external (call another language):
    if {[llength $testPartCall]} {
      set cmd [linsert $cmd 0 {*}$testPartCall]
    }
    # exec:
    if {[catch {
      set r [exec {*}$cmd {*}$args]
    } r]} {
      set r "ERROR: $r"
    }
    if {![info exists prevr] || $prevr eq $r} {
      if {$r eq [set e "    \x60[join [list $efile {*}$args] "\xb4 \x60"]\xb4"]} {
        puts ""
      } else {
        incr cDiff
        if {$stopOnError} {
          puts " -- *DIFFERENT*, result:\n$r\nexpected:\n$e"
          exit -1
        }
        puts " -- *DIFFERENT*"
      }
    } else {
      incr cVuln
      puts " -- *VULNERABLE*"
      if {$stopOnError} {
        exit -1
      }
    }
    set prevr $r
    puts [regsub -all -line {^} $r "  "]
  }
  incr c
}

puts "\nDone.[
  if {$cDiff} {format " - %d case(s) DIFFERENT/VULNERABLE" $cDiff}
][
  if {$cVuln} {format " - %d case(s) VULNERABLE on batch" $cVuln}
]"
