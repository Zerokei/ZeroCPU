package zeroCPU.wow
import chisel3._
import chisel3.util._
import zeroCPU.const.ZeroConfig._

class CSR_MOD extends Module{
  val io = IO(new Bundle{
    val pc_in = Input(UInt(DLEN.W))
    val sig   = Input(UInt(CSW_SIG_LEN.W))
    val rd    = Input(UInt(DLEN.W))
    val csr   = Input(UInt(CSRS_SIZE.W))
    val reg   = Input(UInt(DLEN.W))
    val t     = Output(UInt(SLEN.W))
    val pc_out= Output(UInt(SLEN.W))
  })

  /*
    csrrs: t = CSRs[csr]; CSRs[csr] = t | x[rs1]; x[rd] = t
  */
  val mcause  = RegInit(0.U(SLEN.W))
  val mtvec   = RegInit(0.U(SLEN.W))
  val mepc    = RegInit(0.U(SLEN.W))
  val mstatus = RegInit(0.U(SLEN.W))
  
  when(io.sig === CSW_REG){
    when(io.csr === CSR_MTVEC){
      io.t := mtvec
    }.elsewhen(io.csr === CSR_MSTATUS){
      io.t := mstatus
    }.otherwise{
      io.t := 0.U(SLEN.W)
    }
  }.otherwise{
    io.t := 0.U(SLEN.W)
  }

  io.pc_out := mepc

  when(io.sig === CSW_REG){
    when(io.csr === CSR_X){
      // do nothing
    }.elsewhen(io.csr === CSR_MTVEC){
      mtvec := mtvec | io.reg
    }.elsewhen(io.csr === CSR_MSTATUS){
      mstatus := mstatus | io.reg
    }.elsewhen(io.csr === CSR_F){
      mstatus := "h00002".U // Illegal Instruction
    }.otherwise{
      // exception
    }
  }.elsewhen(io.sig === CSW_BRK){
    mepc := io.pc_in + 4.U
    mcause := "h80003".U // machine software 
  }.elsewhen(io.sig === CSW_CAL){
    mepc := io.pc_in + 4.U
    mcause := "h80003".U // machine software interrupt
  }.otherwise{
    // do nothing
  }
}