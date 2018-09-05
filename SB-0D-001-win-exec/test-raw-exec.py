import os, subprocess
def rexec(cmdArgs, exe='test-dump-details.cmd', shell=False, env=None):
  """ Raw execution to test proper escape (unpaired quotation mark, meta-chars, etc) """
  env = env if env else os.environ.copy()
  env.update({
    'X'    : 'simple-X',
    '"X"'  : 'quoted-X',
    '""X""': 'douquo-X',
    '"X'   : 'quolef-X',
    'X"'   : 'quorig-X',
    '"X^'  : 'quocfl-X',
    '^X"'  : 'cflquo-X',
    'x22'  : '"',
    'x20'  : ' ',
  })
  return subprocess.call((exe + ' ' if exe else '') + cmdArgs, shell=shell, env=env)
