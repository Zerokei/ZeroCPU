package zeroCPU.wow
import chisel3._
import chisel3.util._
import zeroCPU.isa.ISA._
import chisel3.util.experimental.BoringUtils
import zeroCPU.const.ZeroConfig._

class Decoder(verilator: Boolean = false) extends Module{
  val io = IO(new Bundle{
    val inst = Input(UInt(LEN.W))

    // signals
    val pc_src      = Output(UInt(PC_SIG_LEN.W))
    val branch_sig  = Output(UInt(BR_SIG_LEN.W))
    val mem_to_reg  = Output(UInt(WB_SIG_LEN.W))
    val reg_write   = Output(Bool())
    val op_src1     = Output(UInt(OP1_SIG_LEN.W))
    val op_src2     = Output(UInt(OP2_SIG_LEN.W))
    val alu_op      = Output(UInt(ALU_SIG_LEN.W))
    val mem_op      = Output(Bool())
    val csr_write   = Output(UInt(CSW_SIG_LEN.W))
    // imm
    val imm         = Output(UInt(DLEN.W))
    // register
    val rs1         = Output(UInt(NREGS_BIT.W))
    val rs2         = Output(UInt(NREGS_BIT.W))
    val rd          = Output(UInt(NREGS_BIT.W))
    val csr_index   = Output(UInt(CSRS_SIZE.W))
  })
  val inst = io.inst
  val signals =
    ListLookup(inst,
                        List( PC_X  , BR_X  , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MEM_X  , CSW_X   , X_TYPE),
          Array(        /*    PcSrc | Branch| Mem2Reg | RegW  | SigRs1  | SigRs2  | AluOp   | MemW  | CSRWrite| Type*/
              LW      ->List( PC_X  , BR_X  , WB_RAM  , REN_Y , OP1_RS1 , OP2_IMM , ALU_ADD , MEM_RED, CSW_X   , I_TYPE),          
              SW      ->List( PC_X  , BR_X  , WB_X    , REN_X , OP1_RS1 , OP2_IMM , ALU_ADD , MEM_WRT, CSW_X   , S_TYPE),
              
              AUIPC   ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_IMM , OP2_PC  , ALU_ADD , MEM_X  , CSW_X   , U_TYPE),
              LUI     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_IMM , OP2_X   , ALU_COPY, MEM_X  , CSW_X   , U_TYPE),

