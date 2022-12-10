package tiling

import "github.com/antchfx/xmlquery"

type Vector64 struct {
	X, Y float64
}

type Size64 struct {
	Width, Height float64
}

type Template interface {
	Render(content map[string]any) (*xmlquery.Node, error)
}

type DocumentRenderOptions struct {
	// TODO: rework in hooks, can be customized (i.e. colored or custom border renderer)

	RenderPageBorders  bool
	RenderTileBorders  bool
	RenderLabelBorders bool
}
