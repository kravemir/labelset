package template

import (
	"fmt"
	"strings"
	"text/template"

	"github.com/antchfx/xmlquery"
	"github.com/google/cel-go/cel"
)

type ContentReplaceRule struct {
	Value        string
	ElementXPath string
	If           string
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

	ruleCondition, err := rule.compileConditionCheck()
	if err != nil {
		return nil, fmt.Errorf("compile rule condition: %w", err)
	}

	ruleFunc := func(svgNode *xmlquery.Node, content map[string]any) error {
		shouldSkip, err := ruleCondition(content)
		if err != nil {
			return fmt.Errorf("check condition: %w", err)
		}
		if shouldSkip {
			return nil
		}

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

func (rule ContentReplaceRule) compileConditionCheck() (func(content map[string]any) (bool, error), error) {
	if rule.If == "" {
		return func(content map[string]any) (bool, error) {
			return false, nil
		}, nil
	}

	env, err := cel.NewEnv(
		cel.Variable("Instance", cel.MapType(cel.StringType, cel.AnyType)),
	)
	if err != nil {
		return nil, fmt.Errorf("create cel env")
	}

	ast, issues := env.Compile(rule.If)
	if issues != nil && issues.Err() != nil {
		return nil, fmt.Errorf("compile: %w", issues.Err())
	}

	program, err := env.Program(ast)
	if err != nil {
		return nil, fmt.Errorf("create program: %w", err)
	}

	return func(content map[string]any) (bool, error) {
		evalResult, _, err := program.Eval(map[string]any{
			"Instance": content,
		})
		if err != nil {
			return false, fmt.Errorf("evaluation error: %w", err)
		}

		return evalResult.Value() != true, nil
	}, nil
}
