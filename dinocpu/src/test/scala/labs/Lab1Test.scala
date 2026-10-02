// Tests for Lab 1. Feel free to modify and add more tests here.
// If you name your test class something that ends with "TesterLab1" it will
// automatically be run when you use `Lab1 / test` at the sbt prompt.

package dinocpu

import dinocpu._
import dinocpu.components._
import dinocpu.test._
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class ALUControlUnitRITypeTester(c: ALUControl) extends PeekPokeTester(c) {
  private val ctl = c

  // Copied from Patterson and Waterman Figure 2.3
  val tests = List(
    // alu,   itype,       Funct7,    Func3, Wordinst,   Control Input
    (  1.U, false.B, "b0000000".U, "b000".U,      0.U, "b00111".U, "add"),
    (  1.U, false.B, "b0100000".U, "b000".U,      0.U, "b00100".U, "sub"),
    (  1.U,  true.B, "b0000000".U, "b000".U,      0.U, "b00111".U, "addi"),
    (  1.U,  true.B, "b0000001".U, "b101".U,      0.U, "b00010".U, "srli"),
  )

  for (t <- tests) {
    poke(ctl.io.aluop, t._1)
    poke(ctl.io.itype, t._2)
    poke(ctl.io.funct7, t._3)
    poke(ctl.io.funct3, t._4)
    poke(ctl.io.wordinst, t._5)
    step(1)
    expect(ctl.io.operation, t._6, s"${t._7} wrong")
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.ALUControlTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.ALUControlTesterLab1'
  * }}}
  */
class ALUControlTesterLab1 extends ChiselFlatSpec {
  "ALUControl" should s"match expectations for each intruction type" in {
    Driver(() => new ALUControl) {
      c => new ALUControlUnitRITypeTester(c)
    } should be (true)
  }
}

class ControlUnitRITypeTester(c: Control) extends PeekPokeTester(c) {
  private val ctl = c

  val tests = List(
    // Inputs,      itype, aluop, src1, src2, branch, jumptype, resultselect, memop, toreg, regwrite, validinst, wordinst
    ( "b0110011".U,   0.U,   1.U,  0.U,  0.U,    0.U,      0.U,          0.U,   0.U,   0.U,      1.U,       1.U,      0.U, "R-type"),
  )

  for (t <- tests) {
    poke(ctl.io.opcode, t._1)
    step(1)
    expect(ctl.io.itype, t._2, s"${t._14} itype wrong")
    expect(ctl.io.aluop, t._3, s"${t._14} aluop wrong")
    expect(ctl.io.src1, t._4, s"${t._14} src1 wrong")
    expect(ctl.io.src2, t._5, s"${t._14} src2 wrong")
    expect(ctl.io.branch, t._6, s"${t._14} branch wrong")
    expect(ctl.io.jumptype, t._7, s"${t._14} jumptype wrong")
    expect(ctl.io.resultselect, t._8, s"${t._14} resultselect wrong")
    expect(ctl.io.memop, t._9, s"${t._14} memop wrong")
    expect(ctl.io.toreg, t._10, s"${t._14} toreg wrong")
    expect(ctl.io.regwrite, t._11, s"${t._14} regwrite wrong")
    expect(ctl.io.validinst, t._12, s"${t._14} validinst wrong")
    expect(ctl.io.wordinst, t._13, s"${t._14} wordinst wrong")
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.ControlTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.ControlTesterLab1'
  * }}}
  */
class ControlTesterLab1 extends ChiselFlatSpec {
  "Control" should s"match expectations for each opcode" in {
    Driver(() => new Control) {
      c => new ControlUnitRITypeTester(c)
    } should be (true)
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.SingleCycleAddTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.SingleCycleAddTesterLab1'
  * }}}
  */
class SingleCycleAddTesterLab1 extends CPUFlatSpec {
  behavior of "Single Cycle CPU"
  for (name <- List("add0", "add1", "add2")) {
    val test = InstTests.nameMap(name)
    it should s"run add test ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "single-cycle") should be(true)
    }
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.SingleCycleRTypeTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.SingleCycleRTypeTesterLab1'
  * }}}
  *
  * To run a **single** test from this suite, you can use the -z option to sbt test.
  * The option after the `-z` is a string to search for in the test
  * {{{
  * sbt> Lab1 / testOnly dinocpu.SingleCycleRTypeTesterLab1 -- -z sub
  * }}}
  */
class SingleCycleRTypeTesterLab1 extends CPUFlatSpec {
  behavior of "Single Cycle CPU"
  for (name <- List("sub", "and", "or", "slt", "sll")) {
    val test = InstTests.nameMap(name)
    it should s"run R-type instruction ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "single-cycle") should be(true)
    }
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.SingleCycleITypeTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.SingleCycleITypeTesterLab1'
  * }}}
  *
  * To run a **single** test from this suite, you can use the -z option to sbt test.
  * {{{
  * sbt> Lab1 / testOnly dinocpu.SingleCycleITypeTesterLab1 -- -z andi
  * }}}
  */
class SingleCycleITypeTesterLab1 extends CPUFlatSpec {
  behavior of "Single Cycle CPU"
  for (test <- List("addi1", "andi", "slli", "slti", "addi3").map(InstTests.nameMap)) {
    it should s"run I-type instruction ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "single-cycle") should be(true)
    }
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.SingleCycleMultiCycleTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.SingleCycleMultiCycleTesterLab1'
  * }}}
  *
  * To run a **single** test from this suite, you can use the -z option to sbt test.
  * The option after the `-z` is a string to search for in the test
  * {{{
  * sbt> Lab1 / testOnly dinocpu.SingleCycleMultiCycleTesterLab1 -- -z swapxor
  * }}}
  */
class SingleCycleMultiCycleTesterLab1 extends CPUFlatSpec {
  behavior of "Single Cycle CPU"
  for (test <- List("addfwd", "swapxor", "addi2").map(InstTests.nameMap)) {
    it should s"run multi-cycle program ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "single-cycle") should be(true)
    }
  }
}
