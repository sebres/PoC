#!/usr/bin/env tclsh
catch { puts -nonewline "    `[file tail $::argv0]´ `[join $::argv "´ `"]´"; flush stdout }