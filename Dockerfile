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


FROM alpine:${ALPINE_VERSION}

RUN apk add --no-cache inkscape make

COPY --from=builder /build/labelset /usr/local/bin

