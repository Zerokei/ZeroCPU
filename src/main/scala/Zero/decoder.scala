package zeroCPU.wow
import chisel3._
import chisel3.util._
import zeroCPU.isa.ISA._
import zeroCPU.const.ZeroConfig._

class Decoder extends Module{
  val io = IO(new Bundle{
    val inst = Input(UInt(32.W))
    val rs1 = Output(UInt(5.W))
    val rs2 = Output(UInt(5.W))
    val rd = Output(UInt(5.W))
    val alu_op = Output(UInt(5.W))
    val imm = Output(UInt(32.W))

    //signal
    val mem_write = Output(Bool())
    val reg_write = Output(Bool())
    val branch_sig = Output(Bool())
    val pc_src = Output(UInt(2.W))
    val alu_src = Output(UInt(2.W))
    val mem_to_reg = Output(UInt(2.W))
  })
  // val signals =
  //   ListLookup(io.inst,
  //                       List()
    
    
  //   )
}