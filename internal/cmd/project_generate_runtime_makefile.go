package cmd

import (
	"encoding/json"
	"fmt"
	"os"
	"path"

	"github.com/kravemir/labelset/internal/project"

	"github.com/spf13/cobra"
)

func projectGenerateRuntimeMakefile() *cobra.Command {
	var projectPath, runtimeMakefilePath string

	argsFunc := func(cmd *cobra.Command, args []string) error {
		if len(args) != 2 {
			return fmt.Errorf("expected 2 argument, got %d arguments", len(args))
		}

		projectPath, runtimeMakefilePath = args[0], args[1]

		return nil
	}

	cmdFunc := func(cmd *cobra.Command, args []string) error {
		projectSpecs, err := loadProjectSpecs(projectPath)
		if err != nil {
			return err
		}

		makefile, err := project.GenerateRuntimeMakefile(projectSpecs)
		if err != nil {
			return fmt.Errorf("generate runtime makefile")
		}

		err = os.MkdirAll(path.Dir(runtimeMakefilePath), 0755)
		if err != nil {
			return fmt.Errorf("create directory for %s", runtimeMakefilePath)
		}

		err = os.WriteFile(runtimeMakefilePath, []byte(makefile), 0755)
		if err != nil {
			return fmt.Errorf("write Makefile: %w", err)
		}

		return nil
	}

	cmd := &cobra.Command{
		Use:   "generate-runtime-makefile",
		Short: "Generates runtime makefile for project (used by Makefile)",

		Hidden: true,

		Args: argsFunc,
		RunE: cmdFunc,
	}

	return cmd
}

func loadProjectSpecs(projectPath string) (project.Project, error) {
	projectSpecsBytes, err := os.ReadFile(projectPath)
	if err != nil {
		return project.Project{}, fmt.Errorf("read project specs from project.json: %w", err)
	}

	var projectSpecs project.Project
	err = json.Unmarshal(projectSpecsBytes, &projectSpecs)
	if err != nil {
		return project.Project{}, fmt.Errorf("unmarshal project specs: %w", err)
	}
	return projectSpecs, nil
}
