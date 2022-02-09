package zeroCPU.base

import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

import zeroCPU.core._
import zeroCPU.wow._
import zeroCPU.isa._
import zeroCPU.utils._
import zeroCPU.const.ZeroConfig._

class DiffTestIO extends Bundle{
  val gprs = Output(Vec(NREGS, UInt(DLEN.W)))
  val csrs = Output(new CSRState)
  val counter   = Output(UInt(1.W))
}
class TileForVerilator extends Module{
  val io = IO(new Bundle{
    val difftest = new DiffTestIO
  })
  val difftest = WireInit(0.U.asTypeOf(new DiffTestIO))
  BoringUtils.addSink(difftest.gprs, "dt_gprs")
  BoringUtils.addSink(difftest.csrs, "dt_csrs")
  BoringUtils.addSink(difftest.counter, "dt_counter")
  io.difftest <> difftest
  val cpu = Module(new CPU)
  val rom = Module(new SimRom(2048, "testfile.hex"))
  val ram = Module(new SimRam(2048))

  cpu.io.inst := rom.io.data
  cpu.io.data_in := ram.io.data_o
  ram.io.addr := cpu.io.addr_out
  rom.io.addr := cpu.io.pc_out
  ram.io.data_i := cpu.io.data_out
  ram.io.we := cpu.io.mem_write
}

object GenTV extends App{
  println("succeed!")
  (new chisel3.stage.ChiselStage).execute(
      Array("-td", "build/verilog/base", "-X", "verilog"), 
      Seq(ChiselGeneratorAnnotation(() => new TileForVerilator())))
//  visualize(() => new mux_1)
  //(new chisel3.stage.ChiselStage).emitVerilog(new CPU())
  // (new chisel3.stage.ChiselStage).emitVerilog()
}