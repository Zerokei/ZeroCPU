package zeroCPU.isa
import chisel3._
import chisel3.util._

object ISA{
  def LUI           = BitPat("b????????????????????_?????_0110111")
  def AUIPC         = BitPat("b????????????????????_?????_0010111")

  def JAL           = BitPat("b????????????????????_?????_1101111")
  def JALR          = BitPat("b????????????_?????_000_?????_1100111")

  def BEQ           = BitPat("b???????_?????_?????_000_?????_1100011")
  def BNE           = BitPat("b???????_?????_?????_001_?????_1100011")
  def BLT           = BitPat("b???????_?????_?????_100_?????_1100011")
  def BGE           = BitPat("b???????_?????_?????_101_?????_1100011")
  def BLTU          = BitPat("b???????_?????_?????_110_?????_1100011")
  def BGEU          = BitPat("b???????_?????_?????_111_?????_1100011")

  def LW            = BitPat("b????????????_?????_010_?????_0000011")
  def SW            = BitPat("b???????_?????_?????_010_?????_0100011")

  def ADDI          = BitPat("b????????????_?????_000_?????_0010011")
  def SLTI          = BitPat("b????????????_?????_010_?????_0010011")
  def SLTIU         = BitPat("b????????????_?????_011_?????_0010011")
  def XORI          = BitPat("b????????????_?????_100_?????_0010011")
  def ORI           = BitPat("b????????????_?????_110_?????_0010011")
  def ANDI          = BitPat("b????????????_?????_111_?????_0010011")
  def SLLI          = BitPat("b000000_??????_?????_001_?????_0010011")
  def SRLI          = BitPat("b000000_??????_?????_101_?????_0010011")
  def SRAI          = BitPat("b010000_??????_?????_101_?????_0010011")

  def ADD           = BitPat("b0000000_?????_?????_000_?????_0110011")
  def SUB           = BitPat("b0100000_?????_?????_000_?????_0110011")
  def SLL           = BitPat("b0000000_?????_?????_001_?????_0110011")
  def SLT           = BitPat("b0000000_?????_?????_010_?????_0110011")
  def SLTU          = BitPat("b0000000_?????_?????_011_?????_0110011")
  def XOR           = BitPat("b0000000_?????_?????_100_?????_0110011")
  def SRL           = BitPat("b0000000_?????_?????_101_?????_0110011")
  def SRA           = BitPat("b0100000_?????_?????_101_?????_0110011")
  def OR            = BitPat("b0000000_?????_?????_110_?????_0110011")
  def AND           = BitPat("b0000000_?????_?????_111_?????_0110011")

  def CSRRS         = BitPat("b????????????_?????_010_?????_1110011")
  def CSRRW         = BitPat("b????????????_?????_001_?????_1110011")

  def ECALL         = BitPat("b000000000000_00000_000_00000_1110011")
  def EBREAK        = BitPat("b000000000001_00000_000_00000_1110011")

  def MRET          = BitPat("b00110000001000000000000001110011")
}