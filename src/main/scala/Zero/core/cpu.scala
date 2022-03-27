package zeroCPU.core
import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import zeroCPU.wow._
import zeroCPU.const.ZeroConfig._
import zeroCPU.utils._
import zeroCPU.cache._


class CPUIO extends Bundle{
  val rom = Flipped(new RomIO())
  val ram = Flipped(new RamIO())
}
class CPU(verilator: Boolean = false) extends Module{
  val io = IO(new CPUIO)
  
  val icache  = Module(new ICache(verilator))
  val dcache  = Module(new DCache(verilator))
  val alu     = Module(new ALU(verilator))
  val csr_mod = Module(new CSR_MOD(verilator))
  val decode  = Module(new Decoder(verilator))
  val reg     = Module(new Register(verilator))
  val fwd     = Module(new Forwarding(verilator))
  val junion  = Module(new Jump_Union(verilator))

  val flush_ID  = Wire(Bool())
  val flush_IF  = Wire(Bool())
  // val stall_WB  = Wire(Bool())
  val stall_MEM = Wire(Bool())
  val stall_EXE = Wire(Bool())
  val stall_ID  = Wire(Bool())
  val stall_IF  = Wire(Bool())
  val stall_PC  = Wire(Bool())

  val ro1_ID   = Wire(UInt(DLEN.W))
  val ro2_ID   = Wire(UInt(DLEN.W))

  val inst_IF = Wire(UInt(DLEN.W))
  val pc_IF = 
    if(verilator)
      RegInit("h80000000".U(DLEN.W))
    else 
      RegInit(0.U(DLEN.W))
  val pc_ID   = RegEnable(Mux(flush_IF, 0.U(DLEN.W), pc_IF), 0.U(DLEN.W), !stall_IF)
  val inst_ID = RegEnable(Mux(flush_IF, NOP, inst_IF), NOP, !stall_IF)

  // Wires Definition
  val mux1_EXE = Wire(UInt(DLEN.W))
  val mux2_EXE = Wire(UInt(DLEN.W))
  val ali1_EXE = Wire(UInt(DLEN.W))
  val ali2_EXE = Wire(UInt(DLEN.W))
  val alo_EXE  = Wire(UInt(DLEN.W))
  val fsig1_EXE = Wire(UInt(FWD_SIG_LEN.W))
  val fsig2_EXE = Wire(UInt(FWD_SIG_LEN.W))
  val mem_data_MEM = Wire(UInt(DLEN.W))
  val csr_data_EXE =  Wire(UInt(SLEN.W))
  val csr_pc_EXE   =  Wire(UInt(SLEN.W))
  val csr_call_for_int = Wire(Bool())
  val reg_data_WB  = Wire(UInt(DLEN.W))

	
  //decoder module
  decode.io.inst := inst_ID
  val pc_src_ID     = decode.io.pc_src
  val branch_sig_ID = decode.io.branch_sig
  val mem_to_reg_ID = decode.io.mem_to_reg
  val reg_write_ID  = decode.io.reg_write
  val op_src1_ID    = decode.io.op_src1
  val op_src2_ID    = decode.io.op_src2
  val alu_op_ID     = decode.io.alu_op
  val mem_op_ID     = decode.io.mem_op
  val csr_write_ID  = decode.io.csr_write
  val imm_ID        = decode.io.imm
  val rs1_ID        = decode.io.rs1
  val rs2_ID        = decode.io.rs2
  val rd_ID         = decode.io.rd
  val csr_index_ID  = decode.io.csr_index

