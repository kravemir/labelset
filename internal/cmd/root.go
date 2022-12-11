package cmd

import (
	"context"
	"fmt"
	"os"
	"os/signal"

	"github.com/spf13/cobra"
)

func rootCmd() *cobra.Command {
	rootCmd := &cobra.Command{
		Use:   "labelset",
		Short: "labelset is a tool to generate documents with labels for printing",

		SilenceErrors: true,
		SilenceUsage:  true,

		CompletionOptions: cobra.CompletionOptions{
			HiddenDefaultCmd: true,
		},

		DisableAutoGenTag: true,
	}

	rootCmd.AddCommand(tileCmd())
	rootCmd.AddCommand(instanceCmd())

	rootCmd.AddCommand(projectCmd())

	rootCmd.AddCommand(genMarkdownCmd())
	rootCmd.AddCommand(genManPageCmd())

	return rootCmd
}

func Execute() {
	osInterrupt := make(chan os.Signal, 1)
	signal.Notify(osInterrupt, os.Interrupt)

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()
	go func() {
		<-osInterrupt
		cancel()
	}()

	cmd := rootCmd()

	if err := cmd.ExecuteContext(ctx); err != nil {
		fmt.Fprintf(os.Stderr, "ERROR: %v\n", err)
		os.Exit(1)
	}
}
