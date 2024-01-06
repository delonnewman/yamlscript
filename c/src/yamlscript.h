#ifndef MAIN_H
#define MAIN_H

#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>

#define YAMLSCRIPT_VERSION "0.1.34";

typedef struct yamlscript_context yamlscript_context;

struct yamlscript_context {
  void* isolate;
};

yamlscript_context* yamlscript_create_context();
const char* yamlscript_load(yamlscript_context*, const char*);
const char* yamlscript_eval(const char*);
void yamlscript_destroy_context(yamlscript_context*);

#endif