  // Registers Definition
  val ro1_EXE = RegEnable(Mux(flush_ID, 0.U(DLEN.W), ro1_ID), 0.U(DLEN.W), !stall_ID)
  val ro2_EXE = RegEnable(Mux(flush_ID, 0.U(DLEN.W), ro2_ID), 0.U(DLEN.W), !stall_ID)
  val rs1_EXE = RegEnable(Mux(flush_ID, 0.U(NREGS_BIT.W), rs1_ID), 0.U(NREGS_BIT.W), !stall_ID)
  val rs2_EXE = RegEnable(Mux(flush_ID, 0.U(NREGS_BIT.W), rs2_ID), 0.U(NREGS_BIT.W), !stall_ID)
  val rd_EXE  = RegEnable(Mux(flush_ID, 0.U(NREGS_BIT.W), rd_ID),  0.U(NREGS_BIT.W), !stall_ID)
  val pc_EXE  = RegEnable(Mux(flush_ID, 0.U(DLEN.W), pc_ID),  0.U(LEN.W), !stall_ID)
  val imm_EXE = RegEnable(Mux(flush_ID, 0.U(DLEN.W), imm_ID), 0.U(DLEN.W), !stall_ID)
  val op_src1_EXE     = RegEnable(Mux(flush_ID, OP1_X, op_src1_ID), 0.U(OP1_SIG_LEN.W), !stall_ID)
  val op_src2_EXE     = RegEnable(Mux(flush_ID, OP2_X, op_src2_ID), 0.U(OP2_SIG_LEN.W), !stall_ID)
  val reg_write_EXE   = RegEnable(Mux(flush_ID, REN_X, reg_write_ID), false.B, !stall_ID)
  val mem_op_EXE      = RegEnable(Mux(flush_ID, MEM_X,  mem_op_ID), 0.U(MEM_SIG_LEN.W), !stall_ID)
  val branch_sig_EXE  = RegEnable(Mux(flush_ID, BR_X,  branch_sig_ID), 0.U(BR_SIG_LEN.W), !stall_ID)
  val csr_write_EXE   = RegEnable(Mux(flush_ID, CSW_X, csr_write_ID),  0.U(CSW_SIG_LEN.W), !stall_ID)
  val csr_index_EXE   = RegEnable(Mux(flush_ID, CSR_X, csr_index_ID),  0.U(CSRS_SIZE.W), !stall_ID)
  val alu_op_EXE      = RegEnable(Mux(flush_ID, ALU_X, alu_op_ID), 0.U(ALU_SIG_LEN.W), !stall_ID)
  val mem_to_reg_EXE  = RegEnable(Mux(flush_ID, WB_X, mem_to_reg_ID), 0.U(WB_SIG_LEN.W), !stall_ID)

  val mux2_MEM  = RegEnable(mux2_EXE, 0.U(DLEN.W), !stall_EXE)
  val rs1_MEM   = RegEnable(rs1_EXE,  0.U(NREGS_BIT.W), !stall_EXE)
  val rs2_MEM   = RegEnable(rs2_EXE,  0.U(NREGS_BIT.W), !stall_EXE)
  val rd_MEM    = RegEnable(rd_EXE,   0.U(NREGS_BIT.W), !stall_EXE)
  val pc_MEM    = RegEnable(pc_EXE,   0.U(DLEN.W), !stall_EXE)
  val alo_MEM   = RegEnable(alo_EXE,  0.U(DLEN.W), !stall_EXE)
  val reg_write_MEM   = RegEnable(reg_write_EXE,  false.B, !stall_EXE)
  val mem_op_MEM      = RegEnable(mem_op_EXE,     0.U(MEM_SIG_LEN.W), !stall_EXE)
  val mem_to_reg_MEM  = RegEnable(mem_to_reg_EXE, 0.U(WB_SIG_LEN.W), !stall_EXE)
  val csr_data_MEM    = RegEnable(csr_data_EXE, 0.U(SLEN.W), !stall_EXE) 
  val csr_write_MEM   = RegEnable(csr_write_EXE,  0.U(CSW_SIG_LEN.W), !stall_EXE)
  
  val alo_WB = RegEnable(alo_MEM, 0.U(DLEN.W), !stall_MEM)
  val rs1_WB = RegEnable(rs1_MEM, 0.U(NREGS_BIT.W), !stall_MEM)
  val rs2_WB = RegEnable(rs2_MEM, 0.U(NREGS_BIT.W), !stall_MEM)
  val rd_WB  = RegEnable(rd_MEM,  0.U(NREGS_BIT.W), !stall_MEM)
  val pc_WB  = RegEnable(pc_MEM,  0.U(DLEN.W), !stall_MEM)
  val mem_data_WB     = RegEnable(mem_data_MEM, 0.U(DLEN.W), !stall_MEM)
  val csr_data_WB     = RegEnable(csr_data_MEM, 0.U(SLEN.W), !stall_MEM) 
  val reg_write_WB    = RegEnable(reg_write_MEM, false.B, !stall_MEM) 
  val mem_to_reg_WB   = RegEnable(mem_to_reg_MEM, 0.U(WB_SIG_LEN.W), !stall_MEM)

  // register module
  reg.io.rs1      := rs1_ID
  reg.io.rs2      := rs2_ID
  reg.io.rd       := rd_WB
  reg.io.reg_write:= reg_write_WB
  reg.io.data_in  := reg_data_WB  
  ro1_ID          := reg.io.data_out1
  ro2_ID          := reg.io.data_out2


  // forwarding module
  fwd.io.rd_MEM   := rd_MEM
  fwd.io.rd_WB    := rd_WB
  fwd.io.rs1_EXE  := rs1_EXE
  fwd.io.rs2_EXE  := rs2_EXE
  fwd.io.rw_MEM   := reg_write_MEM
  fwd.io.rw_WB    := reg_write_WB
  fwd.io.csw_MEM  := csr_write_MEM
  fsig1_EXE       := fwd.io.fsig1
  fsig2_EXE       := fwd.io.fsig2

  //  CSR module
  csr_mod.io.sig    :=  csr_write_EXE
  csr_mod.io.csr    :=  csr_index_EXE
  csr_mod.io.pc_in  :=  pc_EXE
  csr_mod.io.data_i :=  mux1_EXE
  csr_data_EXE      :=  csr_mod.io.data_o
  csr_pc_EXE        :=  csr_mod.io.pc_out
  csr_call_for_int  :=  csr_mod.io.call_for_int

  //  Forward data
  mux1_EXE := 
    MuxLookup(fsig1_EXE,  ro1_EXE ,
      Array(
            FWD_REG     ->ro1_EXE ,
            FWD_MEM     ->alo_MEM ,
            FWD_WB      ->reg_data_WB ,
            FWD_CSR     ->csr_data_MEM 
          ))
  mux2_EXE := 
    MuxLookup(fsig2_EXE,  ro2_EXE ,
      Array(
            FWD_REG     ->ro2_EXE ,
            FWD_MEM     ->alo_MEM ,
            FWD_WB      ->reg_data_WB ,
            FWD_CSR     ->csr_data_MEM 
          ))

  // Pre for ALU module
  ali1_EXE := 
    MuxLookup(op_src1_EXE,  mux1_EXE,
      Array(
            OP1_RS1       ->mux1_EXE,
            OP1_IMM       ->imm_EXE
          ))
  ali2_EXE :=
    MuxLookup(op_src2_EXE,  mux2_EXE,
      Array(
            OP2_RS2       ->mux2_EXE,
            OP2_IMM       ->imm_EXE ,
            OP2_PC        ->pc_EXE
          ))

  // ALU module
  alu.io.in_a   :=  ali1_EXE
  alu.io.in_b   :=  ali2_EXE
  alu.io.alu_op :=  alu_op_EXE  
  alo_EXE := alu.io.alu_out

  // Jump Union module
  junion.io.b_type  := branch_sig_EXE
  junion.io.in1     := mux1_EXE
  junion.io.in2     := mux2_EXE
  junion.io.call_for_int := csr_call_for_int

  // WB state
  reg_data_WB := 
    MuxLookup(mem_to_reg_WB,  alo_WB,
      Array(
            WB_ALU          ->alo_WB,
            WB_PC4          ->(pc_WB + 4.U),
            WB_RAM          ->mem_data_WB,
            WB_CSR          ->csr_data_WB
          ))

  dcache.io.ram <> io.ram
  icache.io.rom <> io.rom

  // Rom Module
  icache.io.valid  := true.B
  icache.io.addr   := pc_IF
  inst_IF          := icache.io.data
  val icache_stall  = icache.io.stall
  // Ram Module
  dcache.io.valid  := mem_op_MEM =/= MEM_X
  dcache.io.wen    := mem_op_MEM === MEM_WRT
  dcache.io.addr   := alo_MEM
  dcache.io.data_i := mux2_MEM
  mem_data_MEM     := dcache.io.data_o
  val dcache_stall  = dcache.io.stall

  // Detect Module
  // EXE state: when Jump
  val cflt_cach = Wire(Bool()) // stall from dcache and icache
  val cflt_data = Wire(Bool()) // R型/B型/ebreak/ecall/csrrs->L型 ID->插1个bubble(PC锁)
  val cflt_ctrl = Wire(Bool()) // B型/J型跳转/mret/ebreak/ecall EX<-插2个bubble(PC不锁)
  cflt_cach := icache_stall || dcache_stall
  cflt_data := (mem_to_reg_EXE === WB_RAM) && (rd_EXE === rs1_ID || rd_EXE === rs2_ID)
  cflt_ctrl := junion.io.pc_src =/= PC_4

  flush_ID := cflt_ctrl || cflt_data
  flush_IF := cflt_ctrl
  // ID state: 
  // stall_WB  := cflt_cach
  stall_MEM := cflt_cach
  stall_EXE := cflt_cach
  stall_ID  := cflt_cach
  stall_IF  := cflt_cach || cflt_data
  stall_PC  := cflt_cach || cflt_data

  // PC module -2
  when(!stall_IF){
    pc_IF :=
      MuxLookup( junion.io.pc_src,  (pc_IF + 4.U),
        Array(
              PC_4                ->(pc_IF + 4.U),
              PC_B                ->alo_EXE,
              PC_J                ->alo_EXE,
              PC_JR               ->(alo_EXE & (~1.U(DLEN.W))),
              PC_CSR              ->csr_pc_EXE
            ))
  }


  // for verilator [debug]
  if(verilator){
    val count_IF  = RegEnable(Mux(flush_IF, 0.U(1.W), 1.U(1.W)), 0.U(1.W), !stall_IF)
    val count_ID  = RegEnable(Mux(flush_ID, 0.U(1.W), count_IF), 0.U(1.W), !stall_ID)
    val count_EXE = RegEnable(count_ID, !stall_EXE)
    val count_MEM = RegEnable(count_ID, !stall_MEM)

    // wait for register to write
    val count_WB  = RegNext(Mux(stall_MEM, 0.U(1.W), count_EXE), 0.U(1.W))
    val count_Final = RegNext(count_WB, 0.U(1.W))

    // extend inst for debug
    val inst_EXE = RegEnable(Mux(flush_ID, 0.U(32.W), inst_ID), 0.U(DLEN.W), !stall_ID)
    val inst_MEM = RegEnable(inst_EXE, 0.U(DLEN.W), !stall_EXE)
    val inst_WB  = RegEnable(inst_MEM, 0.U(DLEN.W), !stall_MEM)
    BoringUtils.addSource(count_Final, "dt_counter")
    // BoringUtils.addSource(inst_EXE, "debug0")
    // BoringUtils.addSource(alo_WB, "debug1")
    // BoringUtils.addSource(icache.io.data, "debug2")
    BoringUtils.addSource(dcache.io.addr, "debug0")
    BoringUtils.addSource(dcache.io.wen, "debug1")
    // BoringUtils.addSource(dcache.io., "debug2")
    // BoringUtils.addSource(dcache.io.addr, "debug3")
  }else{
    BoringUtils.addSource(pc_IF, "debug0")
  }
}