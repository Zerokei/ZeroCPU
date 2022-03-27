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

class TileForLab extends Module{
  val io = IO(new Bundle{
    val cpuio = new CPUIO
    val switch = Input(UInt(12.W))
    val debug0 = Output(UInt(DLEN.W))
    val debug1 = Output(UInt(DLEN.W))
    val debug2 = Output(UInt(DLEN.W))
    val debug3 = Output(UInt(DLEN.W))
  })
  val cpu = Module(new CPU(verilator=false))
  io.cpuio <> cpu.io
  val switch = WireInit(0.U(12.W))
  switch := io.switch
  val debug0 = WireInit(0.U(DLEN.W))
  io.debug0 := debug0
  val debug1 = WireInit(0.U(DLEN.W))
  io.debug1 := debug1
  val debug2 = WireInit(0.U(DLEN.W))
  io.debug2 := debug2
  val debug3 = WireInit(0.U(DLEN.W))
  io.debug3 := debug3
  BoringUtils.addSource(switch, "switch")  
  val gprs = WireInit(VecInit(Seq.fill(NREGS)(0.U(DLEN.W))))

  BoringUtils.addSink(debug0, "debug0")
  BoringUtils.addSink(debug1, "debug1")
  BoringUtils.addSink(debug2, "debug2") 
  BoringUtils.addSink(debug3, "debug3")  
}

class DiffTestIO extends Bundle{
  val gprs = Output(Vec(NREGS, UInt(DLEN.W)))
  val csrs = Output(new CSRState)
  val counter = Output(UInt(1.W))
  val commom = Output(Vec(4, UInt(DLEN.W)))
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
  BoringUtils.addSink(difftest.commom(3), "debug3")

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
      Seq(ChiselGeneratorAnnotation(() => new TileForLab())))
}
object GenTVDebug extends App{
  println("succeed!")
  (new chisel3.stage.ChiselStage).execute(
      Array("-td", "build/verilog/base", "-X", "verilog"), 
      Seq(ChiselGeneratorAnnotation(() => new TileForVerilator())))
}