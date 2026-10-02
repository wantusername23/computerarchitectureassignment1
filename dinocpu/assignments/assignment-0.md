# DINO CPU Assignment 0

## Table of Contents

* [Introduction](#introduction)
* [Step 1: Set up the development environment](#step-1-set-up-the-development-environment)
  * [Starting and exiting sbt](#starting-and-exiting-sbt)
* [Step 2: Chisel tutorial](#step-2-chisel-tutorial)

## Introduction

In this course you will implement the DINO CPU.
This is a simple in-order CPU design based closely on the CPU model in Patterson and Hennessy's Computer Organization and Design.
We will use [Chisel](https://www.chisel-lang.org/) to describe the hardware that implements the CPU.

Through this course, we will be building this CPU from the ground up.
You will be provided with some template code which contains a set of interfaces between CPU components and some pre-written components.
You will combine these components together into a working processor!

**Read the assignment documents carefully before you start.**
The control signals and module interfaces of the DINO CPU are slightly different from the ones you saw in class.
The main ideas are the same, but you must follow the definitions in the assignment documents (and in the code comments), because the tests check against them.

Follow the steps below in order: first build the development environment (Step 1), and then use it to go through the Chisel tutorial (Step 2).

## Step 1: Set up the development environment

Make sure Docker is installed on your machine before you start.

The DINO CPU needs specific versions of Chisel, FIRRTL, sbt, Scala, and Java.
The repository includes a Dockerfile (`../dockerfiles/Dockerfile`) that builds
an environment with the required tools installed.

**VS Code (recommended):** The repository contains a `.devcontainer` configuration.
Install the *Dev Containers* extension, open the `dinocpu` folder, and choose
*Reopen in Container*. The dev container is built from the repository's local
Dockerfile, and its default terminal is a bash shell in the `dinocpu` folder.

**Docker only:** From the `dinocpu` directory, run the following commands.
The first command builds the image from the local Dockerfile. The second mounts
the repository into the container and opens a bash shell in the `dinocpu` folder.

```
docker build -f dockerfiles/Dockerfile -t dinocpu .
docker run -it --rm -v "$(pwd)":/workspaces/dinocpu -v dinocpu-cache:/root -w /workspaces/dinocpu dinocpu bash
```

If `ubuntu:24.04` is not already available locally, Docker will download that
base image while building.
The first time you start sbt, it downloads the project dependencies (Chisel, FIRRTL, and so on), which can take several minutes.
The `-v dinocpu-cache:/root` option stores them in a Docker volume named `dinocpu-cache`, so later runs reuse them and start quickly.
Always include this option; without it, the dependencies are downloaded again every time.
The project is compiled when you run commands such as `compile`, `test`, or
`runMain`.
When you are done, type `exit` in the shell to leave the container.

### Starting and exiting sbt

The commands in the assignments (e.g., `Lab1 / test`) are sbt commands.
To run them, start sbt from the shell in the `dinocpu` folder:

```
root@...:/workspaces/dinocpu# sbt
...
sbt:dinocpu>
```

When you see the sbt prompt (`sbt:dinocpu>`), you can type sbt commands.
In the assignment documents, a command shown after `sbt:dinocpu>` means that you type it at this prompt.
To leave sbt and go back to the shell, type `exit` (or press Ctrl+D):

```
sbt:dinocpu> exit
root@...:/workspaces/dinocpu#
```

You can also run a single sbt command directly from the shell, for example `sbt "Lab1 / test"`.
This is slower when you run many commands, because sbt starts again every time.

## Step 2: Chisel tutorial

[Chisel](https://www.chisel-lang.org/) is an open-source hardware construction language, developed at UC Berkeley.
You write Scala code, which is syntactically similar to Java.
Chisel can then generate low-level Verilog code, which is a hardware description language used by a variety of tools to describe how an electronic circuit works.

Once your development environment is running, go through the tutorial in [your first hardware](../documentation/chisel-notes/first-hardware.md).
It walks you through creating a small system of components in `src/main/scala/simple.scala` and simulating it from the sbt prompt with `runMain dinocpu.simple`.

Some more short Chisel notes are in the [Chisel notes directory](../documentation/chisel-notes/).
Keep the [cheat sheet](../documentation/chisel-notes/cheat-sheet.md) nearby while you work on the assignments, and you can find additional help and documentation on [Chisel's website](https://www.chisel-lang.org/).
