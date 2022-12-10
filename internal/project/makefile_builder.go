package project

import (
	"fmt"
	"strings"
)

type makefileBuilder struct {
	builder strings.Builder
}

func (b *makefileBuilder) appendVariable(name string, value string) {
	b.builder.WriteString(name)
	b.builder.WriteString(" := ")
	b.builder.WriteString(value)
	b.builder.WriteString("\n")
}

func (b *makefileBuilder) appendVariableWithExport(name string, value string) {
	b.appendVariable(name, value)

	b.builder.WriteString("export ")
	b.builder.WriteString(name)
	b.builder.WriteString("\n")
}

func (b *makefileBuilder) appendRule(output string, depends string, commands ...string) {
	b.builder.WriteString(output)
	b.builder.WriteString(":")
	if depends != "" {
		b.builder.WriteString(" " + depends)
	}

	for _, cmd := range commands {
		b.builder.WriteString("\n\t")
		b.builder.WriteString(cmd)
	}

	b.builder.WriteString("\n\n")
}

func (b *makefileBuilder) appendMkdirRule(dirname string) {
	b.builder.WriteString(dirname)
	b.builder.WriteString(": ;\n\tmkdir -p $@\n\n")
}

func (b *makefileBuilder) appendInclude(path string) {
	b.builder.WriteString(fmt.Sprintf("include %s\n", path))
}

func (b *makefileBuilder) append(s string) {
	b.builder.WriteString(s)
}
