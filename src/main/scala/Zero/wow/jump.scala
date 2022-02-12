package zeroCPU.wow
import chisel3._
import chisel3.util._
import zeroCPU.const.ZeroConfig._
class Jump_Union(verilator: Boolean = false) extends Module{
  val io = IO(new Bundle{
    val b_type  = Input(UInt(BR_SIG_LEN.W))
    val in1     = Input(UInt(DLEN.W))
    val in2     = Input(UInt(DLEN.W))
    val call_for_int = Input(Bool())
    val pc_src  = Output(UInt(PC_SIG_LEN.W))
  })
  val in1 = io.in1
  val in2 = io.in2
  val pc_src = Wire(UInt(PC_SIG_LEN.W)) 
  when(io.call_for_int){
    pc_src := PC_CSR
  }
  .otherwise{
    pc_src := MuxLookup(io.b_type, PC_X,
      Array(
        BR_N    ->PC_4,
        BR_NE   ->Mux(in1 =/= in2, PC_B, PC_4),
        BR_EQ   ->Mux(in1 === in2, PC_B, PC_4),
        BR_GE   ->Mux(in1.asSInt() >= in2.asSInt(), PC_B, PC_4),
        BR_GEU  ->Mux(in1.asUInt() >= in2.asUInt(), PC_B, PC_4),
        BR_LT   ->Mux(in1.asSInt() <= in2.asSInt(), PC_B, PC_4),
        BR_LTU  ->Mux(in1.asUInt() <= in2.asUInt(), PC_B, PC_4),
        BR_J    ->PC_J,
        BR_JR   ->PC_JR,
        BR_BK   ->PC_CSR,
        BR_CA   ->PC_CSR,
        BR_RT   ->PC_CSR
      ))
  }
  io.pc_src := pc_src
}