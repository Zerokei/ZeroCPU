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
    
    
	//val pc_rst = 0.U(32.W)
	//val pc_pst = RegNext(pc_rst, 0.U)
	//io.pc_out := pc_pst
	
	// Dec = Module(new Decoder())

}