package ZeroCPU.wow
import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
class Mux_3 extends Module {
  val io = IO(new Bundle{
    val in_a = Input(UInt(32.W))
    val in_b = Input(UInt(32.W))
    val in_c = Input(UInt(32.W))
    val sel = Input(UInt(2.W))
    val out_o = Output(UInt(32.W))
  })
  assert(io.sel =/= 3.U(2.W))
  when(io.sel === 2.U(2.W)){
    io.out_o := io.in_c
  }.elsewhen(io.sel === 1.U(2.W)){
    io.out_o := io.in_b
  }.otherwise{
    io.out_o := io.in_a
  }
}