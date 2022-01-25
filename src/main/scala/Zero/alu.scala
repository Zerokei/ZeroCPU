package zeroCPU.wow
import chisel3._
import chisel3.util._
import zeroCPU.const.ZeroConfig._

class ALU extends Module{
  val io = IO(new Bundle{
    val in_a = Input(UInt(32.W))
    val in_b = Input(UInt(32.W))
    val alu_op = Input(UInt(5.W))
    val alu_out = Output(UInt(32.W))
    val zero = Output(Bool())
  })
  
  val a = io.in_a
  val b = io.in_b
  io.alu_out := MuxCase(0.U, Array(
    (io.alu_op === ALU_ADD) -> (a + b),
    (io.alu_op === ALU_SUB) -> (a - b),
    (io.alu_op === ALU_AND) -> (a & b),
    (io.alu_op === ALU_XOR) -> (a ^ b),
    (io.alu_op === ALU_OR) -> (a | b),
    (io.alu_op === ALU_SLT) -> Mux(a.asSInt() < b.asSInt(), 1.U, 0.U),
    (io.alu_op === ALU_SLTU) -> Mux(a < b, 1.U, 0.U),
    (io.alu_op === ALU_SRL) -> (a >> b(4,0)),
    (io.alu_op === ALU_SLL) -> (a << b(4,0)),
    (io.alu_op === ALU_SRA) -> ((a.asSInt() >> b(4,0))).asUInt(),
    (io.alu_op === ALU_COPY) -> a
  ))

  io.zero := (io.alu_out === 0.U)
}
