// This file contains ALU control logic.

package dinocpu.components

import chisel3._
import chisel3.util._

/**
 * The ALU control unit
 *
 * Input:  aluop        0 for ld/st, 1 for R-type
 * Input:  itype        True if I-type
 * Input:  funct7       The most significant bits of the instruction
 * Input:  funct3       The middle three bits of the instruction (12-14)
 * Input:  wordinst     True if the instruction *only* operates on 32-bit operands, False otherwise
 * Output: operation    What we want the ALU to do.
 *
 * For more information, see Section 4.4 and A.5 of Patterson and Hennessy.
 * This is loosely based on figure 4.12
 */
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
