package zeroCPU.wow
import chisel3._
import chisel3.util._
import zeroCPU.isa.ISA._
import zeroCPU.const.ZeroConfig._

class Decoder extends Module{
  val io = IO(new Bundle{
    val inst = Input(UInt(32.W))

    //signal
    val pc_src      = Output(UInt(PC_SIG_LEN.W))
    val branch_sig  = Output(UInt(BR_SIG_LEN.W))
    val mem_to_reg  = Output(UInt(WB_SIG_LEN.W))
    val reg_write   = Output(Bool())
    val op_src1     = Output(UInt(OP1_SIG_LEN.W))
    val op_src2     = Output(UInt(OP2_SIG_LEN.W))
    val alu_src     = Output(UInt(ALU_SIG_LEN.W))
    val mem_write   = Output(Bool())
    val csr_write   = Output(UInt(CSW_SIG_LEN.W))
  })
  val signals =
    ListLookup(io.inst,
                        List( PC_X  , BR_X  , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_X   ),
          Array(        /*    PcSrc | Branch| Mem2Reg | RegW  | SigRs1  | SigRs2  | AluOp   | MemW  | CSRWrite*/
              LW      ->List( PC_X  , BR_X  , WB_RAM  , REN_Y , OP1_RS1 , OP2_IMM , ALU_ADD , MW_X  , CSW_X   ),          
              SW      ->List( PC_X  , BR_X  , WB_X    , REN_X , OP1_RS1 , OP2_IMM , ALU_ADD , MW_Y  , CSW_X   ),
              
              AUIPC   ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_IMM , OP2_PC  , ALU_ADD , MW_X  , CSW_X   ),
              LUI     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_IMM , OP2_X   , ALU_COPY, MW_X  , CSW_X   ),

              ADDI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_ADD , MW_X  , CSW_X   ),
              ANDI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_AND , MW_X  , CSW_X   ),
              ORI     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_OR  , MW_X  , CSW_X   ),
              XORI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_XOR , MW_X  , CSW_X   ),
              SLTI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SLT , MW_X  , CSW_X   ),
              SLTIU   ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SLTU, MW_X  , CSW_X   ),
              SLLI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SLL , MW_X  , CSW_X   ),
              SRAI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SRA , MW_X  , CSW_X   ),
              SRLI    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_IMM , ALU_SRL , MW_X  , CSW_X   ),

              ADD     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_ADD , MW_X  , CSW_X   ),
              SUB     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SUB , MW_X  , CSW_X   ),
              AND     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_AND , MW_X  , CSW_X   ),
              OR      ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_OR  , MW_X  , CSW_X   ),
              XOR     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_XOR , MW_X  , CSW_X   ),
              SLT     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SLT , MW_X  , CSW_X   ),
              SLTU    ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SLTU, MW_X  , CSW_X   ),
              SLL     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SLL , MW_X  , CSW_X   ),
              SRA     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SRA , MW_X  , CSW_X   ),
              SRL     ->List( PC_X  , BR_X  , WB_ALU  , REN_Y , OP1_RS1 , OP2_RS2 , ALU_SRL , MW_X  , CSW_X   ),

              JAL     ->List( PC_J  , BR_J  , WB_ALU  , REN_Y , OP1_IMM , OP2_PC  , ALU_ADD , MW_X  , CSW_X   ),
              JALR    ->List( PC_JR , BR_JR , WB_PC4  , REN_Y , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_X   ),
              BEQ     ->List( PC_B  , BR_EQ , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_X   ),
              BNE     ->List( PC_B  , BR_NE , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_X   ),
              BGE     ->List( PC_B  , BR_GE , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_X   ),
              BGEU    ->List( PC_B  , BR_GEU, WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_X   ),
              BLT     ->List( PC_B  , BR_LT , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_X   ),
              BLTU    ->List( PC_B  , BR_LTU, WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_X   ),
    
              CSRRS   ->List( PC_X  , BR_X  , WB_CSR  , REN_Y , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_REG ),

              MRET    ->List( PC_CSR, BR_X  , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_X   ),

              EBREAK  ->List( PC_CSR, BR_X  , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_BRK ),
              ECALL   ->List( PC_CSR, BR_X  , WB_X    , REN_X , OP1_X   , OP2_X   , ALU_X   , MW_X  , CSW_CAL )
            ))
  // val io.pc_src :: io.branch_sig :: io.mem_to_reg :: io.reg_write :: io.op_src1 :: io.op_src2 :: io.alu_src :: io.mem_write :: io.csr_write :: Nil = signals
  val pc_src  :: branch_sig :: mem_to_reg ::  (reg_write: Bool) :: op_src1:: op_src2 ::  alu_src :: (mem_write: Bool) :: csr_write :: Nil = signals
  io.pc_src := pc_src
  io.branch_sig := branch_sig
  io.mem_to_reg := mem_to_reg
  io.reg_write := reg_write
  io.op_src1 := op_src1
  io.op_src2 := op_src2
  io.alu_src := alu_src
  io.mem_write := mem_write
  io.csr_write := csr_write
}