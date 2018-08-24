package main 

import (
  "os"
  "os/exec"
)

func main() {
  
  args := os.Args[2:]
  cmd := exec.Command(os.Args[1], args...)

  cmd.Stdout = os.Stdout
  cmd.Stderr = os.Stderr

  err := cmd.Start()
  if err != nil {
    os.Exit(1)
  }

}
