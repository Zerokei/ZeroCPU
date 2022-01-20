package ZeroCPU.base
import chisel3._
import chisel3.util._
import chisel3.experimental._
import ZeroCPU.wow._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

object GenTV extends App{
  
  println("succeed!")
  // (new chisel3.stage.ChiselStage).execute(
  //   Array("-td", "build/verilog/base", "-X", "verilog"), 
  //   Seq(ChiselGeneratorAnnotation(() => new Total)))
//  visualize(() => new mux_1)
  (new chisel3.stage.ChiselStage).emitVerilog(new ALU())
  (new chisel3.stage.ChiselStage).emitVerilog(new Mux_3())
}