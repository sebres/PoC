import scala.sys.process._

object test_dump_inv extends App {

  def _out(msg: String) {
    print(msg);
  }

  val exeFiles = Array(
    "test-dump.exe", "test-dump.CMD" 
  );
  val Args = Array(
      // "test whoami",

      "test\"whoami",       "test\"\"whoami",
      "test\"\"\"whoami",   "test\"\"\"\"whoami",
      
      "test\"whoami\\",       "test\"\"whoami\\",
      "test\"\"\"whoami\\",   "test\"\"\"\"whoami\\",
      "test\"whoami\\\\",     "test\"\"whoami\\\\",
      "test\"\"\"whoami\\\\", "test\"\"\"\"whoami\\\\",

      "test\\\\&\\\\test",       "test\"\\\\&\\\\test",
      "\"test\\\\&\\\\test",     "\"test\"\\\\&\\\\\"test\"",
      "test\\\\\"&\"\\\\test",   "test\"\\\\\"&\"\\\\test",
      "\"test\\\\\"&\"\\\\test", "\"test\"\\\\\"&\"\\\\\"test\"",

      "test\\\"&whoami",       "test\"\\\"&whoami",
      "test\"\"\\\"&whoami",   "test\"\"\"\\\"&whoami",
      "test\\\"\\&whoami",     "test\"\\\"\\&whoami",
      "test\"\"\\\"\\&whoami", "test\"\"\"\\\"\\&whoami",
      
      "test&whoami",       "test|whoami",
      "\"test&whoami",     "\"test|whoami",
      "test\"&whoami",     "test\"|whoami",
      "\"test\"&whoami",   "\"test\"|whoami",
      "\"\"test\"&whoami", "\"\"test\"|whoami",

      "test&echo \"",         "test|echo \"",
      "\"test&echo \"",       "\"test|echo \"",
      "test\"&echo \"",       "test\"|echo \"",
      "\"test\"&echo \"",     "\"test\"|echo \"",
      "\"\"test\"&echo \"",   "\"\"test\"|echo \"",

      "test&echo \"\"",       "test|echo \"\"",
      "\"test&echo \"\"",     "\"test|echo \"\"",
      "test\"&echo \"\"",     "test\"|echo \"\"",
      "\"test\"&echo \"\"",   "\"test\"|echo \"\"",
      "\"\"test\"&echo \"\"", "\"\"test\"|echo \"\"",

      "test>whoami",       "test<whoami",
      "\"test>whoami",     "\"test<whoami",
      "test\">whoami",     "test\"<whoami",
      "\"test\">whoami",   "\"test\"<whoami",
      "\"\"test\">whoami", "\"\"test\"<whoami",
      "test(whoami)",      "test(whoami)",
      "test\"(whoami)",    "test\"(whoami)",
      "test^whoami",       "test^^echo ^^^",
      "test\"^whoami",     "test\"^^echo ^^^",
      "test\"^echo ^^^\"", "test\"\"^echo\" ^^^\"",

      "test%USERDOMAIN%\\%USERNAME%",
      "test\" %USERDOMAIN%\\%USERNAME%",
      "test%USERDOMAIN%\\\\%USERNAME%",
      "test\" %USERDOMAIN%\\\\%USERNAME%",
      "test%USERDOMAIN%&%USERNAME%",
      "test\" %USERDOMAIN%&%USERNAME%",
      "test%USERDOMAIN%\\&\\%USERNAME%",
      "test\" %USERDOMAIN%\\&\\%USERNAME%",

      "test%USERDOMAIN%\\&\\test",
      "test\" %USERDOMAIN%\\&\\test",
      "test%USERDOMAIN%\\\\&\\\\test",
      "test\" %USERDOMAIN%\\\\&\\\\test",
      
      "test%USERDOMAIN%\\&\\\"test",
      "test\" %USERDOMAIN%\\&\\\"test",
      "test%USERDOMAIN%\\\\&\\\\\"test",
      "test\" %USERDOMAIN%\\\\&\\\\\"test"
  );

  var c = 0;
  var cDiff = 0;
  var cVuln = 0;
  for (arg <- Args) {

    _out(("-"*20)+ "\n");

    var prev : String = null;
    for (exe <- exeFiles) {

      _out("*%3d) `%s\u00b4 `%s\u00b4".format(c, exe, arg));

      var stdout = new StringBuilder;
      var stderr = new StringBuilder;
      var res : String = "";
      try {
        Seq(exe, arg) ! ProcessLogger(stdout append _, stderr append _);
        res = stdout.toString + stderr.toString;
      } catch { 
        case e: RuntimeException => None
        case e: Throwable => res += s"ERROR: $e" 
      }
      
      if (res == "    `%s\u00b4 `%s\u00b4".format(exeFiles(0), arg)) {
        _out("\n");
      } else {
        if (prev == null || res == prev) {
          cDiff += 1;
          _out(" -- *DIFFERENT*\n");
          //System.exit(-1);
        } else {
          cVuln += 1;
          _out(" -- *VULNERABLE*\n");
        }
      }
      prev = res;

      _out("  " + res + "\n");

    }
    c += 1;
  }

  _out("\nDone." 
    + (if (cDiff > 0) " - %d case(s) DIFFERENT/VULNERABLE".format(cDiff) else "") 
    + (if (cVuln > 0) " - %d case(s) VULNERABLE on batch".format(cVuln) else "")
  );
}
