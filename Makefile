TILE := Tile

default: tile

tile:
	sbt "runMain zeroCPU.base.GenTV"
	-@rm build/verilog/base/*.fir
	-@rm build/verilog/base/*.json
debug:
	sbt "runMain zeroCPU.base.GenTVDebug"
	-@rm build/verilog/base/*.fir
	-@rm build/verilog/base/*.json

clean:
	-@rm *.v
	-@rm -rf target