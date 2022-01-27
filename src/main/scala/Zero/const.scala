package zeroCPU.const
import chisel3._
import chisel3.util._
object ZeroConfig{

  // AluOp
  val ALU_SIG_LEN = 5
  val ALU_ADD   = 0.U(ALU_SIG_LEN.W)
  val ALU_SUB   = 1.U(ALU_SIG_LEN.W)
  val ALU_SLL   = 2.U(ALU_SIG_LEN.W)
  val ALU_SLT   = 3.U(ALU_SIG_LEN.W)
  val ALU_SLTU  = 4.U(ALU_SIG_LEN.W)
  val ALU_XOR   = 5.U(ALU_SIG_LEN.W)
  val ALU_SRL   = 6.U(ALU_SIG_LEN.W)
  val ALU_OR    = 7.U(ALU_SIG_LEN.W)
  val ALU_AND   = 8.U(ALU_SIG_LEN.W)
  val ALU_SRA   = 9.U(ALU_SIG_LEN.W) 
  val ALU_COPY  = 10.U(ALU_SIG_LEN.W) // copy
  val ALU_X     = 0.U(ALU_SIG_LEN.W)  // default

  // PcSrc
  val PC_SIG_LEN = 2
  val PC_4    = 0.U(PC_SIG_LEN.W) // pc+4
  val PC_B    = 1.U(PC_SIG_LEN.W) // alu result, pc+sext(offset): B type
  val PC_J    = 1.U(PC_SIG_LEN.W) // alu result, pc+sext(offset): jal
  val PC_JR   = 2.U(PC_SIG_LEN.W) // (alu result)&(~1), (x[rs1]+sext(offset))&(~1): jalr
  val PC_CSR  = 3.U(PC_SIG_LEN.W) // csr output: ecall ebreak mret
  val PC_X    = 0.U(PC_SIG_LEN.W) // default

  // Branch type
  val BR_SIG_LEN = 3
  val BR_N   = 0.U(BR_SIG_LEN.W)  // Next
  val BR_NE  = 1.U(BR_SIG_LEN.W)  // Branch on NotEqual
  val BR_EQ  = 2.U(BR_SIG_LEN.W)  // Branch on Equal
  val BR_GE  = 3.U(BR_SIG_LEN.W)  // Branch on Greater/Equal
  val BR_GEU = 4.U(BR_SIG_LEN.W)  // Branch on Greater/Equal Unsigned
  val BR_LT  = 5.U(BR_SIG_LEN.W)  // Branch on Less Than
  val BR_LTU = 6.U(BR_SIG_LEN.W)  // Branch on Less Than Unsigned
  val BR_J   = 7.U(BR_SIG_LEN.W)  // Jump
  val BR_JR  = 7.U(BR_SIG_LEN.W)  // Jump Register
  val BR_BK  = 7.U(BR_SIG_LEN.W)  // Ebreak
  val BR_CA  = 7.U(BR_SIG_LEN.W)  // ecall
  val BR_RT  = 7.U(BR_SIG_LEN.W)  // mret
  val BR_X   = 0.U(BR_SIG_LEN.W)  // default

  // MemToReg
  val WB_SIG_LEN = 2
  val WB_ALU = 0.U(WB_SIG_LEN.W)  // alu result
  val WB_RAM = 1.U(WB_SIG_LEN.W)  // ram
  val WB_PC4 = 2.U(WB_SIG_LEN.W)  // pc+4
  val WB_CSR = 3.U(WB_SIG_LEN.W)  // csr output
  val WB_X   = 0.U(WB_SIG_LEN.W)  // default

  // SigRs1
  val OP1_SIG_LEN = 1
  val OP1_RS1 = 0.U(OP1_SIG_LEN.W)  // register source
  val OP1_IMM = 1.U(OP1_SIG_LEN.W)  // imm source
  val OP1_X   = 0.U(OP1_SIG_LEN.W)  // default

  // SigRs2
  val OP2_SIG_LEN = 2
  val OP2_RS2 = 0.U(OP2_SIG_LEN.W)  // register source
  val OP2_IMM = 1.U(OP2_SIG_LEN.W)  // imm source
  val OP2_PC  = 2.U(OP2_SIG_LEN.W)  // pc
  val OP2_X   = 0.U(OP2_SIG_LEN.W)  // default

  // RegWrite
  val REN_Y = true.B // enable write to register
  val REN_X = false.B // default

  // MemWrite
  val MW_Y = true.B  // memory write
  val MW_X = false.B  // default

  // PcLock
  val PCL_Y = true.B // lock pc
  val PCL_X = false.B // default

  // Forwarding Rs
  val FR_SIG_LEN = 2
  val FR_REG = 0.U(FR_SIG_LEN.W)  // register output
  val FR_ALU = 1.U(FR_SIG_LEN.W)  // alu result in MEM
  val FR_WB  = 2.U(FR_SIG_LEN.W)  // data in WB
  val FR_CSR = 3.U(FR_SIG_LEN.W)  // csr output in MEM
  val FR_X   = 0.U(FR_SIG_LEN.W)  // default

  // CSRWrite
  val CSW_SIG_LEN = 2
  val CSW_REG = 1.U(CSW_SIG_LEN.W)  // write to CSR csrrs
  val CSW_BRK = 2.U(CSW_SIG_LEN.W)  // write to CSR ebreak
  val CSW_CAL = 3.U(CSW_SIG_LEN.W)  // write to CSR ecall
  val CSW_X   = 0.U(CSW_SIG_LEN.W)  // default
}