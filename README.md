labelset
========

[![Go Reference](https://pkg.go.dev/badge/github.com/kravemir/labelset.svg)](https://pkg.go.dev/github.com/kravemir/labelset)
[![Go Report Card](https://goreportcard.com/badge/github.com/kravemir/labelset)](https://goreportcard.com/report/github.com/kravemir/labelset)
[![Test](https://github.com/kravemir/labelset/actions/workflows/test.yml/badge.svg)](https://github.com/kravemir/labelset/actions/workflows/test.yml)

Labelset is a CLI tool and a library for generating masses of labels for printing, in quick and simple way.

## About

Labelset provides:

- population of SVG templates with instance:
  - XPath based query for matching text elements for replacements,
  - standard feature rich [text/template](https://pkg.go.dev/text/template) for replacement contents.
- generation of tiled labels documents:
  - paper specification supports paper size, label size, label offset and label spacing options.

## Use as a command line tool

The tool offers command-line interface as a simple way to invoke these manipulations without need to write any custom code.

###  CLI tool installation

The tool can be installed as a go module:

```shell
go install github.com/kravemir/labelset@latest
```

### Usage examples

TO BE documented.

### CLI options

Check [complete help](docs/cli) to see all available options.

## Use as a docker image

Prepared docker images are available in [Docker Hub repository](https://hub.docker.com/r/kravemir/labelset/tags).

TO BE documented more.

## Use as a library

First, use `go get` to install the latest version of the library.

```shell
go get -u github.com/kravemir/labelset@latest
```

Next, include it in your application:

```go
import "github.com/kravemir/labelset"
```

## License

The project is licensed under Apache License, which allows proprietary use. See [LICENSE](LICENSE) for more details.

