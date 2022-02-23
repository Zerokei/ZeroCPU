#!/bin/bash

make clean
make prepare ELF=zerocpu/sim.elf
make
cd build
./emulator