# DINO CPU Assignment 1

Before you start, read [Assignment 0](./assignment-0.md) for an introduction to the DINO CPU and how to set up the development environment.

## Table of Contents

* [Part I: Implement the ALU Control](#part-i-implement-the-alu-control)
  * [Testing your ALU control unit](#testing-your-alu-control-unit)
* [Part II: Implement the Control unit and Next PC](#part-ii-implement-the-control-unit-and-next-pc)
  * [Testing the control unit](#testing-the-control-unit)
* [Part III: Implement the ADD instruction](#part-iii-implement-the-add-instruction)
  * [Testing your ADD instruction](#testing-your-add-instruction)
* [Part IV: Implementing the rest of the R-type and I-type instructions](#part-iv-implementing-the-rest-of-the-r-type-and-i-type-instructions)
  * [Testing the rest of the instructions](#testing-the-rest-of-the-instructions)
* [Part V: Moving on to multiple cycles](#part-v-moving-on-to-multiple-cycles)
  * [Testing](#testing)
* [Grading](#grading)
* [Submission](#submission)
  * [What to submit](#what-to-submit)
  * [How to package](#how-to-package)
* [Academic misconduct reminder](#academic-misconduct-reminder)
* [Printf debugging](#printf-debugging)

## Part I: Implement the ALU Control

**The test for this part is `dinocpu.ALUControlTesterLab1`.**

In this part you will be implementing a component in the CPU design.

The DINO CPU is a **64-bit** RISC-V CPU (RV64I).
Registers, the PC, and the ALU are all 64 bits wide, while each instruction is 32 bits.

The ALU has already been implemented for you (`src/main/scala/components/alu.scala`).
It takes three inputs: the `operation`, and two inputs, `inputx` and `inputy`.
It generates the `result` of the operation on the two inputs.

The `operation` input is 5 bits.
The lower 4 bits choose the operation, and the top bit selects the 32-bit "word" version.
A word operation uses only the lower 32 bits of the inputs and sign-extends the 32-bit result to 64 bits.

| operation | result | operation | result |
|-----------|--------|-----------|--------|
| `00000`   | xor    |           |        |
| `00001`   | sltu   |           |        |
| `00010`   | srl    | `10010`   | srlw   |
| `00011`   | sra    | `10011`   | sraw   |
| `00100`   | sub    | `10100`   | subw   |
| `00101`   | or     |           |        |
| `00110`   | and    |           |        |
| `00111`   | add    | `10111`   | addw   |
| `01000`   | sll    | `11000`   | sllw   |
| `01001`   | slt    |           |        |
| `11111`   | invalid |          |        |

In Lab 1 you will implement only the **R-type** and **I-type arithmetic/logic** instructions listed in the tables below, including their 32-bit word versions.
(Loads, stores, branches, and jumps come in Lab 2.)
Using these encodings, implement the control that chooses the right ALU operation for each instruction.

| funct7 (31--25) | rs2 (24--20) | rs1 (19--15) | funct3 (14--12) | rd (11--7) | opcode (6--0) | R-type |
|-----------------|--------------|--------------|-----------------|------------|---------------|--------|
| 0000000 | rs2    | rs1    | 000    | rd    | 0110011   | ADD    |
| 0100000 | rs2    | rs1    | 000    | rd    | 0110011   | SUB    |
| 0000000 | rs2    | rs1    | 001    | rd    | 0110011   | SLL    |
| 0000000 | rs2    | rs1    | 010    | rd    | 0110011   | SLT    |
| 0000000 | rs2    | rs1    | 011    | rd    | 0110011   | SLTU   |
| 0000000 | rs2    | rs1    | 100    | rd    | 0110011   | XOR    |
| 0000000 | rs2    | rs1    | 101    | rd    | 0110011   | SRL    |
| 0100000 | rs2    | rs1    | 101    | rd    | 0110011   | SRA    |
| 0000000 | rs2    | rs1    | 110    | rd    | 0110011   | OR     |
| 0000000 | rs2    | rs1    | 111    | rd    | 0110011   | AND    |
| 0000000 | rs2    | rs1    | 000    | rd    | 0111011   | ADDW   |
| 0100000 | rs2    | rs1    | 000    | rd    | 0111011   | SUBW   |
| 0000000 | rs2    | rs1    | 001    | rd    | 0111011   | SLLW   |
| 0000000 | rs2    | rs1    | 101    | rd    | 0111011   | SRLW   |
| 0100000 | rs2    | rs1    | 101    | rd    | 0111011   | SRAW   |

| imm[11:0] (31--20) | rs1 (19--15) | funct3 (14--12) | rd (11--7) | opcode (6--0) | I-type |
|--------------------|--------------|-----------------|------------|---------------|--------|
| imm[11:0]    | rs1    | 000    | rd    | 0010011   | ADDI   |
| imm[11:0]    | rs1    | 010    | rd    | 0010011   | SLTI   |
| imm[11:0]    | rs1    | 011    | rd    | 0010011   | SLTIU  |
| imm[11:0]    | rs1    | 100    | rd    | 0010011   | XORI   |
| imm[11:0]    | rs1    | 110    | rd    | 0010011   | ORI    |
| imm[11:0]    | rs1    | 111    | rd    | 0010011   | ANDI   |
| 000000 shamt[5:0] | rs1    | 001    | rd    | 0010011   | SLLI   |
| 000000 shamt[5:0] | rs1    | 101    | rd    | 0010011   | SRLI   |
| 010000 shamt[5:0] | rs1    | 101    | rd    | 0010011   | SRAI   |
| imm[11:0]    | rs1    | 000    | rd    | 0011011   | ADDIW  |
| 0000000 shamt[4:0]| rs1    | 001    | rd    | 0011011   | SLLIW  |
| 0000000 shamt[4:0]| rs1    | 101    | rd    | 0011011   | SRLIW  |
| 0100000 shamt[4:0]| rs1    | 101    | rd    | 0011011   | SRAIW  |

I-types do not have a `funct7` field; bits 31--25 are the upper bits of the immediate.
However, the ALU control's `funct7` input is always connected to bits 31--25 of the instruction, so for I-types it receives these immediate bits.
Two things are tricky because of this:

* For most I-types, bits 31--25 are just part of the immediate. For example, `addi` with a negative immediate has `1`s in those bits, but it is still an add, not a sub.
* For the shifts, the upper immediate bits select the shift type. In RV64, the shift amount (`shamt`) of `slli`/`srli`/`srai` is 6 bits, so bit 25 belongs to `shamt`, and only bits 31--26 tell `srli` and `srai` apart.
  The word versions (`slliw`/`srliw`/`sraiw`) shift a 32-bit value, so their `shamt` is only 5 bits, and bits 31--25 tell `srliw` and `sraiw` apart.

The ALU control takes five inputs:
* `aluop`, `itype`, and `wordinst`, which come from the control unit (you will implement it in [Part II](#part-ii-implement-the-control-unit-and-next-pc))
  * `aluop` is true for R-type and I-type instructions (all instructions in Lab 1)
  * `itype` is true for I-type instructions
  * `wordinst` is true for the 32-bit word instructions (`addw`, `addiw`, `sllw`, ...)
* `funct7` and `funct3`, which come from the instruction

Given these inputs, you must generate the correct output on the `operation` wire.
The template code from `src/main/scala/components/alucontrol.scala` is shown below.
Remove the line marked `PLACEHOLDER` and fill in where it says *TODO*.

```scala
class ALUControl extends Module {
  val io = IO(new Bundle {
    val aluop     = Input(Bool())
    val itype     = Input(Bool())
    val funct7    = Input(UInt(7.W))
    val funct3    = Input(UInt(3.W))
    val wordinst  = Input(Bool())

    val operation = Output(UInt(5.W))
  })

  io.operation := "b11111".U  // PLACEHOLDER

  // TODO: Select the ALU operation from funct3, funct7, itype, and wordinst.
  //       See alu.scala for the encoding of each operation.
}
```

**HINT:** Use Chisel's `when` / `elsewhen` / `otherwise`, or `MuxCase` syntax.
You may also find the [Chisel cheat sheet](../documentation/chisel-notes/cheat-sheet.md) helpful.

### Testing your ALU control unit

We have implemented some tests for your ALU control unit.
The Lab 1 tests are in `src/test/scala/labs/Lab1Test.scala`.

To run all of the Lab 1 tests, start sbt (see [Starting and exiting sbt](./assignment-0.md#starting-and-exiting-sbt) in Assignment 0) and execute the following at the sbt prompt:

```
sbt:dinocpu> Lab1 / test
```

If you try this before implementing anything, you'll see that most of the tests fail.
The end of the output looks like the following:

```
[info] Total number of tests run: 18
[info] Suites: completed 6, aborted 0
[info] Tests: succeeded 1, failed 17, canceled 0, ignored 0, pending 0
[info] *** 17 TESTS FAILED ***
[error] Failed: Total 18, Failed 17, Errors 0, Passed 1
[error] Failed tests:
[error]   dinocpu.SingleCycleITypeTesterLab1
[error]   dinocpu.SingleCycleRTypeTesterLab1
[error]   dinocpu.SingleCycleAddTesterLab1
[error]   dinocpu.ControlTesterLab1
[error]   dinocpu.ALUControlTesterLab1
[error]   dinocpu.SingleCycleMultiCycleTesterLab1
[error] (Lab1 / test) sbt.TestsFailedException: Tests unsuccessful
```

This is expected.
A test may pass by accident, because its expected register values happen to be the initial values.

In this part of the assignment, you only need to run the ALU control unit tests.
To run just these tests, you can use the sbt command `testOnly`, as demonstrated below.

```
sbt:dinocpu> Lab1 / testOnly dinocpu.ALUControlTesterLab1
```

Each failed case prints the name of the instruction, the value your ALU control produced, and the expected value:

```
[info] [0.003] EXPECT AT 1 add wrong  io_operation got 31 expected 7 FAIL
[info] [0.003] EXPECT AT 2 sub wrong  io_operation got 31 expected 4 FAIL
...
```

Feel free to add your own tests in `src/test/scala`, modify the current tests, and add `print` statements in the tests.

## Part II: Implement the Control unit and Next PC

**The test for this part is `dinocpu.ControlTesterLab1`.**

Before you wire up the CPU, you will fill in two more small components.

### Control unit

The control unit (`src/main/scala/components/control.scala`) looks at the `opcode` (bits 6--0 of the instruction) and generates the control signals for the rest of the CPU.
It is written as a table: each row matches one opcode and lists the value of every control signal.
Opcodes that are not in the table get the `default` row, which sets every signal to `false`/0.

```scala
  val signals =
    ListLookup(io.opcode,
      /*default*/           List(false.B, false.B, false.B,  0.U,  false.B,       0.U,      false.B,   0.U, false.B,  false.B,   false.B,  false.B),
      Array[(BitPat, List[UInt])](/* itype,   aluop,    src1, src2,   branch,  jumptype, resultselect, memop,   toreg, regwrite, validinst, wordinst */

      // PLACEHOLDER
      BitPat("b1111111") -> List(false.B, false.B, false.B,  0.U,  false.B,       0.U,      false.B,   0.U, false.B,  false.B,   false.B,  false.B),

      // TODO: Add one row per opcode that sets every control signal.
      ) // Array
    ) // ListLookup
```

You need to add a row for each of the following opcodes:

| opcode    | instructions                          |
|-----------|---------------------------------------|
| `0110011` | R-type (`add`, `sub`, ...)            |
| `0111011` | 32-bit R-type (`addw`, `subw`, ...)   |
| `0010011` | I-type (`addi`, `slli`, ...)          |
| `0011011` | 32-bit I-type (`addiw`, `slliw`, ...) |

The meaning of each signal is described in the comment at the top of `control.scala`.
In Lab 1, the signals for branches, jumps, and memory (`src1`, `branch`, `jumptype`, `resultselect`, `memop`, and `toreg`) should all be 0.
The test checks **every** signal, so make sure each one has the right value.

### Next PC

The next PC unit (`src/main/scala/components/nextpc.scala`) computes the address of the next instruction.
Since there are no branches or jumps in Lab 1, the next PC is always the current PC plus 4.
Remove the `PLACEHOLDER` line and implement this where it says *TODO*.
You will extend this unit for branches and jumps in Lab 2.
There is no separate test for the next PC unit; it is tested by the multi-cycle programs in [Part V](#part-v-moving-on-to-multiple-cycles).

### Testing the control unit

```
sbt:dinocpu> Lab1 / testOnly dinocpu.ControlTesterLab1
```


## Part III: Implement the ADD instruction

**The test for this part is `dinocpu.SingleCycleAddTesterLab1`.**

Now you're ready to implement your first instruction!
For this part of the assignment, you will modify the `src/main/scala/single-cycle/cpu.scala` file.
You are beginning to implement the DINO CPU!

In `cpu.scala`, you will find all of the components of the single-cycle CPU (control, registers, ALU control, ALU, immediate generator, next PC).
The components are all instantiated Chisel `Module`s.

Notice that in the template code the IO for each module is set to `DontCare` on a line marked `PLACEHOLDER`.
This allows the Chisel code to compile, but it also means these modules are optimized away when generating the hardware.
Remove each `PLACEHOLDER` line as you hook up that module.
Leave the line `io.dmem := DontCare  // Do not modify` as it is, because Lab 1 does not use the data memory.

We have given you the instruction fetch:

```scala
  io.imem.address := pc
  io.imem.valid := true.B

  val instruction = Wire(UInt(32.W))
  when ((pc % 8.U) === 4.U) {
    instruction := io.imem.instruction(63, 32)
  } .otherwise {
    instruction := io.imem.instruction(31, 0)
  }
```

This sends the PC to the instruction memory.
The instruction memory returns 64 bits (two instructions) at a time, so the code picks the upper or lower 32 bits based on the PC.
Use the `instruction` wire in the rest of your design.

You should fill in the other wires (and instruction subsets) that are required to implement the `add` RISC-V instruction.

**Important**: You will not need to modify anything below the `Object to make it easier to print information about the CPU` line.
This code should be kept the same for everyone.

You may add debug code, as you are working on the assignment.
However, when you turn in your assignment, please comment out or remove your debug code before submitting.

### Testing your ADD instruction

Testing the CPU is very similar to testing your control unit [above](#testing-your-alu-control-unit).
To run the tests, you execute the `SingleCycleAddTesterLab1` suite as follows.

```
sbt:dinocpu> Lab1 / testOnly dinocpu.SingleCycleAddTesterLab1
```

This runs three very simple RISC-V applications (`add0`, `add1`, and `add2`) that each have a single `add` instruction.

> **Important: register 0 must always be 0.**
> In RISC-V, register 0 (`zero`) always holds 0, and writing to it has no effect.
> `add0` writes its result to register 0 and checks that the value is still 0 afterward.
> Make sure your CPU never writes to register 0, even when an instruction's `rd` is 0.

You can find the code for `add1` in `src/test/resources/risc-v/add1.riscv` and below:

```
  .text
  .align 2       # Make sure we're aligned to 4 bytes
  .globl _start
_start:
    add t1, zero, t0 # (reg[6] = 0 + reg[5])

    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
_last:
```

(There are a number of `nop` instructions at the end for testing the pipelined CPU in Lab 3.
You can ignore them for now.)

When you get the correct answer, you should see the following output.

```
sbt:dinocpu> Lab1 / testOnly dinocpu.SingleCycleAddTesterLab1
...
[info] SingleCycleAddTesterLab1:
[info] Single Cycle CPU
[info] - should run add test add0
[info] - should run add test add1
[info] - should run add test add2
[info] ScalaTest
[info] Total number of tests run: 3
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 3, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[info] Passed: Total 3, Failed 0, Errors 0, Passed 3
```

Each test only runs for a single cycle, since you're just executing one instruction.

The test for `add1` initializes `t0` to 1234.
You can see this in `src/main/scala/testing/InstTests.scala` (line 31), where the `add1` test case is defined:

```scala
        CPUTestCase("add1",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> 1234),
                                Map(0 -> 0, 5 -> 1234, 6 -> 1234),
                                Map(), Map()),
```

This creates a `CPUTestCase` that:
* runs the `add1` program
* runs for 1 cycle on the `single-cycle` CPU (the other CPU types are for later labs)
* initializes the `t0` register (register 5) to 1234
* checks that `zero` is 0, `t0` is 1234, and `t1` is 1234
* doesn't initialize any memory addresses
* doesn't check any memory addresses

More information about `CPUTestCase` can be found in `src/main/scala/testing/CPUTesterDriver.scala` and in the [DINO CPU testing documentation](../documentation/testing.md).

You can also use the [single stepper](../documentation/single-stepping.md) to step through the execution one cycle at a time and print information as you go.
An example on how to use it is shown below.

First, you can start the single stepper program:

```
sbt:dinocpu> runMain dinocpu.singlestep add1 single-cycle
```

This will compile the `single-cycle` CPU design (which is what you're currently working on) and run the application/test `add1` on that CPU design.

After you start the program, you'll see a command prompt:

```
Single stepper>
```

Here, you can print registers, dump I/O for modules, and step the processor through multiple cycles.

For instance, if you have the correct design, you should see the following:

```
Single stepper> dump registers
registers.io.readdata1         0 (0x0)
registers.io.readdata2         1234 (0x4d2)
registers.io.readreg1          0 (0x0)
registers.io.writereg          6 (0x6)
registers.io.readreg2          5 (0x5)
registers.io.writedata         1234 (0x4d2)
registers.io.wen               1 (0x1)
```

This is saying that you're reading registers 0 and 5, the value in register 0 is 0, the value in register 5 is 1234, you're writing register 6, the value you're writing is 1234, and you're asserting `wen`.

Similarly, the ALU wires should look like the following (`7` is `00111`, the add operation):

```
Single stepper> dump alu
alu.io.inputx                  0 (0x0)
alu.io.result                  1234 (0x4d2)
alu.io.inputy                  1234 (0x4d2)
alu.io.operation               7 (0x7)
```

Finally, you can also print the value of specific registers.
`t0` is register 5 and `t1` is register 6.
So, let's print these two registers at cycle 0, step one cycle, then print them at cycle 1.

```
Single stepper> print reg 5
reg5: 1234
Single stepper> print reg 6
reg6: 0
Single stepper> step 1
Current cycle: 1
Single stepper> print reg 5
reg5: 1234
Single stepper> print reg 6
reg6: 1234
```

On cycle 1, register 6 is written with 1234, as it should be!

More details on how to use the single stepper can be found in the [documentation](../documentation/single-stepping.md).
You can also write `?` on the command prompt to see the help.


## Part IV: Implementing the rest of the R-type and I-type instructions

**The tests for this part are `dinocpu.SingleCycleRTypeTesterLab1` and `dinocpu.SingleCycleITypeTesterLab1`.**

In this part of the lab, you will be implementing all of the other R-type and I-type instructions from [Part I](#part-i-implement-the-alu-control).
The provided tests include test programs for some of these instructions, each with only that one instruction.
The grading tests cover all of them, so test the others yourself (e.g., with the single stepper).

If you have passed the `add` tests and the ALU control and control unit tests, most of the other R-type instructions should *just work*!
Now, test them to make sure that they do!

For I-types, the second ALU input must be the immediate instead of `rs2`.
The immediate generator (`immGen`) has already been implemented for you: give it the instruction, and it outputs the sign-extended immediate on `sextImm`.
Use the control signals from your control unit to choose the right input.

You may need to update the wires in your CPU design to get all of the instructions to work.
Pay attention to instructions that differ only in `funct7` (e.g., `add`/`sub` and `srl`/`sra`) and to the word versions.

## Testing the rest of the instructions

Testing the CPU is very similar to testing your control unit [above](#testing-your-alu-control-unit).
To run the tests, you execute the suites as follows:

```
sbt:dinocpu> Lab1 / testOnly dinocpu.SingleCycleRTypeTesterLab1
sbt:dinocpu> Lab1 / testOnly dinocpu.SingleCycleITypeTesterLab1
```

These will load some binary applications from `src/test/resources/risc-v`.
The application that each test runs is shown in the output.
Below is an example of a test that failed:

```
[info] SingleCycleRTypeTesterLab1:
[info] Single Cycle CPU
[info] - should run R-type instruction sub *** FAILED ***
...
```

This ran an **R-type** application which used the binary **sub**.
You can view the RISC-V assembly for this application in `src/test/resources/risc-v/sub.riscv`.
The list of applications that each suite runs can be found in `src/main/scala/testing/InstTests.scala` (`rtype` and `itype`).

If you want to run only a single application from a suite, you can add a parameter to the `testOnly` sbt task.
You can pass the option `-z` which will execute any tests that match the text given to the parameter.
You must use `--` between the parameters to the sbt task (e.g., the suite to run) and the parameters for testing.
For instance, to only run the subtract test, you would use the following:

```
sbt:dinocpu> Lab1 / testOnly dinocpu.SingleCycleRTypeTesterLab1 -- -z sub
```

Remember, you can also use the `singlestep` application to inspect wires and registers or use [printf debugging](#printf-debugging).

## Part V: Moving on to multiple cycles

**The test for this part is `dinocpu.SingleCycleMultiCycleTesterLab1`.**

Now, let's try a more complicated program that executes more than one instruction.
`addfwd` is one example of this which executes 10 add instructions in a row.

To run these programs correctly, the PC has to move to the next instruction every cycle.
Connect your next PC unit from [Part II](#part-ii-implement-the-control-unit-and-next-pc) so that the `pc` register is updated each cycle.
These programs are starting to be able to do some "real" things.
For instance, `swapxor` swaps two registers without using a temporary register.

```
  .text
  .align 2       # Make sure we're aligned to 4 bytes
  .globl _start
_start: #swaps a and b without a temporary variable
    xor t0, t0, t2 #  t0 =a and t2 = b
    xor t2, t2, t0
    xor t0, t0, t2
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
    nop
_last:
```

### Testing

To run just one test, you can use the `-z` trick from above.

```
sbt:dinocpu> Lab1 / testOnly dinocpu.SingleCycleMultiCycleTesterLab1 -- -z addfwd
```

When everything is done, all 18 Lab 1 tests should pass:

```
sbt:dinocpu> Lab1 / test
...
[info] Tests: succeeded 18, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[info] Passed: Total 18, Failed 0, Errors 0, Passed 18
```

## Grading

Your code will be graded automatically with tests, except for the diagram.
The grading tests check more cases than the tests given to you in `Lab1Test.scala`, so make sure your design is correct for every instruction in [Part I](#part-i-implement-the-alu-control), not just the ones in the provided tests.

| Item                                   | Provided test                      | Points |
|----------------------------------------|------------------------------------|--------|
| ALU control (R-type + I-type)          | `ALUControlTesterLab1`             | 20     |
| Control unit                           | `ControlTesterLab1`                | 10     |
| ADD (`add0`, `add1`, `add2`)           | `SingleCycleAddTesterLab1`         | 5      |
| R-type (all)                           | `SingleCycleRTypeTesterLab1`       | 20     |
| I-type (all)                           | `SingleCycleITypeTesterLab1`       | 20     |
| Multi-cycle programs                   | `SingleCycleMultiCycleTesterLab1`  | 20     |
| Diagram                                | (graded manually)                  | 5      |
| **Total**                              |                                    | **100**|

**There is no partial credit within an item.**
Each item is all or nothing: you get its points only if every case in that item passes.
For example, if one of the I-type instructions fails, you get 0 of the 20 I-type points.

## Submission

**Warning**: read the submission instructions carefully.
Failure to adhere to the instructions will result in a loss of points.

### What to submit

**Code:** the four files that you changed.

* `src/main/scala/components/alucontrol.scala`
* `src/main/scala/components/control.scala`
* `src/main/scala/components/nextpc.scala`
* `src/main/scala/single-cycle/cpu.scala`

Only these four files are used for grading, so any changes to other files are ignored.
**Do not change the I/O of any module.** If you do, your code will not compile with the grading tests and you will receive no credit.

**Diagram:** your datapath diagram, drawn on the [blank worksheet](./assignment-1-worksheet.jpg).
Draw every wire you need for the R-type and I-type instructions, and label each wire with its width in bits (and which bits, for a wire that carries only some of them).
Save it as a single file named `diagram.pdf`, `diagram.jpg`, or `diagram.png` (a clear scan or photo is fine).

### How to package

1. Make a folder named with your student ID (e.g., `2026-12345`).
2. Put the four code files and your diagram directly in that folder (no subfolders).
3. Create a tar file of the folder named `<student ID>.tar` and submit it to the **Lab1 assignment on eTL**.

For example, from the `dinocpu` directory:

```
mkdir 2026-12345
cp src/main/scala/components/alucontrol.scala \
   src/main/scala/components/control.scala \
   src/main/scala/components/nextpc.scala \
   src/main/scala/single-cycle/cpu.scala \
   2026-12345/
cp /path/to/your/diagram.pdf 2026-12345/
tar -cvf 2026-12345.tar 2026-12345
```

Check the contents before you submit:

```
tar -tvf 2026-12345.tar
```

It should list exactly these files (the order may differ):

```
2026-12345/
2026-12345/alucontrol.scala
2026-12345/control.scala
2026-12345/nextpc.scala
2026-12345/cpu.scala
2026-12345/diagram.pdf
```

## Academic misconduct reminder

You are to work on this project **individually**.
You may discuss *high level concepts* with one another (e.g., talking about the diagram), but all work must be completed on your own.

**DO NOT POST YOUR CODE PUBLICLY ON GITHUB!**


## Printf debugging

Printing values with `printf` is the best style of debugging for this assignment.
See [`printf` debugging](../documentation/chisel-notes/first-hardware.md#printf-debugging) in the Chisel tutorial for how to use `printf` and `println`.
