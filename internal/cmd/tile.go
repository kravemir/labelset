package cmd

import (
	"encoding/json"
	"errors"
	"fmt"
	"io/fs"
	"os"
	"path"
	"strings"

	"github.com/kravemir/labelset/template"
	"github.com/kravemir/labelset/tiling"

	"github.com/spf13/cobra"
)

func tileCmd() *cobra.Command {
	var flags struct {
		templateDescriptor string

		instanceJson string

		paperSize    Size64Flag
		labelSize    Size64Flag
		labelOffset  Vector64Flag
		labelSpacing Vector64Flag
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
		if flags.templateDescriptor != "" {
			descriptorBytes, err := os.ReadFile(flags.templateDescriptor)
			if err != nil {
				return "", fmt.Errorf("read descriptor file %s: %w", flags.templateDescriptor, err)
			}

			return string(descriptorBytes), nil
		} else {
			defaultDescriptorPath := strings.TrimSuffix(sourcePath, path.Ext(sourcePath)) + ".labelset.json"

			descriptorBytes, err := os.ReadFile(defaultDescriptorPath)
			if err != nil {
				if errors.Is(err, fs.ErrNotExist) {
					if flags.instanceJson != "" {
						fmt.Printf("WARNING: descriptor doesn't exist on %s and instance content is set", defaultDescriptorPath)
					}
					return "", nil
				}
				return "", fmt.Errorf("read default descriptor file %s: %w", defaultDescriptorPath, err)
			}

			return string(descriptorBytes), nil
		}
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
		paper := tiling.TiledPaper{
			Width:       flags.paperSize.Width,
			Height:      flags.paperSize.Height,
			TileOffset:  flags.labelOffset.Vector64,
			TileSize:    flags.labelSize.Size64,
			TileSpacing: flags.labelSpacing.Vector64,
		}

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

		var groups = []tiling.Renderable{
			tiling.TemplateInstancesGroup{
				Template: tmpl,
				Instances: []tiling.TemplateInstancesGroupInstance{
					{
						FillPage: true,
						Content:  instance,
					},
				},
			},
		}

		var options tiling.DocumentRenderOptions

		result, err := tiling.Render(
			paper,
			groups,
			options,
		)
		if err != nil {
			return fmt.Errorf("generate document: %w", err)
		}

		err = os.MkdirAll(path.Dir(destinationPath), 0755)
		if err != nil {
			return fmt.Errorf("mkdirs for destination path: %w", err)
		}

		err = os.WriteFile(destinationPath, []byte(result[0].OutputXML(true)), 0755)
		if err != nil {
			return fmt.Errorf("write generated document: %w", err)
		}

		return nil
	}

	cmd := &cobra.Command{
		Use:   "tile [flags] source target",
		Short: "Generated documents with tiled labels",

		Args: argsFunc,
		RunE: cmdFunc,
	}

	cmd.Flags().StringVar(&flags.templateDescriptor, "template-descriptor", "", "Path to JSON file containing descriptor of template")

	cmd.Flags().StringVar(&flags.instanceJson, "instance-json", "", "Path to JSON file containing values for single instance")

	cmd.Flags().Var(&flags.paperSize, "paper-size", "Width and height of the paper in mm, ie. \"210x297\" for A4 paper portrait")
	cmd.Flags().Var(&flags.labelSize, "label-size", "Width and height of label in mm, ie. \"5x5\"")
	cmd.Flags().Var(&flags.labelOffset, "label-offset", "X and Y offset of the first label in mm, ie. 5 5")
	cmd.Flags().Var(&flags.labelSpacing, "label-spacing", "X and Y spacing between labels in mm, ie. 5 5")

	return cmd
}
