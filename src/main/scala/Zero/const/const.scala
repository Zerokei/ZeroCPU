package zeroCPU.const
import chisel3._
import chisel3.util._
object ZeroConfig{

  val LEN = 32  // Length of instruction
  val DLEN = 32 // Length of data
  val SLEN = 32 // Length of csr register

  val CINUM = 2 // none or one instruction being executed

  val NREGS = 32 // number of registers
  val NREGS_BIT = log2Ceil(NREGS)

  val NOP = "h00000013".U(LEN.W)
  
  // Cache auto mechine
  val WAIT_SIG_LEN   = 2
  val WAIT_NOTHING    = 0.U(WAIT_SIG_LEN.W)
  val WAIT_WRITE_OLD = 1.U(WAIT_SIG_LEN.W)
  val WAIT_READ_NEW  = 2.U(WAIT_SIG_LEN.W)

  
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
  val BR_SIG_LEN = 4
  val BR_N   = 0.U(BR_SIG_LEN.W)  // Next
  val BR_NE  = 1.U(BR_SIG_LEN.W)  // Branch on NotEqual
  val BR_EQ  = 2.U(BR_SIG_LEN.W)  // Branch on Equal
  val BR_GE  = 3.U(BR_SIG_LEN.W)  // Branch on Greater/Equal
  val BR_GEU = 4.U(BR_SIG_LEN.W)  // Branch on Greater/Equal Unsigned
  val BR_LT  = 5.U(BR_SIG_LEN.W)  // Branch on Less Than
  val BR_LTU = 6.U(BR_SIG_LEN.W)  // Branch on Less Than Unsigned
  val BR_J   = 7.U(BR_SIG_LEN.W)  // Jump
  val BR_JR  = 8.U(BR_SIG_LEN.W)  // Jump Register
  val BR_BK  = 9.U(BR_SIG_LEN.W)  // Ebreak
  val BR_CA  = 10.U(BR_SIG_LEN.W)  // ecall
  val BR_RT  = 11.U(BR_SIG_LEN.W)  // mret
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

  // Mem operations
  val MEM_SIG_LEN = 2
  val MEM_X   = 0.U(MEM_SIG_LEN.W)  // default
  val MEM_WRT = 1.U(MEM_SIG_LEN.W)  // memory write
  val MEM_RED = 2.U(MEM_SIG_LEN.W)  // memory read

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
  val CSW_SIG_LEN = 3
  val CSW_RED = 1.U(CSW_SIG_LEN.W)  // write to CSR csrrs
  val CSW_WRT = 2.U(CSW_SIG_LEN.W)  // write to CSR csrrs
  val CSW_BRK = 3.U(CSW_SIG_LEN.W)  // write to CSR ebreak
  val CSW_CAL = 4.U(CSW_SIG_LEN.W)  // write to CSR ecall
  val CSW_X   = 0.U(CSW_SIG_LEN.W)  // default

  // Type
  val TYPE_LEN = 3
  val R_TYPE = 0.U(TYPE_LEN.W)
  val I_TYPE = 1.U(TYPE_LEN.W)
  val S_TYPE = 2.U(TYPE_LEN.W)
  val U_TYPE = 3.U(TYPE_LEN.W)
  val B_TYPE = 4.U(TYPE_LEN.W)
  val J_TYPE = 5.U(TYPE_LEN.W)
  val X_TYPE = 0.U(TYPE_LEN.W)

  // Forwarding Part
  val FWD_SIG_LEN = 2
  val FWD_REG = 0.U(FWD_SIG_LEN.W)  // normal
  val FWD_MEM = 1.U(FWD_SIG_LEN.W)  // forwarding alu data from mem state
  val FWD_WB  = 2.U(FWD_SIG_LEN.W)  // forwarding data from wb state
  val FWD_CSR = 3.U(FWD_SIG_LEN.W)  // forwarding csr data from mem state(while csr data from mem is included in 2.U)
  val FWD_X   = 0.U(FWD_SIG_LEN.W)  // default

  val CSRS_SIZE    = 12  
  val CSR_MSTATUS = "h300".U(CSRS_SIZE.W)
  val CSR_MTVEC   = "h305".U(CSRS_SIZE.W)
  val CSR_MEPC    = "h341".U(CSRS_SIZE.W)
  val CSR_MCAUSE  = "h342".U(CSRS_SIZE.W)
  val CSR_F       = "hC00".U(CSRS_SIZE.W) // undefined instruction
  val CSR_X       = "h000".U(CSRS_SIZE.W)
}