              ADDI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_ADD , MEM_X  , CSW_X   , I_TYPE),
              ANDI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_AND , MEM_X  , CSW_X   , I_TYPE),
              ORI     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_OR  , MEM_X  , CSW_X   , I_TYPE),
              XORI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_XOR , MEM_X  , CSW_X   , I_TYPE),
              SLTI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SLT , MEM_X  , CSW_X   , I_TYPE),
              SLTIU   ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SLTU, MEM_X  , CSW_X   , I_TYPE),
              SLLI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SLL , MEM_X  , CSW_X   , I_TYPE),
              SRAI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SRA , MEM_X  , CSW_X   , I_TYPE),
              SRLI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SRL , MEM_X  , CSW_X   , I_TYPE),

              ADD     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_ADD , MEM_X  , CSW_X   , R_TYPE),
              SUB     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SUB , MEM_X  , CSW_X   , R_TYPE),
              AND     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_AND , MEM_X  , CSW_X   , R_TYPE),
              OR      ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_OR  , MEM_X  , CSW_X   , R_TYPE),
              XOR     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_XOR , MEM_X  , CSW_X   , R_TYPE),
              SLT     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SLT , MEM_X  , CSW_X   , R_TYPE),
              SLTU    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SLTU, MEM_X  , CSW_X   , R_TYPE),
              SLL     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SLL , MEM_X  , CSW_X   , R_TYPE),
              SRA     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SRA , MEM_X  , CSW_X   , R_TYPE),
              SRL     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SRL , MEM_X  , CSW_X   , R_TYPE),

              JAL     ->List( PC_J  , BR_J  , WB_PC4  , REN_Y , OP1_IMM , OP2_PC  , ALU_ADD , MEM_X  , CSW_X   , J_TYPE),
              JALR    ->List( PC_JR , BR_JR , WB_PC4  , REN_Y , OP1_X   , OP2_IMM , ALU_X   , MEM_X  , CSW_X   , I_TYPE),
              BEQ     ->List( PC_B  , BR_EQ , WB_X    , REN_X , OP1_IMM , OP2_PC  , ALU_X   , MEM_X  , CSW_X   , B_TYPE),
              BNE     ->List( PC_B  , BR_NE , WB_X    , REN_X , OP1_IMM , OP2_PC  , ALU_X   , MEM_X  , CSW_X   , B_TYPE),
              BGE     ->List( PC_B  , BR_GE , WB_X    , REN_X , OP1_IMM , OP2_PC  , ALU_X   , MEM_X  , CSW_X   , B_TYPE),
              BGEU    ->List( PC_B  , BR_GEU, WB_X    , REN_X , OP1_IMM , OP2_PC  , ALU_X   , MEM_X  , CSW_X   , B_TYPE),
              BLT     ->List( PC_B  , BR_LT , WB_X    , REN_X , OP1_IMM , OP2_PC  , ALU_X   , MEM_X  , CSW_X   , B_TYPE),
              BLTU    ->List( PC_B  , BR_LTU, WB_X    , REN_X , OP1_IMM , OP2_PC  , ALU_X   , MEM_X  , CSW_X   , B_TYPE),
    
              CSRRS   ->List( PC_X  , BR_X  , WB_CSR, REN_Y , OP1_X   , OP2_X   , ALU_X   , MEM_X  , CSW_RED , I_TYPE),
              CSRRW   ->List( PC_X  , BR_X  , WB_X  , REN_X , OP1_X   , OP2_X   , ALU_X   , MEM_X  , CSW_WRT , I_TYPE),

              MRET    ->List( PC_CSR, BR_RT , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MEM_X  , CSW_X   , I_TYPE),

              EBREAK  ->List( PC_CSR, BR_BK , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MEM_X  , CSW_BRK , I_TYPE),
              ECALL   ->List( PC_CSR, BR_CA , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MEM_X  , CSW_CAL , I_TYPE)
            ))

  val pc_src :: branch_sig :: mem_to_reg :: (reg_write: Bool) :: op_src1 :: op_src2 ::  alu_op :: mem_op :: csr_write :: mytype :: Nil = signals

  io.pc_src := pc_src
  io.branch_sig := branch_sig
  io.mem_to_reg := mem_to_reg
  io.reg_write  := reg_write
  io.op_src1    := op_src1
  io.op_src2    := op_src2
  io.alu_op     := alu_op
  io.mem_op     := mem_op
  io.csr_write  := csr_write

  val imm_i = Cat(Fill(DLEN-12, inst(31)), inst(31,20)) // i-type
  val imm_s = Cat(Fill(DLEN-12, inst(31)), inst(31,25), inst(11,7)) // s-type
  val imm_u = Cat(inst(31,12), Fill(DLEN-20,0.U))
  val imm_b = Cat(Fill(DLEN-12, inst(31)), inst(7), inst(30,25), inst(11,8), 0.U(1.W))
  val imm_j = Cat(Fill(DLEN-20, inst(31)), inst(19,12), inst(20), inst(30,21), 0.U(1.W))
  
  io.imm := MuxCase(0.U(DLEN.W), Array(
                (mytype === R_TYPE) -> 0.U(DLEN.W),
                (mytype === I_TYPE) -> imm_i,
                (mytype === S_TYPE) -> imm_s,
                (mytype === U_TYPE) -> imm_u,
                (mytype === J_TYPE) -> imm_j,
                (mytype === B_TYPE) -> imm_b
                ))

  val rs = 
    MuxLookup(mytype,   Cat(0.U(NREGS_BIT.W) , 0.U(NREGS_BIT.W),  0.U(NREGS_BIT.W)), 
          Array(           /*rs1             | rs2             |  rd                */
              R_TYPE  ->Cat(inst(19,15)      , inst(24,20)     ,  inst(11,7)      ),
              I_TYPE  ->Cat(inst(19,15)      , 0.U(NREGS_BIT.W),  inst(11,7)      ),
              S_TYPE  ->Cat(inst(19,15)      , inst(24,20)     ,  0.U(NREGS_BIT.W)),
              U_TYPE  ->Cat(0.U(NREGS_BIT.W) , 0.U(NREGS_BIT.W),  inst(11,7)      ),
              J_TYPE  ->Cat(0.U(NREGS_BIT.W) , 0.U(NREGS_BIT.W),  inst(11,7)      ),
              B_TYPE  ->Cat(inst(19,15)      , inst(24,20)     ,  0.U(NREGS_BIT.W))
              ))
  io.rs1 := rs(NREGS_BIT*3-1, NREGS_BIT*2)
  io.rs2 := rs(NREGS_BIT*2-1, NREGS_BIT)
  io.rd  := rs(NREGS_BIT-1  , 0)


  io.csr_index := inst(31,20)
}