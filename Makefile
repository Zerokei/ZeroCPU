TILE := Tile

default: tile

tile:
	sbt "runMain ZeroCPU.base.GenTV"