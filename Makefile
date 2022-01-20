TILE := Tile

default: tile

tile:
	sbt "runMain ZeroCPU.base.GenTV"
	-@rm *.fir
	-@rm *.json
clean:
	-@rm *.v
	-@rm -rf target