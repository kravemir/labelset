package tiling

type TiledPaper struct {
	Width, Height float64

	TileOffset  Vector64
	TileSize    Size64
	TileSpacing Vector64
}

func (p TiledPaper) createPositionGenerator() *positionGenerator {
	return &positionGenerator{
		paper: p,
	}
}

type positionGenerator struct {
	paper TiledPaper

	position Vector64
	valid    bool
}

func (generator *positionGenerator) start() {
	generator.position = generator.paper.TileOffset
	generator.valid = true
}

func (generator *positionGenerator) advance() {
	if !generator.valid {
		return
	}

	nextX := generator.position.X + generator.paper.TileSize.Width + generator.paper.TileSpacing.X
	nextY := generator.position.Y

	if nextX > generator.paper.Width-generator.paper.TileSize.Width {
		nextX = generator.paper.TileOffset.X
		nextY = generator.position.Y + generator.paper.TileSize.Height + generator.paper.TileSpacing.Y
	}

	if nextY > generator.paper.Height-generator.paper.TileSize.Height {
		generator.valid = false
	}

	generator.position.X = nextX
	generator.position.Y = nextY
}

func (generator *positionGenerator) CurrentPosition() Vector64 {
	return generator.position
}

func (generator *positionGenerator) IsCurrentPositionValid() bool {
	return generator.valid
}
