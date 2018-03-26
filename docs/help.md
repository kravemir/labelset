# `lablie` help

```
Usage: lablie [-hv] [COMMAND]
Tool to generate documents with labels for printing.
  -h, --help      display a help message
  -v, --version   display version info
Commands:
  tile      Tile labels
  instance  Fill label template with instance data
  project   Group command for project manipulation sub-commands
```

Command `tile`:
```
Usage: lablie tile [OPTIONS] SOURCE TARGET
Tile labels
      SOURCE                 Path to SVG file containing a label
      TARGET                 Path to SVG file which should be generated
      --dataset-csv FILE     Path to CSV file containing instances
      --dataset-csv-format FORMAT
                             Sets format for parsing CSV dataset (available options:
                               Default, Excel, InformixUnload, InformixUnloadCsv,
                               MySQL, PostgreSQLCsv, PostgreSQLText, RFC4180, TDF)
      --dataset-json FOLDER  Path to folder containing JSON files for instances
      --instance KEY         Key of instance to be rendered
      --instance-json FILE   Path to JSON file containing values for single instance
      --instances-json FILE  Path to JSON file containing array of instances (can be
                               used in combination with --dataset-json)
      --label-delta mm mm    X and Y delta between labels in mm, ie. 5 5
      --label-offset mm mm   X and Y offset of the first label in mm, ie. 5 5
      --label-size mm mm     Width and height of label in mm, ie.
      --paper-size mm mm     Width and height of the paper in mm, ie. 210 297 for A4
                               paper portrait
      --template-descriptor FILE
                             Path to JSON file containing descriptor of template
  -h, --help                 display a help message
```

Command `instance`:
```
Usage: lablie instance [OPTIONS] SOURCE TARGET
Fill label template with instance data
      SOURCE   Path of a SVG file containing a label
      TARGET   Path of a SVG file which should be generated
      --instance-json <instanceJsonFile>
               Path to JSON file containing values for single instance
  -h, --help   display a help message
```

Command `project`:
```
Usage: lablie project [OPTIONS] [PROJECT_FILE] [COMMAND]
Group command for project manipulation sub-commands
      [PROJECT_FILE]   File containing project configuration
  -h, --help           display a help message
Commands:
  generate-makefile  Generates makefile for project
```

Command `project generate-makefile`:
```
Usage: lablie project generate-makefile [OPTIONS]
Generates makefile for project
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
```
