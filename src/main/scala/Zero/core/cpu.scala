package zeroCPU.core
import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
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
  
  val flush_ID = Wire(Bool())
  val flush_IF = Wire(Bool())
  val stall_IF = Wire(Bool())
  val stall_PC = Wire(Bool())

  val ro1_ID   = Wire(UInt(DLEN.W))
  val ro2_ID   = Wire(UInt(DLEN.W))

  // PC module -1
  val inst_IF = io.inst
  val dat_MEM = io.data_in
  val pc_IF = RegInit(0.U(DLEN.W))
  val pc_ID   = RegInit(0.U(DLEN.W))
  val inst_ID = RegInit(NOP)
  when(!stall_PC){
    pc_ID   := Mux(flush_IF, 0.U(DLEN.W), pc_IF)
    inst_ID := Mux(flush_IF, NOP, inst_IF)
  }
  io.pc_out := pc_IF

  val mux1_EXE = Wire(UInt(DLEN.W))
  val mux2_EXE = Wire(UInt(DLEN.W))
  val ali1_EXE = Wire(UInt(DLEN.W))
  val ali2_EXE = Wire(UInt(DLEN.W))
  val alo_EXE  = Wire(UInt(DLEN.W))
  val fsig1_EXE = Wire(UInt(FWD_SIG_LEN.W))
  val fsig2_EXE = Wire(UInt(FWD_SIG_LEN.W))
  val mem_data_MEM = Wire(UInt(DLEN.W))
  val csr_data_MEM =  Wire(UInt(SLEN.W))
  val csr_pc_MEM   =  Wire(UInt(SLEN.W))
  val reg_data_WB  = Wire(UInt(DLEN.W))

	
  //decoder module
  val decode = Module(new Decoder())
  decode.io.inst := inst_ID
  val pc_src_ID     = decode.io.pc_src
  val branch_sig_ID = decode.io.branch_sig
  val mem_to_reg_ID = decode.io.mem_to_reg
  val reg_write_ID  = decode.io.reg_write
  val op_src1_ID    = decode.io.op_src1
  val op_src2_ID    = decode.io.op_src2
  val alu_op_ID    = decode.io.alu_op
  val mem_write_ID  = decode.io.mem_to_reg
  val csr_write_ID  = decode.io.csr_write

  val imm_ID        = decode.io.imm

  val rs1_ID        = decode.io.rs1
  val rs2_ID        = decode.io.rs2
  val rd_ID         = decode.io.rd
  val csr_index_ID  = decode.io.csr_index



  val ro1_EXE = RegNext(Mux(flush_ID, 0.U(DLEN.W), ro1_ID), 0.U(DLEN.W))
  val ro2_EXE = RegNext(Mux(flush_ID, 0.U(DLEN.W), ro2_ID), 0.U(DLEN.W))
  val rs1_EXE = RegNext(Mux(flush_ID, 0.U(NREGS_BIT.W), rs1_ID), 0.U(NREGS_BIT.W))
  val rs2_EXE = RegNext(Mux(flush_ID, 0.U(NREGS_BIT.W), rs2_ID), 0.U(NREGS_BIT.W))
  val rd_EXE  = RegNext(Mux(flush_ID, 0.U(NREGS_BIT.W), rd_ID),  0.U(NREGS_BIT.W))
  val pc_EXE  = RegNext(Mux(flush_ID, 0.U(DLEN.W), pc_ID),  0.U(LEN.W))
  val imm_EXE = RegNext(Mux(flush_ID, 0.U(DLEN.W), imm_ID), 0.U(DLEN.W))
  val op_src1_EXE     = RegNext(Mux(flush_ID, OP1_X, op_src1_ID), 0.U(OP1_SIG_LEN.W))
  val op_src2_EXE     = RegNext(Mux(flush_ID, OP2_X, op_src2_ID), 0.U(OP2_SIG_LEN.W))
  val reg_write_EXE   = RegNext(Mux(flush_ID, REN_X, reg_write_ID), false.B)
  val mem_write_EXE   = RegNext(Mux(flush_ID, MW_X,  mem_write_ID), false.B)
  val branch_sig_EXE  = RegNext(Mux(flush_ID, BR_X,  branch_sig_ID), 0.U(BR_SIG_LEN.W))
  val csr_write_EXE   = RegNext(Mux(flush_ID, CSW_X, csr_write_ID),  0.U(CSW_SIG_LEN.W))
  val csr_index_EXE   = RegNext(Mux(flush_ID, CSR_X, csr_index_ID),  0.U(CSRS_SIZE.W))
  val alu_op_EXE      = RegNext(Mux(flush_ID, ALU_X, alu_op_ID), 0.U(ALU_SIG_LEN.W))
  val mem_to_reg_EXE  = RegNext(Mux(flush_ID, WB_X, mem_to_reg_ID), 0.U(WB_SIG_LEN.W))

  val mux2_MEM  = RegNext(mux2_EXE, 0.U(DLEN.W))
  val rs1_MEM   = RegNext(rs1_EXE,  0.U(NREGS_BIT.W))
  val rs2_MEM   = RegNext(rs2_EXE,  0.U(NREGS_BIT.W))
  val rd_MEM    = RegNext(rd_EXE,   0.U(NREGS_BIT.W))
  val pc_MEM    = RegNext(pc_EXE,   0.U(DLEN.W))
  val alo_MEM   = RegNext(alo_EXE,  0.U(DLEN.W))
  val reg_write_MEM   = RegNext(reg_write_EXE,  false.B)
  val mem_write_MEM   = RegNext(mem_write_EXE,  false.B)
  val mem_to_reg_MEM  = RegNext(mem_to_reg_EXE, 0.U(WB_SIG_LEN.W))
  val csr_write_MEM   = RegNext(csr_write_EXE,  0.U(CSW_SIG_LEN.W))

  val alo_WB = RegNext(alo_MEM, 0.U(DLEN.W))
  val rs1_WB = RegNext(rs1_MEM, 0.U(NREGS_BIT.W))
  val rs2_WB = RegNext(rs2_MEM, 0.U(NREGS_BIT.W))
  val rd_WB  = RegNext(rd_MEM,  0.U(NREGS_BIT.W))
  val pc_WB  = RegNext(pc_MEM,  0.U(DLEN.W))
  val dat_WB = RegNext(dat_MEM, 0.U(DLEN.W))
  val mem_data_WB     = RegNext(mem_data_MEM, 0.U(DLEN.W))
  val csr_data_WB     = RegNext(csr_data_MEM, 0.U(SLEN.W)) 
  val reg_write_WB    = RegNext(reg_write_MEM, false.B) 
  val mem_to_reg_WB   = RegNext(mem_to_reg_MEM, 0.U(WB_SIG_LEN.W))

  // register module
  val reg = Module(new Register())
  reg.io.rs1      := rs1_ID
  reg.io.rs2      := rs2_ID
  reg.io.rd       := rd_ID
  reg.io.reg_write:= reg_write_WB
  reg.io.data_in  := reg_data_WB  
  ro1_ID          := reg.io.data_out1
  ro2_ID          := reg.io.data_out2


  // forwarding module
  val fwd = Module(new Forwarding())
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
  val csr_mod = Module(new CSR_MOD())
  csr_mod.io.pc_in  :=  pc_EXE
  csr_mod.io.sig    :=  csr_write_EXE
  csr_mod.io.rd     :=  rd_EXE
  csr_mod.io.csr    :=  csr_index_EXE
  csr_mod.io.reg    :=  ro1_EXE
  csr_data_MEM      :=  csr_mod.io.t
  csr_pc_MEM        :=  csr_mod.io.pc_out
  
  //  Forward data
  mux1_EXE := 
    MuxLookup(fsig1_EXE,  ro1_EXE ,
      Array(
            FWD_REG     ->ro1_EXE ,
            FWD_MEM     ->alo_MEM ,
            FWD_WB      ->dat_WB  ,
            FWD_CSR     ->csr_data_MEM 
          ))
  mux2_EXE := 
    MuxLookup(fsig1_EXE,  ro2_EXE ,
      Array(
            FWD_REG     ->ro2_EXE ,
            FWD_MEM     ->alo_MEM ,
            FWD_WB      ->dat_WB  ,
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
  val alu = Module(new ALU())
  alu.io.in_a   :=  ali1_EXE
  alu.io.in_b   :=  ali2_EXE
  alu.io.alu_op :=  alu_op_EXE  
  alo_EXE := alu.io.alu_out

  // Jump Union module
  val junion = Module(new Jump_Union())
  junion.io.b_type  := branch_sig_EXE
  junion.io.in1     := mux1_EXE
  junion.io.in2     := mux2_EXE

  // Ram Module
  mem_data_MEM := io.data_in
  io.mem_write := mem_write_MEM
  io.addr_out := alo_MEM
  io.data_out := mux2_MEM

  // WB state
  reg_data_WB := 
    MuxLookup(mem_to_reg_WB,  alo_WB,
      Array(
            WB_ALU          ->alo_WB,
            WB_PC4          ->(pc_WB+4.U),
            WB_RAM          ->mem_data_WB,
            WB_CSR          ->csr_data_WB
          ))
  
  // Detect Module
  // EXE state: when Jump
  val cflt_data = Wire(Bool()) // R型/B型/ebreak/ecall/csrrs->L型 ID->插1个bubble(PC锁)
  val cflt_ctrl = Wire(Bool()) // B型/J型跳转/mret/ebreak/ecall EX<-插2个bubble(PC不锁)
  cflt_data := (mem_to_reg_EXE === WB_RAM) && (rd_EXE === rs1_ID || rd_EXE === rs2_ID)
  cflt_ctrl := junion.io.pc_src =/= PC_4

  flush_ID := cflt_ctrl || cflt_data
  flush_IF := cflt_ctrl
  // ID state: 
  stall_IF := cflt_data
  stall_PC := cflt_data

  // PC module -2
  when(!stall_PC){
    pc_IF :=
      MuxLookup( junion.io.pc_src,  (pc_EXE + 4.U),
        Array(
              PC_4                ->(pc_EXE + 4.U),
              PC_B                ->alo_EXE,
              PC_J                ->alo_EXE,
              PC_JR               ->((alo_EXE) & (~1.U)),
              PC_CSR              ->csr_pc_MEM
            ))
  }


  // for verilator
  val count_IF  = Wire(UInt(1.W))
  val count_ID  = RegInit(0.U(1.W))
  val count_EXE = RegInit(0.U(1.W))
  val count_MEM = RegInit(0.U(1.W))
  val count_WB  = RegInit(0.U(1.W))
  count_IF  := Mux(cflt_ctrl, 1.U(1.W), 0.U(1.W))
  count_ID  := RegNext(Mux(cflt_data || cflt_ctrl, count_IF, 0.U(1.W)))
  count_EXE := RegNext(count_ID)
  count_MEM := RegNext(count_EXE)
  count_WB  := RegNext(count_MEM)
  BoringUtils.addSource(count_WB, "dt_counter")

}