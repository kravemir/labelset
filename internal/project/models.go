package project

import "github.com/kravemir/labelset/tiling"

type JsonCollectionStorage struct {
	Location string
}

type CSVTableStorage struct {
	Location string
}

type DataSet struct {
	JsonCollectionStorage JsonCollectionStorage
	CSVTableStorage       CSVTableStorage
}

type OutputSet struct {
	Template string
	Dataset  string

	AttributeRename map[string]string
	OutputMimetypes []string

	Paper tiling.TiledPaper
}
type Archive struct {
	Name    string
	Format  string
	Sources []string
}

type Project struct {
	Datasets   map[string]DataSet
	OutputSets map[string]OutputSet
	Archives   []Archive
}
