package zeroCPU.wow
import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import zeroCPU.const.ZeroConfig._

class CSRState() extends Bundle{
  val mcause  = UInt(SLEN.W)
  val mtvec   = UInt(SLEN.W)
  val mepc    = UInt(SLEN.W)
  val mstatus = UInt(SLEN.W)
}

class CSR_MOD(verilator: Boolean = false) extends Module{
  val io = IO(new Bundle{
    val sig     = Input(UInt(CSW_SIG_LEN.W))
    val csr     = Input(UInt(CSRS_SIZE.W))
    val data_i  = Input(UInt(DLEN.W))
    val pc_in   = Input(UInt(DLEN.W))
    val data_o  = Output(UInt(SLEN.W))
    val pc_out  = Output(UInt(SLEN.W))
    val call_for_int = Output(Bool())
  })

  //debug
  val mcause  = RegInit(0.U(SLEN.W))
  val mtvec   = RegInit(0.U(SLEN.W))
  val mepc    = RegInit(0.U(SLEN.W))
  val mstatus = RegInit(0.U(SLEN.W))
  val csrs = Wire(new CSRState)
  csrs.mcause   := mcause
  csrs.mtvec    := mtvec
  csrs.mepc     := mepc
  csrs.mstatus  := mstatus

  if(verilator){
    val csrs_MEM = RegNext(csrs)
    val csrs_WB  = RegNext(csrs_MEM)
    BoringUtils.addSource(csrs_WB, "dt_csrs")
  }
  
  io.data_o := MuxLookup( io.csr, 0.U(SLEN.W),
        Array(
              CSR_MEPC      -> mepc   ,
              CSR_MTVEC     -> mtvec  ,
              CSR_MCAUSE    -> mcause ,
              CSR_MSTATUS   -> mstatus
        )
  )
  when(io.sig === CSW_WRT){
    when(io.csr === CSR_MTVEC){
      mtvec := io.data_i
      io.pc_out := mepc
      io.call_for_int := false.B
    }.elsewhen(io.csr === CSR_MEPC){
      mepc := io.data_i
      io.pc_out := mepc
      io.call_for_int := false.B
    }.elsewhen(io.csr === CSR_MSTATUS){
      mstatus := io.data_i
      io.pc_out := mepc
      io.call_for_int := false.B
    }.otherwise{
      mcause := "h00002".U
      mepc := io.pc_in
      io.pc_out := mtvec
      io.call_for_int := true.B
    }
  }
  .elsewhen(io.sig === CSW_BRK){
    mepc := io.pc_in
    mcause := "h00003".U // Breakpoint 
    io.pc_out := mtvec
    io.call_for_int := false.B
  }
  .elsewhen(io.sig === CSW_CAL){
    mepc := io.pc_in
    mcause := "h0000B".U // Environmrnt call from M-mode
    io.pc_out := mtvec
    io.call_for_int := false.B
}
  .otherwise{
    io.pc_out := mepc
    io.call_for_int := false.B
    // do nothing
  }
}