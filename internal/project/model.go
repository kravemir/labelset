package project

import "github.com/kravemir/labelset/tiling"

type Project struct {
	Datasets map[string]DataSet

	Outputs Outputs

	Archives []Archive
}

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

type Outputs struct {
	Instancing map[string]InstancingSpecification
	Tiling     map[string]TilingSpecification
}

type InstancingSpecification struct {
	Template string
	Dataset  string

	OutputTypes []string

	Paper tiling.TiledPaper
}

type TilingSpecification struct {
	Template string

	OutputTypes []string

	Paper tiling.TiledPaper
}

type Archive struct {
	Name    string
	Format  string
	Sources []string
}
