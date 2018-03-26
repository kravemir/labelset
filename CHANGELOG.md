# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- GUI for projects, supporting:
    - preview of document to be printed
    - TODO: initial structure (finish)
    - history of opened projects
- GUI output capabilities:
    - quick open as PDF in desktop's default PDF application
    - export to PDF
    - (TODO: experimental) print of specific label from output/label set on tiled paper

### Changed
- build: requires Java 11

## [0.6.1] - 2018-05-01
### Added
- example for project sub-command

### Changes
- fix missed renames to Lablie,
- set sub-project names to match their artifactId (integrates better with gradle defaults),
- set `Implementation-Version` for all JARs, not only executable JAR,
- extended documentation of examples,
- print user-friendly information when template descriptor file doesn't exist.

## [0.6.0] - 2018-04-19
### Breaking changes
- rename to **Lablie** from old svg-labels,
- descriptor uses `.lablie.json` extension instead of old `.svg-labels.json`

## [0.5.0] - 2018-03-24
### Added
- documentation: written in markdown, static site generated with [mdBook](https://github.com/rust-lang-nursery/mdBook)
- tool: support to use instance data from CSV dataset using new options: `--dataset-csv`, `--dataset-csv-format`, `--instance`
- tool: support for multi-page `tile` command outputs
- tool: man-page generation (gradle task `:tool:generateManPage`)

### Changed
- build: improved docker image build and quality
- documentation: examples are now part of docs
- tool: renamed option `--dataset-json` from `--instance-definitions-location` for consistency

## [0.4.0-docker] - 2018-12-07
### Added
- build: docker image

## [0.4.0] - 2018-10-30
### Added
- build: snapcraft package [SVG Labels](https://snapcraft.io/lablie)
- documentation: CLI tool usage examples
- tool: added project feature:
    - generate Makefile for incremental builds
    - support datasets stored as folder JSON data files
    - support outputsets of SVG and PDF (using [Inkscape])
    - support archives creation (zip)
- tool: added integration tests

### Changed
- library: generate immutable model classes with [LightValue]
- tool: generate immutable model classes with [LightValue]
- tool: CLI cosmetics:
    - use space as option value separator,
    - abbreviate synopsis,
    - show user-friendly message instead of RuntimeException when sub-command is missing

## [0.3.0] - 2018-08-04
### Added
- library: support of template instancing in tiling rendering
- tool: support of template instancing with option `--instances-json` for `tile` command
- tool: support to refer instances stored in `--instance-definitions-location` path using `instanceContentRef`
- tests: separated sourceset to share test classes and resources between modules 

### Changed
- library: extended tiling model to support definition of instances
- library: a bit polished API interfaces and models

## [0.2.0] - 2018-07-01
### Added
- library: instance SVG rendering from template:
    - separate template descriptor and instance data
    - matching text elements with XPath
    - support for multi-line texts
    - conditional rule application based on `if` (JEXL) condition
- tool: added option to render tiled labels from template and content JSON
- tool: added `instance` sub-command to render instance label with tiling

### Changed
- library: lots of refactoring to simplify huge classes
- tool: moved tiled labels rendering into `tile` sub-command

## 0.1.0 - 2018-05-01
### Added
- library implementing tiled SVG rendering
- CLI tool

[Unreleased]: https://gitlab.com/kravemir/lablie/compare/0.6.1...master
[0.6.1]: https://gitlab.com/kravemir/lablie/compare/0.6.0...0.6.1
[0.6.0]: https://gitlab.com/kravemir/lablie/compare/0.5.0-docker...0.6.0
[0.5.0]: https://gitlab.com/kravemir/lablie/compare/0.4.0-docker...0.5.0
[0.4.0-docker]: https://gitlab.com/kravemir/lablie/compare/0.4.0...0.4.0-docker
[0.4.0]: https://gitlab.com/kravemir/lablie/compare/0.3.0...0.4.0
[0.3.0]: https://gitlab.com/kravemir/lablie/compare/0.2.0...0.3.0
[0.2.0]: https://gitlab.com/kravemir/lablie/compare/0.1.0...0.2.0
[Inkscape]: https://inkscape.org/
[LightValue]: https://mvnrepository.com/artifact/org.kravemir.lightvalue
