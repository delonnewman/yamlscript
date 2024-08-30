#!/usr/bin/env ys-0

!yamlscript/v0

ns: mainx

require ys::taptest: :all

say: CWD

NIL =: nil

base =:
  if CWD =~ /\/yamlscript$/:
    then: "$CWD/ys"
    else: CWD

test::
#-------------------------------------------------------------------------------
- note: "Short named functions for very common operations"
- note: 'Short named functions'

# A is for partials
- code: (fun(+ 1) 41)
  want: 42
- code: call(fun(+ 1) 41)
  want: 42
- code: fun(+ 1).call(41)
  want: 42
- code: -"inc".call(41)
  want: 42

- code: 'just: 123'
  want: 123
- code: just('123')
  want: '123'
- code: a(41).inc()
  want: 42

- code: len('hello')
  want: 5
- code: -'Hello'.len()
  want: 5

# q is for quote
- code: q(name)
  want:: \'name
- code: q((1 2 3))
  want:: \'(1 2 3)

- code: value('inc')
  want:: inc
- code: value('inc').call(41)
  want: 42
- code: value(q(inc))
  want:: inc

- code: qw(one two three)
  want: [one, two, three]


#-------------------------------------------------------------------------------
- note: "Named function aliases for infix operators"

- code: eq(23 23 23 23)
- code: eq("x" "x" "x" "x")
- code: eq(["x" "x"] ["x" "x"])
- code: eq({"x" "x"} {"x" "x"})
- code: eq(false not(true))
- code: eq(nil first([]) nil)

- code: ne(23 23 23 24)
- code: ne("x" "x" "x" "y")
- code: ne(["x" "x"] ["x" "y"])
- code: ne({"x" "x"} {"x" "y"})
- code: ne(false not(true).not())
- code: ne(nil [] nil)

- code: 'gt: (2 + 3) 4'
- code: 'ge: (2 + 2) 4'
- code: 'lt: 4 (2 + 3)'
- code: 'le: 4 (2 + 2)'
- code: 'lt: 1 2 3 4'
- code: 'le: 1 2 2 3'


#-------------------------------------------------------------------------------
- note: "Truthy and falsy operations"

- code: falsey?(0)
- code: falsey?(0.0)
- code: falsey?('')
- code: falsey?("")
- code: falsey?([])
- code: falsey?({})
- code: falsey?(\{})
- code: falsey?(nil)
- code: falsey?(false)

- code: -"" ||| [] ||| 42
  want: 42
- code: 42 &&& []
  want: null



#-------------------------------------------------------------------------------
- note: "Common type conversion functions"


#-------------------------------------------------------------------------------
- note: "Math functions"


#-------------------------------------------------------------------------------
- note: "YAML Anchor and alias functions"


#-------------------------------------------------------------------------------
- note: "YAMLScript document result stashing functions"


#-------------------------------------------------------------------------------
- note: "Dot chaining support"

- code: nil.$NIL
  want: null
- code: nil.123
  want: null
- code: nil.foo
  want: null
- code: -{}.$NIL
  want: null
- code: -[].$NIL
  what: error
  want: Can't (get+ [] nil)

- code: (1 .. 20).partition(3 5)
  want:: \'((1 2 3) (6 7 8) (11 12 13) (16 17 18))


#-------------------------------------------------------------------------------
- note: "Control functions"

# call a function by reference, string, or symbol
- code: -'inc'.call(41)
  want: 42
- code: \'inc.call(41)
  want: 42
- code: |
    ns: foo
    -"inc": .call(41)
  want: 42


#-------------------------------------------------------------------------------
- note: "String functions"

- code: ('foo' == 'oof'.reverse())
- code: ('foo' != 'bar')

- code: uc('foo') == 'FOO'
- code: uc1('foo') == 'Foo'
- code: lc('FoOoO') == 'foooo'

- code: -"Hello".split().join()
  want: Hello


#-------------------------------------------------------------------------------
- note: "Collection functions"

- code: (\\A .. \\E)
  want:: \'(\\A \\B \\C \\D \\E)

- code: 'reduce + 0 (1 .. 5):'
  want: 15
- code: 'reduce _ 0 (1 .. 5): +'
  want: 15
- code: -[{"a" 1}{"a" 2}].map(\(get %1 "a"))
  want:: \'(1 2)
- code: -"abc".map(int)
  want:: \'(97 98 99)
- code: int.map('abc')
  want:: \'(97 98 99)
- code: -"abc".map(int).mapv(inc)
  want:: -[98 99 100]
- code: '(1 .. 10).has?(5)'
- code: 'num(5).in?(1 .. 10)'


#-------------------------------------------------------------------------------
- note: "I/O functions"


#-------------------------------------------------------------------------------
- note: "File system functions"

- code: 'fs-d: CWD'
- code: 'fs-e: CWD'
- code: 'fs-f: "$base/test/std.t"'
- code: 'fs-l: "$base/test/a-symlink"'
- code: 'fs-r: CWD'
- code: 'fs-s: CWD'
- code: 'fs-w: CWD'
- code: 'fs-x: CWD'
- code: 'fs-z: "$base/test/empty-file"'

- code: fs-cwd()
  want:: CWD
- code: 'fs-which: "ys"'
  like: /ys$
- code: fs-mtime(CWD).str()
  like: ^\d{13}$


#-------------------------------------------------------------------------------
- note: "Regex functions"


#-------------------------------------------------------------------------------
- note: "Java interop functions"


#-------------------------------------------------------------------------------
- note: "IPC functions"


#-------------------------------------------------------------------------------
- note: "External library functions"


#-------------------------------------------------------------------------------
- note: "HTTP functions"


#-------------------------------------------------------------------------------
done:
