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
  val counter = Output(UInt(1.W))
  val commom = Output(Vec(3, UInt(DLEN.W)))
  val finish = Output(Bool())
}
class TileForVerilator extends Module{
  val io = IO(new Bundle{
    val difftest = new DiffTestIO
  })
  val difftest = WireInit(0.U.asTypeOf(new DiffTestIO))
  BoringUtils.addSink(difftest.gprs, "dt_gprs")
  BoringUtils.addSink(difftest.csrs, "dt_csrs")
  BoringUtils.addSink(difftest.counter, "dt_counter")
  BoringUtils.addSink(difftest.commom(0), "debug0")
  BoringUtils.addSink(difftest.commom(1), "debug1")
  BoringUtils.addSink(difftest.commom(2), "debug2")

  difftest.finish := false.B
  io.difftest <> difftest

  // get clocks
  val nclock = RegInit(0.U(DLEN.W))
  nclock := nclock + 1.U
  // BoringUtils.addSource(nclock, "debug0")

  val cpu = Module(new CPU(verilator=true))
  val rom = Module(new SimRom(verilator=true))
  val ram = Module(new SimRam(verilator=true))

  rom.io <> cpu.io.rom
  ram.io <> cpu.io.ram
}

object GenTV extends App{
  println("succeed!")
  (new chisel3.stage.ChiselStage).execute(
      Array("-td", "build/verilog/base", "-X", "verilog"), 
      Seq(ChiselGeneratorAnnotation(() => new CPU())))
}
object GenTVDebug extends App{
  println("succeed!")
  (new chisel3.stage.ChiselStage).execute(
      Array("-td", "build/verilog/base", "-X", "verilog"), 
      Seq(ChiselGeneratorAnnotation(() => new TileForVerilator())))
}