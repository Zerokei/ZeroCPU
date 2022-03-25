package zeroCPU.utils
import chisel3._
import chisel3.util._
import chisel3.util.experimental._

import zeroCPU.wow._
import zeroCPU.const.ZeroConfig._

class RamIO extends Bundle{
  val addr = Input(UInt(DLEN.W))
  val wen = Input(Bool())
  val data_i  = Input(UInt(DLEN.W))
  val data_o  = Output(UInt(DLEN.W))
  val valid   = Output(Bool())
}
class RomIO extends Bundle{
  val addr = Input(UInt(DLEN.W))
  val data = Output(UInt(DLEN.W))
  val valid = Output(Bool())
}
class SimRom(verilator: Boolean = false) extends Module{
  val io = IO(new RomIO())
  val mem = Mem(0x4000000L, UInt(8.W))
  loadMemoryFromFileInline(mem, "testfile.hex")
  val addr = io.addr & ("h7FFFFFFF".U)
  io.data := Cat(mem.read(addr+3.U),mem.read(addr+2.U),mem.read(addr+1.U),mem.read(io.addr))
  // realise asynchronous
  val count = RegInit(0.U(3.W))
  count := count + 1.U
  io.valid := Mux(count === 7.U, true.B, false.B)
}
class SimRam(verilator: Boolean = false) extends Module{
  val io = IO(new RamIO())  
  val mask = "h80800000".U

  val mem = Mem(0x1000000L, UInt(DLEN.W))
  io.data_o := mem.read(io.addr ^ mask)
  when(io.wen){
    mem(io.addr ^ mask) := io.data_i
  }

  // realise asynchronous
  val count = RegInit(0.U(3.W))
  count := count + 1.U
  io.valid := Mux(count === 7.U, true.B, false.B)
  // val de = Wire(UInt(DLEN.W))
  // de := mem.read("h80001004".U)
  // BoringUtils.addSource(de, "debug2")
}