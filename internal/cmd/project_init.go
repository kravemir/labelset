package cmd

import (
	"fmt"
	"os"

	"github.com/kravemir/labelset/internal/project"

	"github.com/spf13/cobra"
)

func projectInitCmd() *cobra.Command {

	cmdFunc := func(cmd *cobra.Command, args []string) error {
		makefile := project.GenerateMainMakefile("project.json")

		err := os.WriteFile("Makefile", []byte(makefile), 0755)
		if err != nil {
			return fmt.Errorf("write Makefile: %w", err)
		}

		return nil
	}

	cmd := &cobra.Command{
		Use:   "init",
		Short: "Generates main Makefile for project",

		RunE: cmdFunc,
	}

	return cmd
}
