package ZeroCPU.base

import chisel3._
import chisel3.experimental._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class mux_1 extends Module {
  val io = IO(new Bundle{
	val in_a = Input(UInt(1.W))
	val in_b = Input(UInt(1.W))
	val sel = Input(UInt(1.W))
	val out_o = Output(UInt(1.W))
  })
  io.out_o := (io.in_a & io.sel) | (io.in_b & io.sel)
}



object GenTV extends App{
    println("test_mux")
    (new chisel3.stage.ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new mux_1)))
}