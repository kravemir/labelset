OUT_DIR:=output
TMP_DIR:=tmp

LABEL_PAPER := --paper-size 210 297 --label-offset 5 13.5 --label-size 100 30 --label-delta 0 0

PRODUCT_JSON_FILES  := $(wildcard product_*.json)
PRODUCT_PDF_FILES   := $(patsubst product_%.json,${OUT_DIR}/product_%.pdf,${PRODUCT_JSON_FILES})

all: ${PRODUCT_PDF_FILES}

${OUT_DIR}/%.pdf: ${TMP_DIR}/%.svg | ${OUT_DIR}
	mkdir -p $$(dirname "$@")
	inkscape --file="$<" --without-gui --export-pdf="$@"

${TMP_DIR}/%.svg: %.json label.svg | ${TMP_DIR}
	lablie tile ${LABEL_PAPER} --instance-json "$<" label.svg "$@"

${TMP_DIR}:
	mkdir -p $@

${OUT_DIR}:
	mkdir -p $@
