# Testing DINO CPU

This file explains how to run the tests, how the CPU test cases are defined, and how to add your own CPU test cases.
To step through a test one cycle at a time, see [single stepping](single-stepping.md).

To run a test, first start sbt (see [Starting and exiting sbt](../assignments/assignment-0.md#starting-and-exiting-sbt) in Assignment 0).
When sbt is ready, you will see the sbt prompt:

```
sbt:dinocpu>
```

The tests for each lab are in `src/test/scala/labs/` (e.g., `Lab1Test.scala`).
To run all of the tests for a lab, put the lab name and `/` before the `test` command.
For example, to run the Lab 1 tests:

```
sbt:dinocpu> Lab1 / test
```

Until you implement the requirements of the lab, most of the tests will fail.

**Note:** Always use `Lab1 / test`, not just `test`.
Running `test` alone does not run any of the lab tests (`No tests were executed.`).

## Running a single test

The tests are grouped together into "suites".
For the labs, each part of the lab is a single suite (e.g., `dinocpu.ALUControlTesterLab1` or `dinocpu.SingleCycleRTypeTesterLab1`).
To run one suite, use `testOnly` instead of `test`.

For instance, to run just the R-type tests you can use the following.

```
sbt:dinocpu> Lab1 / testOnly dinocpu.SingleCycleRTypeTesterLab1
```

Note: You can use tab completion in sbt to make searching for tests easier.

Each of these suites runs a number of different tests.
When trying to debug a test, you will often want to run just a single test case.
To do that, you can use full text search on the name of the test.
You can pass `-z <search>` to the tester.
**Important**: This must be after `--` to distinguish the parameters.

The name of each test is printed when you run the suite.
For instance, for the R-type suite you will see the following output (on a correct design):

```
[info] SingleCycleRTypeTesterLab1:
[info] Single Cycle CPU
[info] - should run R-type instruction sub
[info] - should run R-type instruction and
[info] - should run R-type instruction or
[info] - should run R-type instruction slt
[info] - should run R-type instruction sll
```

So, let's say you only want to run the test which executes the `add1` application, you can use the following.

```
sbt:dinocpu> Lab1 / testOnly dinocpu.SingleCycleAddTesterLab1 -- -z add1
```

## CPU Test Case

The `InstTests` object in `src/main/scala/testing/InstTests.scala` contains lists of instruction test cases (e.g., `rtype`, `itype`, `rtypeMultiCycle`).
Each test case runs a RISC-V program in `src/test/resources/risc-v`.

Each test case looks like:
 - binary to run in `src/test/resources/risc-v`
 - number of cycles to run for each CPU type
 - initial values for registers
 - final values to check for registers
 - initial values for memory
 - final values to check for memory
 - extra name information (optional)

```
CPUTestCase("binary_name",
            Map("single-cycle" -> n_single, "pipelined" -> n_pipelined),
            Map(rs1 -> data1, rs2 -> data2),
            Map(0 -> 0, rd -> (data1 ? data2), ...),
            Map(), Map())
```

For example, here is `add2.riscv`:

```
  .text
  .align 2       # Make sure we're aligned to 4 bytes
  .globl _start
_start:
    add a0, s4, t0 # (reg[10] = reg[20] + reg[5])

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

In the `CPUTestCase` below, we run the binary `add2`, which is compiled from `add2.riscv` (shown above); both of them are in `src/test/resources/risc-v`.
The `add2.riscv` program adds the contents of `t0` (`reg[5]`) and `s4` (`reg[20]`) and stores the result in `a0` (`reg[10]`).

```
CPUTestCase("add2",
            Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
            Map(5 -> 1234, 20 -> 5678),
            Map(0 -> 0, 10 -> 6912),
            Map(), Map())
```

 - `Map("single-cycle" -> 1, ...)` runs the single-cycle CPU for one cycle. (The other CPU types are for later labs.)
 - `Map(5 -> 1234, 20 -> 5678)` initializes `t0` (`reg[5]`) to 1234 and `s4` (`reg[20]`) to 5678.
 - `Map(0 -> 0, 10 -> 6912)` checks if the contents of `reg[0]` and `a0` are 0 and 6912 respectively.
 - `Map(), Map()` means that no memory is initialized or checked.

The name of a test case is the binary name followed by the extra name (e.g., `add2`, or `add2-addnew` if the extra name is `"-addnew"`).

# Adding your CPU Test Cases

## 1. Editing the given CPU Test Cases

Editing the given CPU Test Cases with new values for initializing and checking registers is very simple.
Consider the test case for `add2.riscv`. If we edit the test case to the following:

```
CPUTestCase("add2",
            Map("single-cycle" -> 1, "pipelined" -> 5),
            Map(5 -> 2048, 20 -> 512),
            Map(0 -> 0, 10 -> 2560),
            Map(), Map())
```

The test case now initializes `t0` (`reg[5]`) to 2048, `s4` (`reg[20]`) to 512, and checks if `a0` (`reg[10]`) has the sum 2560 (= 2048 + 512).

## 2. Adding new tests

Adding a new test takes two steps.

**Step 1:** Add a new `CPUTestCase` to a list in `InstTests.scala`.
If it runs the same binary as an existing test case, give it a unique extra name as the last parameter.
Otherwise, the two test cases have the same name, and only one of them can be used.

```
CPUTestCase("add2",
            Map("single-cycle" -> 1, "pipelined" -> 5),
            Map(5 -> 1234, 20 -> 5678),
            Map(0 -> 0, 10 -> 6912),
            Map(), Map()),

CPUTestCase("add2",
            Map("single-cycle" -> 1, "pipelined" -> 5),
            Map(5 -> 2048, 20 -> 512),
            Map(0 -> 0, 10 -> 2560),
            Map(), Map(), "-addnew")
```

**Step 2:** Add the name of the new test case to the list of tests in the suite in `src/test/scala/labs/Lab1Test.scala`.
For example, to run the new test with the ADD tests:

```
class SingleCycleAddTesterLab1 extends CPUFlatSpec {
  behavior of "Single Cycle CPU"
  for (name <- List("add0", "add1", "add2", "add2-addnew")) {
```

Now `Lab1 / testOnly dinocpu.SingleCycleAddTesterLab1` also runs the new test:

```
[info] - should run add test add0
[info] - should run add test add1
[info] - should run add test add2
[info] - should run add test add2-addnew
```

Each test in a suite must have a unique name.
If the same name appears twice in a suite's list, the whole suite is aborted with an error like the following:

```
[info] dinocpu.SingleCycleRTypeTesterLab1 *** ABORTED ***
[info]   Duplicate test name: Single Cycle CPU should run R-type instruction sub (Lab1Test.scala:142)
```
