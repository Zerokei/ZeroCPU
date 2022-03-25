package zeroCPU.cache
import chisel3._
import chisel3.util._
import zeroCPU.utils._
import zeroCPU.const.ZeroConfig._


class ICacheIO extends Bundle{
	val rom	 		= Flipped(new RomIO())
  val addr    = Input(UInt(DLEN.W))
	val data  	= Output(UInt(DLEN.W))
  val stall		= Output(Bool())
}
class ICache extends Module{
  val io = IO(new ICacheIO())
  val regs = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(DLEN.W))))
  val tags = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(TAG_LEN.W))))
  
  val addr  = RegInit(0.U(DLEN.W))
  val data  = RegInit(0.U(DLEN.W))
  val wait_type = RegInit(0.U(WAIT_SIG_LEN.W))
  val data_out  = RegInit(0.U(DLEN.W)) 

  val index = Wire(UInt(INDEX_LEN.W))
  val tag   = Wire(UInt(TAG_LEN.W))
  index := addr & CAMASK
  tag   := addr >> INDEX_LEN

  val rom_addr = RegInit(0.U(DLEN.W))
  io.rom.addr := rom_addr

  when(wait_type === WAIT_READ_NEW){
    when(io.rom.valid){
      wait_type     := WAIT_NOTHING
      tags(index)   := tag
      regs(index)   := io.rom.data
      data_out := io.rom.data
    }
  }.elsewhen(wait_type === WAIT_NOTHING){
    addr  := io.addr
    data  := io.data
    when(tags(index) === tag){//hit
      data_out      := regs(index)
    }.otherwise{
      rom_addr   := addr
      wait_type := WAIT_READ_NEW
    }
  }
  io.stall := (wait_type =/= WAIT_NOTHING)
  io.data := data_out
}

class DCacheIO extends Bundle{
	val ram	 		= Flipped(new RamIO())
	val wen 		= Input(Bool())
	val valid   = Input(Bool())
	val data_i 	= Input(UInt(DLEN.W))
  val addr    = Input(UInt(DLEN.W))
	val data_o	= Output(UInt(DLEN.W))
  val stall		= Output(Bool())
}
class DCache extends Module{
  val io = IO(new DCacheIO())
  val regs = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(DLEN.W))))
  val tags = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(TAG_LEN.W))))
  val dirtys = RegInit(VecInit(Seq.fill(CACOUNTS)(false.B)))
  
  val addr  = RegInit(0.U(DLEN.W))
  val data  = RegInit(0.U(DLEN.W))
  val wen   = RegInit(false.B)
  val wait_type = RegInit(0.U(WAIT_SIG_LEN.W))
  val data_out  = RegInit(0.U(DLEN.W)) 
  io.data_o := data_out

  val index = Wire(UInt(INDEX_LEN.W))
  val tag   = Wire(UInt(TAG_LEN.W))
  index := addr & CAMASK
  tag   := addr >> INDEX_LEN

  val ram_data_i = RegInit(0.U(DLEN.W))
  val ram_addr   = RegInit(0.U(DLEN.W))
  val ram_wen    = RegInit(false.B)
  io.ram.data_i := ram_data_i
  io.ram.addr   := ram_addr
  io.ram.wen    := ram_wen

  when(wait_type === WAIT_NOTHING){
    when(io.valid){
      wen   := io.wen
      addr  := io.addr
      data  := io.data_i

      when(io.wen){//write
        when(tags(index) === tag){//hit
          tags(index)   := tag
          regs(index)   := data
          dirtys(index) := true.B
        }.elsewhen(dirtys(index) === false.B){//miss & clean
          ram_data_i   := addr
          wait_type := WAIT_READ_NEW
        }.otherwise{//miss & dirty
          ram_wen    := true.B
          ram_addr   := Cat(tags(index), index)
          ram_data_i := regs(index)
          wait_type := WAIT_WRITE_OLD
        }
      }.otherwise{//read
        when(tags(index) === tag){//hit
          data_out      := regs(index)
        }.elsewhen(dirtys(index) === false.B){//miss & clean
          ram_addr   := addr
          wait_type := WAIT_READ_NEW
        }.otherwise{//miss & dirty
          ram_addr   := Cat(tags(index), index)
          ram_data_i := regs(index)
          wait_type := WAIT_WRITE_OLD
        }
      }
    }
  }.elsewhen(wait_type === WAIT_WRITE_OLD){
    when(io.ram.valid){
      ram_addr := addr
      ram_wen  := false.B
      wait_type := WAIT_READ_NEW    
    }
  }.elsewhen(wait_type === WAIT_READ_NEW){
    when(io.ram.valid){
      wait_type     := WAIT_NOTHING
      when(wen){//write
        tags(index)   := tag
        regs(index)   := data
        dirtys(index) := true.B
      }.otherwise{//read
        tags(index)   := tag
        regs(index)   := io.ram.data_o
        dirtys(index) := false.B
        data_out := io.ram.data_o
      }
    }
  }
  io.stall := (wait_type =/= WAIT_NOTHING)
}
