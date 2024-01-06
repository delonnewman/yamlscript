CC=cc
FLAGS=-Wall -rdynamic
LIBS=-ldl

libyamlscript.o:
	$(CC) -c -fPIC -Isrc src/yamlscript.c -o libyamlscript.o

libyamlscript.dylib: libyamlscript.o
	$(CC) libyamlscript.o -shared -o libyamlscript.dylib

test/load_test: libyamlscript.dylib
	$(CC) $(FLAGS) $(LIBS) -Isrc -L./ -lyamlscript test/load_test.c -o test/load_test

test: all test/load_test
	./test/load_test

clean:
	rm -rf libyamlscript.o
	rm -rf libyamlscript.dylib

all: libyamlscript.dylib
