package project

import "os"

func GenerateMainMakefile(source string) string {
	var makefile makefileBuilder

	if val, ok := os.LookupEnv("LABELSET_BIN"); ok {
		makefile.appendVariableWithExport("LABELSET_BIN", val)
	} else {
		makefile.appendVariableWithExport("LABELSET_BIN", "labelset")
	}

	makefile.appendVariable("TMP_DIR", "tmp")

	makefile.appendRule("default", "all")

	makefile.appendRule(
		"${TMP_DIR}/labelset.Makefile",
		source+" | ${TMP_DIR}",
		"${LABELSET_BIN} project generate-runtime-makefile \"$<\" \"$@\"",
	)

	makefile.appendMkdirRule("${TMP_DIR}")

	makefile.appendInclude("${TMP_DIR}/labelset.Makefile")

	return makefile.builder.String()
}
