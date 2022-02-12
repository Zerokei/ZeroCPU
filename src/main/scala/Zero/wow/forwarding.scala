package zeroCPU.wow
import chisel3._
import chisel3.util._
import zeroCPU.const.ZeroConfig._

class Forwarding(verilator: Boolean = false) extends Module{
  val io = IO(new Bundle{
    val rd_MEM  = Input(UInt(NREGS_BIT.W))
    val rd_WB   = Input(UInt(NREGS_BIT.W))
    val rs1_EXE = Input(UInt(NREGS_BIT.W))
    val rs2_EXE = Input(UInt(NREGS_BIT.W))
    
    //signals
    val rw_MEM  = Input(Bool())
    val rw_WB   = Input(Bool())
    val csw_MEM = Input(UInt(CSW_SIG_LEN.W))

    val fsig1   = Output(UInt(FWD_SIG_LEN.W))
    val fsig2   = Output(UInt(FWD_SIG_LEN.W))
  })

  when((io.rs1_EXE === io.rd_MEM) && (io.csw_MEM =/= CSW_X) && (io.rd_MEM =/= 0.U)){
    io.fsig1 := FWD_CSR
  }.elsewhen((io.rs1_EXE === io.rd_MEM) && (io.rd_MEM =/= 0.U)){
    io.fsig1 := FWD_MEM
  }.elsewhen((io.rs1_EXE === io.rd_WB) && (io.rd_WB =/= 0.U)){
    io.fsig1 := FWD_WB
  }.otherwise{
    io.fsig1 := FWD_X  
  }

  when((io.rs2_EXE === io.rd_MEM) && (io.csw_MEM =/= CSW_X) && (io.rd_MEM =/= 0.U)){
    io.fsig2 := FWD_CSR
  }.elsewhen((io.rs2_EXE === io.rd_MEM) && (io.rd_MEM =/= 0.U)){
    io.fsig2 := FWD_MEM
  }.elsewhen((io.rs2_EXE === io.rd_WB) && (io.rd_WB =/= 0.U)){
    io.fsig2 := FWD_WB
  }.otherwise{
    io.fsig2 := FWD_X  
  }
}