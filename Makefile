TILE := Tile

default: tile

tile:
	sbt "runMain zeroCPU.base.GenTV"
	-@rm $(CURDIR)/build/verilog/base/*.fir
	-@rm $(CURDIR)/build/verilog/base/*.json
debug:
	sbt "runMain zeroCPU.base.GenTVDebug"
	-@rm $(CURDIR)/build/verilog/base/*.fir
	-@rm $(CURDIR)/build/verilog/base/*.json

clean:
	-@rm *.v
	-@rm -rf target