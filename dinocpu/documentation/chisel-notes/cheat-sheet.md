# Chisel cheat sheet

This document contains some of the Chisel patterns you will use while implementing the DINO CPU.
The Chisel project provides [a more complete cheat sheet](https://github.com/freechipsproject/chisel-cheatsheet/releases/latest/download/chisel_cheatsheet.pdf).

# Wires

## Create a new wire

```
val x = Wire(UInt())
```

![wire](./wire.svg)

Create a wire (named `x`) that is of type `UInt`.
The width of the wire will be inferred.
**Important:** this is one of the few times you will use `=`, and not `:=`.

## Connect two wires

```
y := x
```

![connecting wires](./connecting.svg)

Connect wire `x` to wire `y`.
This is "backwards" in that the input is on the right and the output is on the left.
However, it's forwards in the way you say it out loud.
(For the example above, think "`y` is connected to `x`.")

## Another wire example

```
val adder1 = Module(new Adder())
val adder2 = Module(new Adder())

adder2.io.inputx := adder1.io.result
```

![adder connection](./simplesystem-1.svg)

`adder2.io.inputx := adder1.io.result` connects the output of adder 1 to the input of adder 2.

# Muxes

## Mux

```
val x = Wire(UInt())

x := Mux(selector, true_value, false_value)
```

This creates a wire `x` which will have the `true_value` on it if `selector` is true and the `false_value` otherwise.

## When-elsewhen-otherwise

```
val x = Wire(UInt(3.W))

when (value === 0.S) {
  x := "b001".U
} .elsewhen (value > 0.S) {
  x := "b010".U
} .otherwise { // value must be < 0
  x := "b100".U
}
```

The above creates a one-hot value on the wire `x` depending on whether the wire `value` (an `SInt`) is 0, greater than 0, or less than 0.
Note that both sides of a comparison must have the same type: compare an `SInt` with `0.S`, and a `UInt` with `0.U`.

## switch-is

The `switch-is` statement is useful for implementing logic tables.
For instance, below.

| input | output |
|-------|--------|
| 0001  | true   |
| 0100  | false  |
| 0101  | true   |
| 1101  | true   |

```
output := DontCare // since we aren't fully specifying the output, this is required.

switch (input) {
  is ("b0001".U) { output := true.B }
  is ("b0100".U) { output := false.B }
  is ("b0101".U) { output := true.B }
  is ("b1101".U) { output := true.B }
}
```

`switch` and `is` are in `chisel3.util`, so you need `import chisel3.util._` to use them.
You can put other `switch`, `when`, or `Mux` statements inside an `is` block.

# Types

## Boolean

- `Bool()`: a 1-bit value.
- `true.B`: to convert from a Scala boolean to Chisel, use `.B`.
- `false.B`: to convert from a Scala boolean to Chisel, use `.B`.

## Integers

- `UInt(32.W)`: an unsigned integer that is 32 bits wide.
- `UInt()`: an unsigned integer with the width inferred. (You may get an error saying it can't infer the width.)
- `77.U`: to convert from a Scala integer to a Chisel unsigned int, use `.U`. (You may get type incompatible errors if you don't do this correctly.)
- `-1.S(2.W)`: a signed integer (`SInt`) that is 2 bits wide. The value must fit in the width (e.g., `3.S(2.W)` is an error, because 3 needs 3 bits as a signed integer).
- `"b001010".U`: to create a binary literal, use a string of 1's and 0's starting with "b" and convert it to an unsigned int with `.U`. (There is no `.S` for strings; use `"b001010".U.asSInt` if you need a signed value.)

# Getting parts of a wire

If you want a subset of a wire, you can use `()`.
Some examples below.

```
val x = Wire(UInt(32.W))
val lower_5 = x(4,0)
val top_3 = x(31,29)
val rd_in_riscv_instruction = x(11,7)
```

Note: these numbers are *exactly* how you will write them on all of your diagrams.
The indices are inclusive with the high-order bits on the left.

# Circuits provided as operators

## Math

You can use simple operators like `+`, `-`, `>>`, etc. and they will generate circuits to match those operations.
See [the more complete cheat sheet](https://github.com/freechipsproject/chisel-cheatsheet/releases/latest/download/chisel_cheatsheet.pdf) for more details.

## Comparisons

The way to compare two chisel values is a little different than Scala, since it's creating a circuit and not doing a comparison.

- Equality: `===`
- Inequality: `=/=`

Less than, greater than, etc. work as expected.
However, make sure you are using the correct type (signed or unsigned).

# State elements (registers)

- `Reg(UInt(64.W))`: A 64-bit register
- `RegInit(1.U(32.W))`: A 32-bit register that has the value 1 when the system starts.

Registers can be connected to other wires.

```
val register = Reg(UInt(32.W))

x := register
```

![Register connect to wire](register-x.svg)

This takes the value coming out of the register and connects it to the wire `x`.

Similarly, you can set a register to a value (at the end of a clock cycle).

```
val register = Reg(UInt(32.W))

register := y
```

![wire connect to register](y-register.svg)

This will set the register to the value on wire `y` at the end of the clock cycle.

# Modules

## IO

The I/O of a module is a bundle of wires, declared with `IO(new Bundle { ... })`.
Each wire is either an `Input` or an `Output`.

```
class MyModule extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

  io.out := io.in + 1.U
}
```

## Using modules

See [creating your first Chisel hardware](./first-hardware.md).

# Bundles

Bundles are a way to group a set of wires together.
For instance, the I/O for each module is a bundle of wires which you can refer to as `module.io`.

You can set a single wire of a bundle by using the `.` operator, kind of like accessing the element of an object or a structure.
In fact, that's a good way to think of bundles.
They are kind of like `struct`s in C/C++.

Let's use a complex number with a real and imaginary component as an example.

```
class Complex extends Bundle {
  val real = SInt(32.W)
  val imag = SInt(32.W)
}
```

You can use this new "type" to create a wire:

```
val wire = Wire(new Complex())
```

And you can set each component of the wire separately:

```
wire.real := 3.S
wire.imag := -5.S
```

You can also connect the entire bundle of wires to other objects.
For instance, the code below creates a register that is connected to the wire above.

```
val myreg = Reg(new Complex())
wire := myreg
```

To set the entire bundle to 0 (all of the wires to 0), you need to use an explicit cast.

```
wire := 0.U.asTypeOf(new Complex)
```

See [Bundles and Vecs](https://www.chisel-lang.org/docs/explanations/bundles-and-vecs) in the Chisel documentation for more information.

# Frequently asked questions

You may also find your answer in the [Chisel cookbook](https://www.chisel-lang.org/docs/cookbooks/cookbook).

## When to use `=` vs `:=`

You should use `=` when *creating a new variable* (e.g., a new wire, register, or module).
The `=` should always be on the same line as a `val`.

`:=` is the operator to *connect* the output wire on the right to the input wire on the left.
Note: This is backwards from the way you would draw it, but it's forwards for the way you would say it.
Think of the `:=` as "is connected to" in English.
