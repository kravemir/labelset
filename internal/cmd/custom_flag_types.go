package cmd

import (
	"fmt"
	"strconv"
	"strings"

	"github.com/kravemir/labelset/tiling"
)

type Size64Flag struct {
	tiling.Size64
}

func (s *Size64Flag) String() string {
	return fmt.Sprintf("%fx%f", s.Width, s.Height)
}

func (s *Size64Flag) Set(str string) error {
	var err error

	parts := strings.Split(str, "x")
	if len(parts) != 2 {
		return fmt.Errorf("there should be 2 parts in value")
	}

	s.Width, err = strconv.ParseFloat(parts[0], 64)
	if err != nil {
		return fmt.Errorf("invalid first value format: %w", err)
	}
	s.Height, err = strconv.ParseFloat(parts[1], 64)
	if err != nil {
		return fmt.Errorf("invalid second value format: %w", err)
	}

	return nil
}

func (s *Size64Flag) Type() string {
	return "size64"
}

type Vector64Flag struct {
	tiling.Vector64
}

func (v *Vector64Flag) String() string {
	return fmt.Sprintf("%f,%f", v.X, v.Y)
}

func (v *Vector64Flag) Set(s string) error {
	var err error

	parts := strings.Split(s, ",")
	if len(parts) != 2 {
		return fmt.Errorf("there should be 2 parts in value")
	}

	v.X, err = strconv.ParseFloat(parts[0], 64)
	if err != nil {
		return fmt.Errorf("invalid first value format: %w", err)
	}
	v.Y, err = strconv.ParseFloat(parts[1], 64)
	if err != nil {
		return fmt.Errorf("invalid second value format: %w", err)
	}

	return nil
}

func (v Vector64Flag) Type() string {
	return "vector64"
}
