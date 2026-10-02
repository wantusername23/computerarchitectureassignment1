# DINO CPU

This repository contains the template code for the DINO CPU labs.

**To get started, read `assignments/assignment-*.md`.**
It explains how to set up the development environment, what to implement, how to test your code, and how to submit.

## Directory structure

```
dinocpu/
├── assignments/            Lab documents and the diagram worksheet
├── documentation/          Chisel notes, testing, and single-stepping guides
├── src/
│   ├── main/scala/
│   │   ├── components/     CPU components (ALU control, control unit, next PC, ...)
│   │   ├── single-cycle/   The single-cycle CPU (cpu.scala)
│   │   ├── memory/         Instruction and data memory
│   │   └── testing/        Test driver and the list of test cases
│   └── test/
│       ├── scala/labs/     Lab tests (Lab1Test.scala)
│       └── resources/      RISC-V test programs
├── build.sbt               sbt build configuration
├── dockerfiles/            Dockerfile for the development environment image
└── .devcontainer/          VS Code development container configuration
```

The files you need to modify are listed in the assignment document.
Do not modify the other files.
