# `labelset` help

```
labelset is a tool to generate documents with labels for printing

Usage:
  labelset [command]

Available Commands:
  completion  Generate the autocompletion script for the specified shell
  help        Help about any command
  instance    Fill label template with instance data
  project     Group command for project manipulation sub-commands
  tile        Generated documents with tiled labels

Flags:
  -h, --help   help for labelset

Use "labelset [command] --help" for more information about a command.
```

Command `tile`:
```
Generated documents with tiled labels

Usage:
  labelset tile [SOURCE] [TARGET] [flags]

Flags:
  -h, --help                         help for tile
      --instance-json string         Path to JSON file containing values for single instance
      --label-offset vector64        X and Y offset of the first label in mm, ie. 5 5 (default 0.000000,0.000000)
      --label-size size64            Width and height of label in mm, ie. "5x5" (default 0.000000x0.000000)
      --label-spacing vector64       X and Y spacing between labels in mm, ie. 5 5 (default 0.000000,0.000000)
      --paper-size size64            Width and height of the paper in mm, ie. "210x297" for A4 paper portrait (default 0.000000x0.000000)
      --template-descriptor string   Path to JSON file containing descriptor of template
```

Command `instance`:
```
Fill label template with instance data

Usage:
  labelset instance [SOURCE] [TARGET] [flags]

Flags:
  -h, --help                         help for instance
      --instance-json string         Path to JSON file containing values for single instance
      --template-descriptor string   Path to JSON file containing descriptor of template
```

Command `project`:
```
Group command for project manipulation sub-commands

Usage:
  labelset project [command]

Available Commands:
  init                      Generates main Makefile for project

Flags:
  -h, --help   help for project

Use "labelset project [command] --help" for more information about a command.
```
