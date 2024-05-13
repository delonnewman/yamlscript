package yamlscript

import (
	"testing"
)

func TestLoad(t *testing.T) {
	code := "a: 1"
	result, error = Load(code)
}
