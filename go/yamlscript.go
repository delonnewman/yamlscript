package yamlscript

import (
	"encoding/json"
)

/*
#cgo LDFLAGS: -ldl
#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>

#define YAMLSCRIPT_VERSION "0.1.34";

const char* yamlscript_load(const char* code) {
  void* isolate = malloc(sizeof(void*));
  int (*graal_create_isolate)(void*, void**, void**);
  char* (*load_ys_to_json)(void*, const char*);
  int (*graal_tear_down_isolate)(void*);

  char* error;
  void* libyamlscript = dlopen("/usr/local/lib/libyamlscript.dylib.0.1.34", RTLD_LOCAL);
  if (!libyamlscript) {
    fputs(dlerror(), stderr);
    exit(1);
  }

  graal_create_isolate = dlsym(libyamlscript, "graal_create_isolate");
  if ((error = dlerror()) != NULL) {
    fputs(error, stderr);
    exit(1);
  }

  load_ys_to_json = dlsym(libyamlscript, "load_ys_to_json");
  if ((error = dlerror()) != NULL) {
    fputs(error, stderr);
    exit(1);
  }

  graal_tear_down_isolate = dlsym(libyamlscript, "graal_tear_down_isolate");
  if ((error = dlerror()) != NULL) {
    fputs(error, stderr);
    exit(1);
  }

  void* threadisolate = malloc(sizeof(void*));
  (*graal_create_isolate)(NULL, &isolate, &threadisolate);

  const char* response = (*load_ys_to_json)(threadisolate, code);
  (*graal_tear_down_isolate)(threadisolate);

  free(isolate);
  dlclose(libyamlscript);

  return response;
}
*/
import "C"

func Load(data []byte) (interface{}, error) {
	if len(data) == 0 {
		return nil, nil
	}

	jsonString := C.yamlscript_load(C.CString(string(data)))
}
