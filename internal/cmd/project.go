package cmd

import (
	"github.com/spf13/cobra"
)

func projectCmd() *cobra.Command {
	cmd := &cobra.Command{
		Use:   "project",
		Short: "Group command for project manipulation sub-commands",
	}

	cmd.AddCommand(projectInitCmd())
	cmd.AddCommand(projectGenerateRuntimeMakefile())

	return cmd
}
