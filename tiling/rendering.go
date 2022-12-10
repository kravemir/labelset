package tiling

import (
	"fmt"

	"github.com/antchfx/xmlquery"
)

type Renderable interface {
	RenderTo(renderer *DocumentRenderer) error
}

func Render(
	paper TiledPaper,
	labelGroups []Renderable,
	options DocumentRenderOptions,
) ([]*xmlquery.Node, error) {
	renderer := NewDocumentRenderer(
		paper,
		options,
	)

	for groupIndex, group := range labelGroups {
		err := group.RenderTo(renderer)
		if err != nil {
			return nil, fmt.Errorf("render group %d: %w", groupIndex, err)
		}
	}

	return renderer.Pages(), nil
}
