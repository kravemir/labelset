package template

import (
	"encoding/json"
	"fmt"
	"strings"

	"github.com/antchfx/xmlquery"
)

type Template struct {
	svg string

	descriptor         Descriptor
	compiledDescriptor *compiledDescriptor
}

func NewTemplate(
	svg string,
	descriptor string,
) (*Template, error) {
	var err error

	tmpl := &Template{
		svg: svg,
	}

	if descriptor != "" {
		err := json.Unmarshal([]byte(descriptor), &tmpl.descriptor)
		if err != nil {
			return nil, fmt.Errorf("unmarshal descriptor: %w", err)
		}
	}

	tmpl.compiledDescriptor, err = tmpl.descriptor.compile()
	if err != nil {
		return nil, fmt.Errorf("compile descriptor: %w", err)
	}

	return tmpl, nil
}

func (tmpl *Template) Render(content map[string]any) (*xmlquery.Node, error) {
	svgDocument, err := xmlquery.Parse(strings.NewReader(tmpl.svg))
	if err != nil {
		return nil, fmt.Errorf("parse SVG document: %w", err)
	}

	svgNode := xmlquery.FindOne(svgDocument, "svg")
	if svgNode == nil {
		return nil, fmt.Errorf("not found root <svg /> element")
	}

	for ruleIndex, rule := range tmpl.compiledDescriptor.contentReplaceRules {
		err = rule(svgNode, content)
		if err != nil {
			return nil, fmt.Errorf("apply rule #%d: %w", ruleIndex, err)
		}
	}

	return svgNode, nil
}

func replaceTextContents(node *xmlquery.Node, value string) {
	valueLines := strings.Split(value, "\n")

	spanNodes := xmlquery.Find(node, "tspan")

	for idx, spanNode := range spanNodes {
		lineValue := ""

		if idx < len(valueLines) {
			lineValue = valueLines[idx]
		}

		lineTextNode := &xmlquery.Node{
			Data:   lineValue,
			Type:   xmlquery.TextNode,
			Parent: spanNode,
		}

		spanNode.FirstChild = lineTextNode
		spanNode.LastChild = lineTextNode
	}
}
