package zeroCPU.base

import chisel3._
import chisel3.util._
import chisel3.experimental._
import zeroCPU.wow._
import zeroCPU.core._
import zeroCPU.isa._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

object GenTV extends App{
  println("succeed!")
  // (new chisel3.stage.ChiselStage).execute(
  //   Array("-td", "build/verilog/base", "-X", "verilog"), 
  //   Seq(ChiselGeneratorAnnotation(() => new Total)))
//  visualize(() => new mux_1)
  (new chisel3.stage.ChiselStage).emitVerilog(new CPU())
  // (new chisel3.stage.ChiselStage).emitVerilog()
}