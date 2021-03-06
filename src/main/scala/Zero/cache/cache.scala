package zeroCPU.cache
import chisel3._
import chisel3.util._
import zeroCPU.utils._
import chisel3.util.experimental.BoringUtils
import zeroCPU.const.ZeroConfig._

object CacheNumbers {
  val INDEX_LEN   = 7
  val OFFSET_LEN  = 2
  val TAG_LEN     = 4
  val REST_LEN    = DLEN - TAG_LEN - OFFSET_LEN - INDEX_LEN
  val Implement   = 0.U(REST_LEN.W)

  val CACOUNTS    = 1<<INDEX_LEN
  val CAMASK      = ((1<<INDEX_LEN)-1).U
  val CTMASK      = ((1<<TAG_LEN)-1).U
}

class ICacheIO extends Bundle{
	val rom	 		= Flipped(new RomIO())
  val valid   = Input(Bool())
  val addr    = Input(UInt(DLEN.W))
	val data  	= Output(UInt(DLEN.W))
  val stall		= Output(Bool())
}
class ICache(verilator: Boolean = false) extends Module{
  import CacheNumbers._
  val io = IO(new ICacheIO())
  val regs = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(DLEN.W))))
  val tags = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(TAG_LEN.W))))
  val valids = RegInit(VecInit(Seq.fill(CACOUNTS)(false.B)))
  
  val wait_type = RegInit(WAIT_NOTHING)

  val index = Wire(UInt(INDEX_LEN.W))
  val tag   = Wire(UInt(TAG_LEN.W))
  index := (io.addr >> OFFSET_LEN) & CAMASK
  tag   := (io.addr >> (INDEX_LEN+OFFSET_LEN)) & CTMASK

  when(wait_type === WAIT_NOTHING){//wait for query
    when(io.valid){
      when(valids(index) && tags(index) === tag){//hit
        io.rom.addr := 0.U(DLEN.W)
        io.stall    := false.B
        io.data     := regs(index)
      }.otherwise{// miss
        valids(index) := true.B
        io.rom.addr   := io.addr
        io.stall      := true.B
        io.data       := 0.U(DLEN.W)
        wait_type     := WAIT_READ_NEW
      }
    }.otherwise{// no query
      io.rom.addr   := 0.U(DLEN.W)
      io.stall      := false.B
      io.data       := regs(index)
    }
  }.elsewhen(wait_type === WAIT_READ_NEW){//wait for rom (already or not ready)
    when(io.rom.valid){// rom write back
      io.rom.addr   := io.addr
      io.stall      := true.B
      io.data       := 0.U(DLEN.W)
      tags(index)   := tag
      regs(index)   := io.rom.data
      wait_type     := WAIT_NOTHING
    }.otherwise{// wait for rom
      io.rom.addr   := io.addr
      io.stall      := true.B
      io.data       := 0.U(DLEN.W)
    }
  }.otherwise{// exception
    io.rom.addr     := 0.U(DLEN.W)
    io.stall        := true.B
    io.data         := 0.U(DLEN.W)
  }
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
class DCache(verilator: Boolean = false) extends Module{
  import CacheNumbers._
  
