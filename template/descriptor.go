package template

import (
	"fmt"
	"strings"
	"text/template"

	"github.com/antchfx/xmlquery"
)

type ContentReplaceRule struct {
	Value        string
	ElementXPath string
}

type Descriptor struct {
	Attributes []struct {
		Key string
	}

	ContentReplaceRules []ContentReplaceRule
}

type compiledDescriptor struct {
	contentReplaceRules []func(svgNode *xmlquery.Node, content map[string]any) error
}

func (d Descriptor) compile() (*compiledDescriptor, error) {
	var c compiledDescriptor

	for _ruleIndex, _rule := range d.ContentReplaceRules {
		ruleFunc, err := _rule.compile()
		if err != nil {
			return nil, fmt.Errorf("compile rule #%d: %w", _ruleIndex, err)
		}

		c.contentReplaceRules = append(c.contentReplaceRules, ruleFunc)
	}

	return &c, nil
}

func (rule ContentReplaceRule) compile() (func(svgNode *xmlquery.Node, content map[string]any) error, error) {
	valueTemplate, err := rule.compileValueTemplate()
	if err != nil {
		return nil, fmt.Errorf("compile value template: %w", err)
	}

	ruleFunc := func(svgNode *xmlquery.Node, content map[string]any) error {
		value, err := valueTemplate(content)
		if err != nil {
			return fmt.Errorf("get value: %w", err)
		}

		nodes, err := xmlquery.QueryAll(svgNode, rule.ElementXPath)
		for _, node := range nodes {
			replaceTextContents(node, value)
		}

		return nil
	}
	return ruleFunc, nil
}

func (rule ContentReplaceRule) compileValueTemplate() (func(content map[string]any) (string, error), error) {
	valueTemplate, err := template.New("root").Parse(rule.Value)
	if err != nil {
		return nil, fmt.Errorf("template parse: %w", err)
	}

	return func(content map[string]any) (string, error) {
		var buf strings.Builder
		err = valueTemplate.Execute(&buf, struct {
			Instance map[string]any
		}{
			Instance: content,
		})
		if err != nil {
			return "", fmt.Errorf("execute value template: %w", err)
		}

		return buf.String(), nil
	}, nil
}
