package zeroCPU.core
import chisel3._
import chisel3.util._
import chisel3.experimental._
import zeroCPU.wow._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class CPU extends Module{
  val io = IO(new Bundle{
    val inst = Input(UInt(32.W)) // inst from rom
		val data_in = Input(UInt(32.W)) // ram -> CPU
		val addr_out = Output(UInt(32.W)) // CPU -> ram(addr)
		val data_out = Output(UInt(32.W)) // CPU -> ram(data)
		val mem_write = Output(UInt(32.W)) // CPU -> ram(mem_write)
		val pc_out = Output(UInt(32.W)) // CPU -> rom(pc)
	})
    
    
	//val pc_rst = 0.U(32.W)
	//val pc_pst = RegNext(pc_rst, 0.U)
	//io.pc_out := pc_pst
	
	// Dec = Module(new Decoder())

}