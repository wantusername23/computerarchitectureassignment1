// Lists of different instruction test cases for use with different CPU models

package dinocpu.test

/**
 * This object contains a set of lists of tests. Each list is a different set of
 * instruction types and corresponds to a RISC-V program in resources/risc-v
 *
 * Each test case looks like:
 *  - binary to run in src/test/resources/risc-v
 *  - number of cycles to run for each CPU type
 *  - initial values for registers
 *  - final values to check for registers
 *  - initial values for memory
 *  - final values to check for memory
 *  - extra name information
 */
object InstTests {

  val maxInt = BigInt("FFFFFFFFFFFFFFFF", 16)

  def twoscomp(v: BigInt) : BigInt = {
    if (v < 0) {
      return maxInt + v + 1
    } else {
      return v
    }
  }

    val rtype = List[CPUTestCase](
        CPUTestCase("add1",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> 1234),
                                Map(0 -> 0, 5 -> 1234, 6 -> 1234),
                                Map(), Map()),
        CPUTestCase("add2",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> 1234, 20 -> 5678),
                                Map(0 -> 0, 10 -> 6912),
                                Map(), Map()),
        CPUTestCase("add0",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> 1234, 6 -> 3456),
                                Map(0 -> 0, 5 -> 1234, 6 -> 3456),
                                Map(), Map()),
        CPUTestCase("or",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> 1234, 6 -> 5678),
                                Map(7 -> 5886),
                                Map(), Map()),
        CPUTestCase("sub",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> 1234, 6 -> 5678),
                                Map(7 -> BigInt("FFFFFFFFFFFFEEA4", 16)),
                                Map(), Map()),
        CPUTestCase("and",
                Map("single-cycle" -> 1,  "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> 1234, 6 -> 5678),
                                Map(7 -> 1026),
                                Map(), Map()),
        CPUTestCase("slt",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(7 -> 1234, 5 -> 5678),
                                Map(5 -> 5678, 7 -> 1234, 6 -> 1),
                                Map(), Map()),
        CPUTestCase("sll",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(7 -> 32, 5 -> 2),
                                Map(7 -> 32, 5 -> 2, 6 -> 128),
                                Map(), Map())
  )

    val rtypeMultiCycle = List[CPUTestCase](
        CPUTestCase("addfwd",
                Map("single-cycle" -> 10, "pipelined" -> 14, "pipelined-non-combin" -> 140, "pipelined-dual-issue" -> 14),
                Map(5 -> 1, 10 -> 0),
                                Map(5 -> 1, 10 -> 10),
                                Map(), Map()),
        CPUTestCase("swapxor",
                Map("single-cycle" -> 3, "pipelined" -> 7, "pipelined-non-combin" -> 70, "pipelined-dual-issue" -> 7),
                Map(7 -> 5678, 5 -> 1234),
                                Map(5 -> 5678,7->1234),
                                Map(), Map())
    )

    val itype = List[CPUTestCase](
        CPUTestCase("addi1",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(),
                                Map(0 -> 0, 10 -> 17),
                                Map(), Map()),
        CPUTestCase("slli",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> 1),
                                Map(0 -> 0, 5 -> 1, 6 -> 128),
                                Map(), Map()),
        CPUTestCase("andi",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> 456),
                                Map(0 -> 0, 5 -> 456, 6 -> 200),
                                Map(), Map()),
        CPUTestCase("slti",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(5 -> twoscomp(-1)),
                                Map(0 -> 0, 5 -> twoscomp(-1),6->1),
                                Map(), Map()),
        CPUTestCase("addi3",
                Map("single-cycle" -> 1, "pipelined" -> 5, "pipelined-non-combin" -> 50, "pipelined-dual-issue" -> 5),
                Map(28 -> 1099),
                                Map(0 -> 0, 5 -> 2123),
                                Map(), Map())
  )

    val itypeMultiCycle = List[CPUTestCase](
        CPUTestCase("addi2",
                Map("single-cycle" -> 2, "pipelined" -> 6, "pipelined-non-combin" -> 60, "pipelined-dual-issue" -> 6),
                Map(),
                                Map(0 -> 0, 10 -> 17, 11 -> 93),
                                Map(), Map())
    )

  // Mapping from group name to list of tests
  val tests = Map(
    "rtype" -> rtype,
    "rtypeMultiCycle" -> rtypeMultiCycle,
    "itype" -> itype,
    "itypeMultiCycle" -> itypeMultiCycle,
  )

  // All of the tests
  val allTests = rtype ++ rtypeMultiCycle ++ itype ++ itypeMultiCycle

  // Mapping from full name of test to test
  val nameMap = allTests.map(x => x.name() -> x).toMap
}
