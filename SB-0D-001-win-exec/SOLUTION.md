## [SB-0D-001-win-exec] - Solution resp. algorithm description

### 0-day vulnerability "insufficient escape of special chars for quoting of arguments by exec process" by almost all languages for windows.

For possible example of implementation of this solution see [how it was fixed in TCL](https://core.tcl-lang.org/tcl/vdiff?from=core-8-5-branch&to=0-day-21b0629c81) (see the function `BuildCommandLine`)

## Algorithm:

This algorithm used for quoting/escape of process arguments for windows, regardless the type of process (PE-executable, batch file, etc).

### Definition:

- SpecMetaChars = `&|^<>!()%` - characters to enclose in quotes if unpaired quote flag (`UNPAIRED`) set
- SpecMetaChars2 = `%` - characters to enclose in quotes in any case (regardless unpaired-flag)
- Space - space characters (space `\x20`, tab `\x09`, etc).
- Quote = `"` - character to handle seperatelly and to count paired/unpaired occurrence (across all arguments)
- BackSlash = `\` - character to escape if followed by `"` (and still one `\` if it also followed by `"`)
- Flags:
  *  `ESCAPE`   - escape argument;
  *  `QUOTE`    - enclose in quotes;
  *  `UNPAIRED` - previous arguments chain contains unpaired quote-char;

### Description:
```
Main-routine BuildCommandLine:

  Reset all flags.
  Set result CmdLine to empty string.

  Main-Cycle - For each argument suplied:
  
    If not first argument, append space-char to CmdLine.
  
    Reset flags QUOTE and ESCAPE.
  
    Analyse the argument:
      - if argument is empty - set flag QUOTE;
      - if argument contains Space - set flag QUOTE;
      - if argument contains SpecMetaChars - set flags QUOTE and ESCAPE;
      - if argument contains Quote - set flag ESCAPE;
      - if argument contains BackSlash - if flag QUOTE set (or will be set), set ESCAPE flag also;
      
    Process argument:
      If flag QUOTE set, append to CmdLine a MAIN opening quote-char;
    
      If flag ESCAPE set, append to CmdLine the argument as is;
      Else:
        Do-Escape - For each argument character:
          - if char is BackSlash, notice current possition as BSPOS and bypass all adjacent BackSlash chars;
          - if char is Quote:
            * invert flag UNPAIRED;
            * add unprocessed argument-part before, thereby if BSPOS was set, 
              double-escape all backslashes from BSPOS as `\\` before this quote, then reset BSPOS;
            * append with backslash escaped quote-char `\"` and continue;
          - if flag UNPAIRED is set and char in SpecMetaChars, do special handling on meta-chars:
            so execute sub-routine QuoteCmdLinePart with SpecCharsTable = SpecMetaChars and continue;
          - if char in SpecMetaChars2, do special handling on meta-chars:
            so execute sub-routine QuoteCmdLinePart with SpecCharsTable = SpecMetaChars2 and continue;
          - otherwise reset BSPOS and continue Do-Escape
    
      Append rest (unprocessed argument-part), thereby if flag QUOTE set and BSPOS was set, 
      double-escape all backslashes from BSPOS as `\\`, because this gets new MAIN quote hereafter; */
    
      If flag QUOTE set, append to CmdLine a MAIN closing quote-char;
      
  End of Main-Cycle
    
End of BuildCommandLine.
```
```
Sub-routine QuoteCmdLinePart (SpecCharsTable):
  
  Add unprocessed argument-part before, thereby if BSPOS was set, 
  double-escape all backslashes from BSPOS as `\\`, because this gets new quote, then reset BSPOS;
  
  Add opening quote-char (to escape next chars outside of MAIN quotes).
            
  Do-Specia-Escape - For each char from current position:
    - if char belongs to SpecCharsTable, bypass it;
    - if char is BackSlash, notice current possition as BSPOS and bypass all adjacent BackSlash chars;
    - otherwise stop Do-Specia-Escape cycle.
    
  Append found chars (all chars belonging to SpecCharsTable and backslashes), thereby if BSPOS was set, 
  double-escape all backslashes from BSPOS as `\\`, because this gets new quote hereafter;
    
  Add closing (paired) quote-char.
  
End of QuoteCmdLinePart.
```
### Notes:
   - unpaired (escaped) quote causes special handling on meta-chars `&|^<>!()%`, at that the flag UNPAIRED is valid
     across all agruments, so it is not reset until end of argument processing (each quote found in arguments will 
     just invert this flag);
   - there is a char `%` that should be always enclosed in quotes (regardless state of flag `UNPAIRED`);
   - adjacent backslashes should be escaped only if followed by `"` (already in argument or new one because of special quoted-escape);
