package zeroCPU.wow
import chisel3._
import chisel3.util._
import zeroCPU.const.ZeroConfig._

class Register(nregs: Int = 32) extends Module{
	val io = IO(new Bundle{
		val rs = Input(UInt(log2Ceil(nregs).W))
		val rd = Input(UInt(log2Ceil(nregs).W))
		val reg_write = Input(Bool())
		val data_in = Input(UInt(DLEN.W))
		val data_out = Output(UInt(DLEN.W))
	})
	// how to use List

	val v = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

	when(io.reg_write){
		v(io.rd) := io.data_in
	}
	io.data_out := v(io.rs)
}