#include "yamlscript.h"

yamlscript_context* yamlscript_create_context() {
  yamlscript_context* context = malloc(sizeof(yamlscript_context*));
  context->isolate = malloc(sizeof(void*));
  return context;
}

const char* yamlscript_load(yamlscript_context* context, const char* code) {
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
  (*graal_create_isolate)(NULL, &context->isolate, &threadisolate);

  const char* response = (*load_ys_to_json)(threadisolate, code);
  (*graal_tear_down_isolate)(threadisolate);

  dlclose(libyamlscript);

  return response;
}

void yamlscript_destroy_context(yamlscript_context* context) {
  free(context->isolate);
  free(context);
}

const char* yamlscript_eval(const char* code) {
  yamlscript_context* context = yamlscript_create_context();
  const char* response = yamlscript_load(context, code);

  yamlscript_destroy_context(context);

  return response;
}
