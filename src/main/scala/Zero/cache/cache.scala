package zeroCPU.cache
import chisel3._
import chisel3.util._
import zeroCPU.utils._
import chisel3.util.experimental.BoringUtils
import zeroCPU.const.ZeroConfig._


class ICacheIO extends Bundle{
	val rom	 		= Flipped(new RomIO())
  val valid   = Input(Bool())
  val addr    = Input(UInt(DLEN.W))
	val data  	= Output(UInt(DLEN.W))
  val stall		= Output(Bool())
}
class ICache extends Module{
  val io = IO(new ICacheIO())
  val regs = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(DLEN.W))))
  val tags = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(TAG_LEN.W))))
  val valids = RegInit(VecInit(Seq.fill(CACOUNTS)(false.B)))
  
  val wait_type = RegInit(WAIT_NOTHING)

  val index = Wire(UInt(INDEX_LEN.W))
  val tag   = Wire(UInt(TAG_LEN.W))
  index := io.addr & CAMASK
  tag   := io.addr >> INDEX_LEN

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
    // when(io.rom.valid){// rom write back
      io.rom.addr   := io.addr  // because it is Combinatorial logic
      io.stall      := false.B
      io.data       := io.rom.data
      tags(index)   := tag
      regs(index)   := io.rom.data
      wait_type     := WAIT_NOTHING
    // }.otherwise{// wait for rom
    //   io.rom.addr   := 0.U(DLEN.W)
    //   io.stall      := true.B
    //   io.data       := 0.U(DLEN.W)
    // }
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
class DCache extends Module{
  val io = IO(new DCacheIO())
  val regs = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(DLEN.W))))
  val tags = RegInit(VecInit(Seq.fill(CACOUNTS)(0.U(TAG_LEN.W))))
  val valids = RegInit(VecInit(Seq.fill(CACOUNTS)(false.B)))
  val dirtys = RegInit(VecInit(Seq.fill(CACOUNTS)(false.B)))
  
  val wait_type = RegInit(0.U(WAIT_SIG_LEN.W))

  val index = Wire(UInt(INDEX_LEN.W))
  val tag   = Wire(UInt(TAG_LEN.W))
  index := io.addr & CAMASK
  tag   := io.addr >> INDEX_LEN

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
          io.ram.addr     := Cat(tags(index), index)
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
          io.ram.addr     := Cat(tags(index), index)
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
      io.ram.addr     := Cat(tags(index), index)
      io.ram.wen      := true.B
      io.ram.data_i   := regs(index)
    }
  }.elsewhen(wait_type === WAIT_READ_NEW){
    // when(io.ram.valid){
    when(io.wen){//write
      wait_type       := WAIT_NOTHING
      tags(index)     := tag
      regs(index)     := io.data_i
      dirtys(index)   := true.B
      io.data_o       := 0.U(DLEN.W)
      io.stall        := false.B
      io.ram.addr     := 0.U(DLEN.W)
      io.ram.wen      := false.B
      io.ram.data_i   := 0.U(DLEN.W)
    }.otherwise{//read
      wait_type       := WAIT_NOTHING
      tags(index)     := tag
      regs(index)     := io.ram.data_o
      dirtys(index)   := false.B
      io.data_o       := io.ram.data_o
      io.stall        := false.B
      io.ram.addr     := io.addr // because it is Combinatorial logic
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
  // val debug = Wire(UInt(DLEN.W))
  // debug := tags(4)
  BoringUtils.addSource(io.ram.addr, "debug2")
  BoringUtils.addSource(io.ram.data_o, "debug3")
}
