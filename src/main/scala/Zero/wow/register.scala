package zeroCPU.wow
import chisel3._
import chisel3.util._
import zeroCPU.const.ZeroConfig._

class Register() extends Module{
	val io = IO(new Bundle{
		val rs1       = Input(UInt(NREGS_BIT.W))
		val rs2       = Input(UInt(NREGS_BIT.W))
		val rd 	      = Input(UInt(NREGS_BIT.W))
		val reg_write = Input(Bool())
		val data_in   = Input(UInt(DLEN.W))
		val data_out1 = Output(UInt(DLEN.W))
		val data_out2 = Output(UInt(DLEN.W))
	})
  // Init registers
	val v = RegInit(VecInit(Seq.fill(NREGS)(0.U(DLEN.W))))
  // write
	when(io.rd =/= 0.U(NREGS_BIT.W) && io.reg_write){
		v(io.rd) := io.data_in
	}.otherwise{
    v(io.rd) := 0.U(DLEN.W)
  }
  // out
	io.data_out1 := v(io.rs1)
	io.data_out2 := v(io.rs2)
}