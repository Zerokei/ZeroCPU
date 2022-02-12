package zeroCPU.wow
import chisel3._
import chisel3.util.experimental.BoringUtils
import zeroCPU.const.ZeroConfig._

class Register(verilator: Boolean = false) extends Module{
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

  val _T = (~clock.asUInt()).asBool().asClock()
  val v =  withClock(_T)(RegInit(VecInit(Seq.fill(NREGS)(0.U(DLEN.W)))))
	
	//debug
  if(verilator){
  	BoringUtils.addSource(v, "dt_gprs")
  }

  // write
	when(io.rd =/= 0.U(NREGS_BIT.W) && io.reg_write){
		v(io.rd) := io.data_in
	}.otherwise{
    v(io.rd) := 0.U(DLEN.W)
  }

  // out
	io.data_out1 := v(io.rs1)
	io.data_out2 := v(io.rs2)

	// BoringUtils.addSource(v(3), "debug2")
}