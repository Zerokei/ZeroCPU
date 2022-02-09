package zeroCPU.utils
import chisel3._
import chisel3.util._
import chisel3.util.experimental._

import zeroCPU.wow._
import zeroCPU.const.ZeroConfig._

class SimRom(val depth: Int, val path: String) extends Module{
  val io = IO(new Bundle{
    val addr = Input(UInt(LEN.W))
    val data = Output(UInt(DLEN.W))
  })
  val mem = Mem(depth, UInt(DLEN.W))
  loadMemoryFromFile(mem, path)
  io.data := mem(io.addr)
}
class SimRam(val depth: Int) extends Module{
  val io = IO(new Bundle{
    val addr = Input(UInt(LEN.W))
    val we = Input(Bool())
    val data_i = Input(UInt(DLEN.W))
    val data_o = Output(UInt(DLEN.W))
  })
  val mem = Mem(depth, UInt(DLEN.W))
  io.data_o := mem(io.addr)
  when(io.we){
    mem(io.addr) := io.data_i
  }
}