package cmd

import (
	"fmt"
	"os"

	"github.com/spf13/cobra"
	"github.com/spf13/cobra/doc"
)

func genManPageCmd() *cobra.Command {
	var outputDirectory string

	argsFunc := func(cmd *cobra.Command, args []string) error {
		if len(args) != 1 {
			return fmt.Errorf("expected 1 argument, got %d arguments", len(args))
		}

		outputDirectory = args[0]

		return nil
	}

	cmdFunc := func(cmd *cobra.Command, args []string) error {
		root := rootCmd()

		header := &doc.GenManHeader{
			Title:   root.Name(),
			Section: "1",
		}

		err := os.MkdirAll(outputDirectory, 0755)
		if err != nil {
			return fmt.Errorf("create output directory %s for man pages: %w", outputDirectory, err)
		}

		err = doc.GenManTree(root, header, outputDirectory)
		if err != nil {
			return fmt.Errorf("generate man tree: %w", err)
		}

		return nil
	}

	cmd := &cobra.Command{
		Use:   "gen-manpage",
		Short: "Generate man pages",

		Hidden: true,

		Args: argsFunc,
		RunE: cmdFunc,
	}

	return cmd

}
