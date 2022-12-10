package tiling

import (
	"testing"

	"github.com/antchfx/xmlquery"
	"gotest.tools/v3/assert"

	"github.com/kravemir/labelset/internal/assets"
	"github.com/kravemir/labelset/template"
)

func TestRender(t *testing.T) {
	t.Run("Should fill full page", func(t *testing.T) {
		tmpl, err := template.NewTemplate(assets.Template01, assets.Template01Descriptor)
		assert.NilError(t, err)

		pages, err := Render(createPage(2, 3), []Renderable{
			TemplateInstancesGroup{
				Template: tmpl,
				Instances: []TemplateInstancesGroupInstance{
					{
						FillPage: true,
						Content:  assets.Instance01,
					},
				},
			},
		}, DocumentRenderOptions{})

		assert.NilError(t, err)
		assert.Equal(t, len(pages), 1)

		tiles := xmlquery.Find(pages[0], "/*/*")
		assert.Equal(t, 6, len(tiles))

		for _, tile := range tiles {
			assert.Equal(t, 1, len(xmlquery.Find(tile, ".//*[@id='nameText']/*[1][text()='JUnit test']")))
			assert.Equal(t, 1, len(xmlquery.Find(tile, ".//*[@id='nameText']/*[2][text()='']")))
			assert.Equal(t, 2, len(xmlquery.Find(tile, ".//*[@id='nameText']/*")))
			assert.Equal(t, 1, len(xmlquery.Find(tile, ".//*[@id='text4540']/*[text()='Test replacement of texts']")))
			assert.Equal(t, 1, len(xmlquery.Find(tile, ".//*[@id='text4544']/*[text()='13. 05. 2017']")))
		}
	})
}

func createPage(rows, columns int) TiledPaper {
	tileWidth, tileHeight := 60.0, 40.0

	return TiledPaper{
		Width:       20.0 + tileWidth*float64(rows),
		Height:      20.0 + tileHeight*float64(columns),
		TileOffset:  Vector64{X: 10, Y: 10},
		TileSize:    Size64{Width: tileWidth, Height: tileHeight},
		TileSpacing: Vector64{X: 0, Y: 0},
	}
}
