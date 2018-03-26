lablie
==========

[![lablie](https://snapcraft.io/lablie/badge.svg)](https://snapcraft.io/lablie)


Designed to be simply usable java library and java tool for generation of documents with labels to print.

The library consist of renderers performing SVG manipulations for creation of label materials.

## CLI tool

The tool offers commandline a simple way to invoke these manipulations without need to write any java code.

###  Tool installation

The tool can be installed as [snap package from snapcraft.io][snapcraft-io-package]:

```
sudo snap install lablie
```

### Usage examples

See [examples at docs][docs-cli-examples].

### CLI options

Check [complete help](docs/help.md) to see all available options.

## Library usage

Library's artifacts are published to maven central. See details [at search.maven.org][search-maven-org-by-group], or [at mvnrepository.com][mvnrepository-com-group].

For documentation and javadocs, see [kravemir.gitlab.io/lablie/developers](https://kravemir.gitlab.io/lablie/developers).

## License

The project is licensed under Apache License, Version 2.0, January 2004. See [LICENSE](LICENSE).

[docs-cli-examples]: https://kravemir.gitlab.io/lablie/cli-examples/
[snapcraft-io-package]: https://snapcraft.io/lablie
[search-maven-org-by-group]: https://search.maven.org/search?q=kravemir
[mvnrepository-com-group]: https://mvnrepository.com/artifact/org.kravemir.svg.labels
