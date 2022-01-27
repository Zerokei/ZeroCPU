package zeroCPU.core
import chisel3._
import chisel3.util._
import zeroCPU.wow._
import zeroCPU.const.ZeroConfig._

class CPU extends Module{
  val io = IO(new Bundle{
    val inst = Input(UInt(LEN.W)) // inst from rom
    val data_in = Input(UInt(LEN.W)) // ram -> CPU
    val addr_out = Output(UInt(LEN.W)) // CPU -> ram(addr)
    val data_out = Output(UInt(LEN.W)) // CPU -> ram(data)
    val mem_write = Output(UInt(LEN.W)) // CPU -> ram(mem_write)
    val pc_out = Output(UInt(LEN.W)) // CPU -> rom(pc)
	})
  
	val inst_IF = io.inst
  val inst_ID = RegNext(inst_IF)
  
  val decode = Module(new Decoder())
  decode.io.inst := inst_ID
  val pc_src_ID     = decode.io.pc_src
  val branch_sig_ID = decode.io.branch_sig
  val mem_to_reg_ID = decode.io.mem_to_reg
  val reg_write_ID  = decode.io.reg_write
  val op_src1_ID    = decode.io.op_src1
  val op_src2_ID    = decode.io.op_src2
  val alu_src_ID    = decode.io.alu_src
  val mem_write_ID  = decode.io.mem_to_reg
  val csr_write_ID  = decode.io.csr_write

  val rs1_ID        = decode.io.rs1
  val rs2_ID        = decode.io.rs2
  val rd_ID         = decode.io.rd

  val reg = Module(new Register())
  reg.io.rs1 := rs1_ID
  reg.io.rs2 := rs2_ID
  val ro1_ID        = reg.io.data_out1
  val ro2_ID        = reg.io.data_out2

  val ro1_EXE = RegNext(ro1_ID)
  val ro2_EXE = RegNext(ro2_ID)
  val rs1_EXE = RegNext(rs1_ID)
  val rs2_EXE = RegNext(rs2_ID)
  val rd_EXE  = RegNext(rd_ID)

  val rs1_MEM = RegNext(rs1_EXE)
  val rs2_MEM = RegNext(rs2_EXE)
  val rd_MEM  = RegNext(rd_EXE)

  val rs1_WB = RegNext(rs1_MEM)
  val rs2_WB = RegNext(rs2_MEM)
  val rd_WB  = RegNext(rd_MEM)

}