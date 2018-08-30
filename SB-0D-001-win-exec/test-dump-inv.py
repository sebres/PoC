#!/usr/bin/env python

import subprocess
import sys

_out = sys.stdout.write

def _uni_decode(res):
  if isinstance(res, str): # py-2.x
    return res.decode('cp1252').encode('cp1252')
  else: # py-3.x
    return str(res, 'cp1252')


exeFiles = [
  "test-dump.exe", "test-dump.CMD" 
];
Args = [
  r'test"whoami',     r'test""whoami',
  r'test"""whoami',   r'test""""whoami',

  'test"whoami\\',     'test""whoami\\',
  'test"""whoami\\',   'test""""whoami\\',
  'test"whoami\\\\',   'test""whoami\\\\',
  'test"""whoami\\\\', 'test""""whoami\\\\',

  r'test\\&\\test',    r'test"\\&\\test',
  r'"test\\&\\test',   r'"test"\\&\\"test"',
  r'test\\"&"\\test',  r'test"\\"&"\\test',
  r'"test\\"&"\\test', r'"test"\\"&"\\"test"',

  r'test\"&whoami',    r'test"\"&whoami',
  r'test""\"&whoami',  r'test"""\"&whoami',
  r'test\"\&whoami',   r'test"\"\&whoami',
  r'test""\"\&whoami', r'test"""\"\&whoami',

  r'test&whoami',     r'test|whoami',
  r'"test&whoami',    r'"test|whoami',
  r'test"&whoami',    r'test"|whoami',
  r'"test"&whoami',   r'"test"|whoami',
  r'""test"&whoami',  r'""test"|whoami',

  r'test&echo "',     r'test|echo "',
  r'"test&echo "',    r'"test|echo "',
  r'test"&echo "',    r'test"|echo "',
  r'"test"&echo "',   r'"test"|echo "',
  r'""test"&echo "',  r'""test"|echo "',

  r'test&echo ""',    r'test|echo ""',
  r'"test&echo ""',   r'"test|echo ""',
  r'test"&echo ""',   r'test"|echo ""',
  r'"test"&echo ""',  r'"test"|echo ""',
  r'""test"&echo ""', r'""test"|echo ""',

  r'test>whoami',     r'test<whoami',
  r'"test>whoami',    r'"test<whoami',
  r'test">whoami',    r'test"<whoami',
  r'"test">whoami',   r'"test"<whoami',
  r'""test">whoami',  r'""test"<whoami',
  r'test(whoami)',    r'test(whoami)',
  r'test"(whoami)',   r'test"(whoami)',
  r'test^whoami',     r'test^^echo ^^^',
  r'test"^whoami',    r'test"^^echo ^^^',
  r'test"^echo ^^^"', r'test""^echo" ^^^"',

  r'test%USERDOMAIN%\%USERNAME%',
  r'test" %USERDOMAIN%\%USERNAME%',
  r'test%USERDOMAIN%\\%USERNAME%',
  r'test" %USERDOMAIN%\\%USERNAME%',
  r'test%USERDOMAIN%&%USERNAME%',
  r'test" %USERDOMAIN%&%USERNAME%',
  r'test%USERDOMAIN%\&\%USERNAME%',
  r'test" %USERDOMAIN%\&\%USERNAME%',

  r'test%USERDOMAIN%\&\test',
  r'test" %USERDOMAIN%\&\test',
  r'test%USERDOMAIN%\\&\\test',
  r'test" %USERDOMAIN%\\&\\test',

  r'test%USERDOMAIN%\&\"test',
  r'test" %USERDOMAIN%\&\"test',
  r'test%USERDOMAIN%\\&\\"test',
  r'test" %USERDOMAIN%\\&\\"test',
]


c = cDiff = cVuln = 0
for arg in Args:

  _out(("-"*20)+ "\n");

  prev = None
  for exe in exeFiles:

    _out("*%3d) `%s\xb4 `%s\xb4" % (c, exe, arg));

    cmd = [exe, arg]
    # subprocess.call(cmd)
    p = subprocess.Popen(cmd, shell=False, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = p.communicate()

    res = _uni_decode(stdout)
    if stderr:
      res += " ERROR: " + _uni_decode(stderr)
    elif p.returncode != 0:
      res += " ERROR: (" + p.returncode +")"

    if (res == ("    `%s\xb4 `%s\xb4" % (exeFiles[0], arg))):
      _out("\n");
    else:
      if (prev is None or res == prev):
        cDiff += 1
        _out(" -- *DIFFERENT*\n");
      else:
        cVuln += 1
        _out(" -- *VULNERABLE*\n");
      _out("  " + res + "\n");
      sys.exit(-1);

    prev = res;

    _out("  " + res + "\n");

  c += 1;

_out("Done."
  + ((" - %d case(s) DIFFERENT/VULNERABLE" % cDiff) if cDiff else "")
  + ((" - %d case(s) VULNERABLE on batch" % cVuln) if cVuln else "")
);
