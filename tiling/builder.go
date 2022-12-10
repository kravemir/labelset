package tiling

import (
	"encoding/xml"
	"fmt"
	"regexp"
	"strconv"
	"strings"

	"github.com/antchfx/xmlquery"
)

const SvgNamespaceURI = "http://www.w3.org/2000/svg"

var UnitSuffixRegex = regexp.MustCompile("[^.0-9]*$")

type DocumentRenderer struct {
	paper   TiledPaper
	options DocumentRenderOptions

	positionGenerator *positionGenerator

	currentPage *xmlquery.Node
	pages       []*xmlquery.Node
}

func NewDocumentRenderer(
	paper TiledPaper,
	options DocumentRenderOptions,
) *DocumentRenderer {
	return &DocumentRenderer{
		paper:   paper,
		options: options,

		positionGenerator: paper.createPositionGenerator(),
	}
}

func (builder *DocumentRenderer) PlaceLabel(node *xmlquery.Node) {
	if !builder.positionGenerator.IsCurrentPositionValid() {
		builder.StartNewPage()
	}

	builder.placeClone(node)
}

func (builder *DocumentRenderer) placeClone(node *xmlquery.Node) {
	position := builder.positionGenerator.CurrentPosition()
	builder.positionGenerator.advance()

	clonedNode := *node
	clonedNode.Attr = append(
		[]xmlquery.Attr{},
		clonedNode.Attr...,
	)

	labelWidth := getLengthAttrInMillimeters(clonedNode, "width")
	labelHeight := getLengthAttrInMillimeters(clonedNode, "height")

	setAttr(&clonedNode, xml.Name{Local: "x"}, lengthString(position.X+(builder.paper.TileSize.Width-labelWidth)/2))
	setAttr(&clonedNode, xml.Name{Local: "y"}, lengthString(position.Y+(builder.paper.TileSize.Height-labelHeight)/2))
	//setAttr(&clonedNode, xml.Name{Local: "width"}, lengthString(builder.paper.TileSize.Width))
	//setAttr(&clonedNode, xml.Name{Local: "height"}, lengthString(builder.paper.TileSize.Height))

	xmlquery.AddChild(builder.currentPage.FirstChild, &clonedNode)
}

func (builder *DocumentRenderer) StartNewPage() {
	doc := &xmlquery.Node{
		Type: xmlquery.DeclarationNode,
		Data: "xml",
		Attr: []xmlquery.Attr{
			{Name: xml.Name{Local: "version"}, Value: "1.0"},
		},
	}
	root := &xmlquery.Node{
		Data: "svg",

		Type: xmlquery.ElementNode,
		Attr: []xmlquery.Attr{
			{Name: xml.Name{Local: "xmlns"}, Value: SvgNamespaceURI},
			{Name: xml.Name{Local: "width"}, Value: lengthString(builder.paper.Width)},
			{Name: xml.Name{Local: "height"}, Value: lengthString(builder.paper.Height)},
		},
	}
	doc.FirstChild = root

	builder.currentPage = doc
	builder.pages = append(builder.pages, doc)

	builder.positionGenerator.start()
}

func (builder *DocumentRenderer) Pages() []*xmlquery.Node {
	return builder.pages
}

func (builder *DocumentRenderer) IsSpaceLeftOnCurrentPage() bool {
	return builder.positionGenerator.IsCurrentPositionValid()
}

func lengthString(value float64) string {
	return fmt.Sprintf("%.3fmm", value)
}

func getLengthAttrInMillimeters(node xmlquery.Node, name string) float64 {
	for _, attr := range node.Attr {
		if attr.Name.Local == name {
			unit := UnitSuffixRegex.FindString(attr.Value)
			value, err := strconv.ParseFloat(strings.TrimSuffix(attr.Value, unit), 64)
			if err != nil {
				panic(fmt.Errorf("parse %s value for length: %w", attr.Value, err))
			}
			switch unit {
			case "mm":
				return value
			case "cm":
				return value * 10
			}
		}
	}

	return 0
}

func setAttr(node *xmlquery.Node, name xml.Name, value string) {
	newAttr := xmlquery.Attr{
		Name:  name,
		Value: value,
	}

	for idx, attr := range node.Attr {
		if attr.Name == name {
			node.Attr[idx] = newAttr

			return
		}
	}

	node.Attr = append(node.Attr, newAttr)
}
