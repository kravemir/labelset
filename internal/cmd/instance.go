package cmd

import (
	"encoding/json"
	"fmt"
	"os"
	"path"
	"strings"

	"github.com/spf13/cobra"

	"github.com/kravemir/labelset/template"
)

func instanceCmd() *cobra.Command {
	var flags struct {
		templateDescriptor string

		instanceJson string
	}

	var sourcePath, destinationPath string

	argsFunc := func(cmd *cobra.Command, args []string) error {
		if len(args) != 2 {
			return fmt.Errorf("expected 2 arguments, got %d arguments", len(args))
		}

		sourcePath, destinationPath = args[0], args[1]

		return nil
	}

	loadSource := func() (string, error) {
		sourceBytes, err := os.ReadFile(sourcePath)
		if err != nil {
			return "", fmt.Errorf("read file: %w", err)
		}
		return string(sourceBytes), nil
	}

	loadDescriptor := func() (string, error) {
		templateDescriptor := flags.templateDescriptor
		if templateDescriptor == "" {
			templateDescriptor = strings.TrimSuffix(sourcePath, path.Ext(sourcePath)) + ".labelset.json"
		}

		descriptorBytes, err := os.ReadFile(templateDescriptor)
		if err != nil {
			return "", fmt.Errorf("read file %s: %w", templateDescriptor, err)
		}

		return string(descriptorBytes), nil
	}

	loadInstance := func() (map[string]any, error) {
		if flags.instanceJson == "" {
			return map[string]any{}, nil
		}

		instanceBytes, err := os.ReadFile(flags.instanceJson)
		if err != nil {
			return nil, fmt.Errorf("read file: %w", err)
		}

		result := map[string]any{}
		err = json.Unmarshal(instanceBytes, &result)
		if err != nil {
			return nil, fmt.Errorf("parse JSON: %w", err)
		}
		return result, nil
	}

	cmdFunc := func(cmd *cobra.Command, args []string) error {
		source, err := loadSource()
		if err != nil {
			return fmt.Errorf("load source SVG: %w", err)
		}

		descriptor, err := loadDescriptor()
		if err != nil {
			return fmt.Errorf("load template descriptor: %w", err)
		}

		instance, err := loadInstance()
		if err != nil {
			return fmt.Errorf("load instance: %w", err)
		}

		tmpl, err := template.NewTemplate(source, descriptor)
		if err != nil {
			return fmt.Errorf("create template: %w", err)
		}

		result, err := tmpl.Render(instance)
		if err != nil {
			return fmt.Errorf("generate instance: %w", err)
		}

		err = os.WriteFile(destinationPath, []byte(result.OutputXML(true)), 0755)
		if err != nil {
			return fmt.Errorf("write generated document: %w", err)
		}

		return nil
	}

	cmd := &cobra.Command{
		Use:   "instance [SOURCE] [TARGET]",
		Short: "Fill label template with instance data",

		Args: argsFunc,
		RunE: cmdFunc,
	}

	cmd.Flags().StringVar(&flags.templateDescriptor, "template-descriptor", "", "Path to JSON file containing descriptor of template")

	cmd.Flags().StringVar(&flags.instanceJson, "instance-json", "", "Path to JSON file containing values for single instance")

	return cmd
}
