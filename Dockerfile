ARG GO_VERSION=1.19.3
ARG ALPINE_VERSION=3.16


FROM golang:${GO_VERSION}-alpine${ALPINE_VERSION} AS builder

RUN apk add --no-cache inkscape make

RUN mkdir /build
WORKDIR /build

COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN go build -o ./labelset -v .
RUN chmod 755 ./labelset


FROM alpine:${ALPINE_VERSION} as base

RUN apk add --no-cache inkscape make musl musl-utils musl-locales

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

COPY --from=builder /build/labelset /usr/local/bin


FROM base as extra-fonts

RUN apk add ttf-dejavu ttf-liberation ttf-linux-libertine texmf-dist-fontsextra ghostscript-fonts && \
    mkdir -p ~/.fonts && \
    ln -s /usr/share/texmf-dist/fonts/opentype/ ~/.fonts/ && \
    fc-cache -v -f


FROM base