  val io = IO(new DCacheIO())
  val regs = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(DLEN.W))))
  val tags = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(TAG_LEN.W))))
  val valids = RegInit(VecInit(Seq.fill(CACOUNTS)(false.B)))
  val dirtys = RegInit(VecInit(Seq.fill(CACOUNTS)(false.B)))
  
  val wait_type = RegInit(0.U(WAIT_SIG_LEN.W))

  val index     = Wire(UInt(INDEX_LEN.W))
  val tag       = Wire(UInt(TAG_LEN.W))
  val pas_addr  = Wire(UInt(DLEN.W))
  index := (io.addr >> OFFSET_LEN) & CAMASK
  tag   := (io.addr >> (INDEX_LEN+OFFSET_LEN)) & CTMASK
  pas_addr := Cat(Implement, tags(index), index, 0.U(OFFSET_LEN.W))

  when(wait_type === WAIT_NOTHING){
    when(io.valid){
      when(io.wen){//write
        when(valids(index) && tags(index) === tag){//hit
          tags(index)     := tag
          regs(index)     := io.data_i
          dirtys(index)   := true.B
          io.data_o       := 0.U(DLEN.W)
          io.stall        := false.B
          io.ram.addr     := 0.U(DLEN.W)
          io.ram.wen      := false.B
          io.ram.data_i   := 0.U(DLEN.W)
        }.elsewhen(dirtys(index) === false.B){//miss & clean
          valids(index)   := true.B
          wait_type       := WAIT_READ_NEW
          io.data_o       := 0.U(DLEN.W)
          io.stall        := true.B
          io.ram.addr     := io.addr
          io.ram.wen      := false.B
          io.ram.data_i   := 0.U(DLEN.W)
        }.otherwise{//miss & dirty
          wait_type       := WAIT_WRITE_OLD
          io.data_o       := 0.U(DLEN.W)
          io.stall        := true.B
          io.ram.addr     := pas_addr
          io.ram.wen      := true.B
          io.ram.data_i   := regs(index)
        }
      }.otherwise{//read
        when(valids(index) && tags(index) === tag){//hit
          io.data_o       := regs(index)
          io.stall        := false.B
          io.ram.addr     := 0.U(DLEN.W)
          io.ram.wen      := false.B
          io.ram.data_i   := 0.U(DLEN.W)
        }.elsewhen(dirtys(index) === false.B){//miss & clean
          valids(index)   := true.B
          wait_type       := WAIT_READ_NEW
          io.data_o       := 0.U(DLEN.W)
          io.stall        := true.B
          io.ram.addr     := io.addr
          io.ram.wen      := false.B
          io.ram.data_i   := 0.U(DLEN.W)
        }.otherwise{//miss & dirty
          wait_type       := WAIT_WRITE_OLD
          io.data_o       := 0.U(DLEN.W)
          io.stall        := true.B
          io.ram.addr     := pas_addr
          io.ram.wen      := true.B
          io.ram.data_i   := regs(index)
        }
      }
    }.otherwise{
      io.data_o       := 0.U(DLEN.W)
      io.stall        := false.B
      io.ram.addr     := 0.U(DLEN.W)
      io.ram.wen      := false.B
      io.ram.data_i   := 0.U(DLEN.W)
    }
  }.elsewhen(wait_type === WAIT_WRITE_OLD){
    when(io.ram.valid){ // already write
      wait_type       := WAIT_READ_NEW
      io.data_o       := 0.U(DLEN.W)
      io.stall        := true.B
      io.ram.addr     := io.addr
      io.ram.wen      := false.B
      io.ram.data_i   := 0.U(DLEN.W)
    }.otherwise{ // wait to write
      io.data_o       := 0.U(DLEN.W)
      io.stall        := true.B
      io.ram.addr     := pas_addr
      io.ram.wen      := true.B
      io.ram.data_i   := regs(index)
    }
  }.elsewhen(wait_type === WAIT_READ_NEW){
    when(io.ram.valid){
      wait_type       := WAIT_NOTHING
      tags(index)     := tag
      regs(index)     := io.ram.data_o
      dirtys(index)   := false.B
      io.data_o       := 0.U(DLEN.W)
      io.stall        := true.B
      io.ram.addr     := io.addr
      io.ram.wen      := false.B
      io.ram.data_i   := 0.U(DLEN.W)
    }.otherwise{// wait for ram
      io.data_o       := 0.U(DLEN.W)
      io.stall        := true.B
      io.ram.addr     := io.addr
      io.ram.wen      := false.B
      io.ram.data_i   := 0.U(DLEN.W)
    }
  }.otherwise{// invalid state
      io.data_o       := 0.U(DLEN.W)
      io.stall        := true.B
      io.ram.addr     := 0.U(DLEN.W)
      io.ram.wen      := false.B
      io.ram.data_i   := 0.U(DLEN.W)
  }

  if(verilator){
    BoringUtils.addSource(io.ram.addr, "debug2")
    BoringUtils.addSource(io.ram.data_o, "debug3")
  }else{
    val switch = WireInit(0.U(12.W))
    val reg_choose = WireInit(0.U(DLEN.W))
    val index = switch(6,0)
    val debug3 = Cat(Implement, dirtys(index), valids(index), tags(index), 0.U(OFFSET_LEN.W))
    reg_choose := regs(switch(6,0))
		BoringUtils.addSink(switch, "switch")
    BoringUtils.addSource(reg_choose, "debug2")
    BoringUtils.addSource(debug3, "debug3")
  }
}
