#include "yamlscript.h"
#include <assert.h>
#include <string.h>

void test_yamlscript_load() {
  yamlscript_context* context = yamlscript_create_context();
  const char* json_data = yamlscript_load(context, "a: 1");

  assert(strcmp(json_data, "{\"a\":1}") == 0);
}

void test_yamlscript_eval() {
  const char* json_data = yamlscript_eval("a: 1");

  assert(strcmp(json_data, "{\"a\":1}") == 0);
}


int main(int argc, char** argv) {
  test_yamlscript_load();
  test_yamlscript_eval();
}
