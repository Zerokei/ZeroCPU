package ZeroCPU.wow
import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

/*
parameter   ADD  = 4'b0000,
            SUB  = 4'b1000,
			SLL  = 4'b0001,
			SLT  = 4'b0010,
			SLTU = 4'b0011,
			XOR  = 4'b0100,
			SRL  = 4'b0101,
			SRA  = 4'b1101,
			OR   = 4'b0110,
			AND  = 4'b0111;
*/

trait AluOpType{
  val alu_add = 0.U(5.W)
  val alu_sub = 8.U(5.W)
  val alu_sll = 1.U(5.W)
  val alu_slt = 2.U(5.W)
  val alu_sltu = 3.U(5.W)
  val alu_xor = 4.U(5.W)
  val alu_srl = 5.U(5.W)
  val alu_or = 6.U(5.W)
  val alu_and = 7.U(5.W)
  val alu_sra = 13.U(5.W)
}

class ALU extends Module with AluOpType{
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
    (io.alu_op === alu_add) -> (a + b),
    (io.alu_op === alu_sub) -> (a - b),
    (io.alu_op === alu_and) -> (a & b),
    (io.alu_op === alu_xor) -> (a ^ b),
    (io.alu_op === alu_or) -> (a | b),
    (io.alu_op === alu_slt) -> Mux(a.asSInt() < b.asSInt(), 1.U, 0.U),
    (io.alu_op === alu_sltu) -> Mux(a < b, 1.U, 0.U),
    (io.alu_op === alu_srl) -> (a >> b(4,0)),
    (io.alu_op === alu_sll) -> (a << b(4,0)),
    (io.alu_op === alu_sra) -> ((a.asSInt() >> b(4,0))).asUInt()
  ))

  io.zero := (io.alu_out === 0.U)
}
