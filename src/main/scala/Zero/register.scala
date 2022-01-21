package ZeroCPU.wow
import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class Register extends Module{
    val io = IO(new Bundle{
        val rs = Input(UInt(5.W))
        val rd = Input(UInt(5.W))
        val reg_write = Input(Bool())
        val data_in = Input(UInt(32.W))
        val data_out = Output(UInt(32.W))
    })
    // how to use List

    val v = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

    when(io.reg_write){
        v(io.rd) := io.data_in
    }
    io.data_out := v(io.rs)
}