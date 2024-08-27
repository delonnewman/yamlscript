;; Copyright 2023-2024 Ingy dot Net
;; This code is licensed under MIT license (See License for details)

(ns yamlscript.runtime
  (:require
   [yamlscript.debug]
   [yamlscript.re :as re]
   [clojure.java.io :as io]
   [clojure.math]
   [clojure.pprint]
   [clojure.set]
   [clojure.string :as str]
   [clojure.tools.cli]
   [clojure.walk]
   ; [clojure.zip]
   ; [babashka.deps]
   [babashka.fs]
   [babashka.http-client]
   [babashka.pods.sci]
   [babashka.process]
   [sci.core :as sci]
   [ys.clj]
   [ys.std]
   [ys.poly]
   [ys.json]
   [ys.yaml]
   [ys.ys :as ys]
   [ys.taptest]
   [yamlscript.common :as common]
   [yamlscript.util :as util]))

(def ys-version "0.1.73")

(def ARGS (sci/new-dynamic-var 'ARGS))
(def ARGV (sci/new-dynamic-var 'ARGV))
(def CWD (sci/new-dynamic-var 'CWD))
(def ENV (sci/new-dynamic-var 'ENV))
(def INC (sci/new-dynamic-var 'INC))
(def RUN (sci/new-dynamic-var 'RUN))

;; Define the clojure.core namespace that is referenced into all namespaces
(def clojure-core-ns
  (let [core {;; Runtime variables
              'ARGS ARGS
              'ARGV ARGV
              'CWD CWD
              'ENV ENV
              'FILE ys/FILE
              'INC INC
              'RUN RUN
              'VERSION ys-version
              '$ common/$
              '$# common/$#

              ;; clojure.core functions overridden by YS
              'load (sci/copy-var ys.ys/load-file nil)
              'use (sci/copy-var ys.ys/use nil)

              ;; clojure.core functions not added by SCI
              'abs (sci/copy-var clojure.core/abs nil)
              'file-seq (sci/copy-var clojure.core/file-seq nil)
              'infinite? (sci/copy-var clojure.core/infinite? nil)
              'parse-double (sci/copy-var clojure.core/parse-double nil)
              'parse-long (sci/copy-var clojure.core/parse-long nil)
              'parse-uuid (sci/copy-var clojure.core/parse-uuid nil)
              'pprint (sci/copy-var clojure.pprint/pprint nil)
              'random-uuid (sci/copy-var clojure.core/random-uuid nil)
              'slurp (sci/copy-var clojure.core/slurp nil)
              'spit (sci/copy-var clojure.core/spit nil)
              'NaN? (sci/copy-var clojure.core/NaN? nil)

              ;; YAMLScript debugging functions
              '_DBG (sci/copy-var clojure.core/_DBG nil)
              'PPP (sci/copy-var yamlscript.debug/PPP nil)
              'WWW (sci/copy-var yamlscript.debug/WWW nil)
              'XXX (sci/copy-var yamlscript.debug/XXX nil)
              'YYY (sci/copy-var yamlscript.debug/YYY nil)
              'ZZZ (sci/copy-var yamlscript.debug/ZZZ nil)}
        std (ns-publics 'ys.std)
        std (update-vals std #(sci/copy-var* %1 nil))
        poly (ns-publics 'ys.poly)
        poly (update-vals poly #(sci/copy-var* %1 nil))]
    (merge core std poly)))

(def babashka-pods-ns
  {'load-pod (sci/copy-var ys/load-pod nil)
   'unload-pod (sci/copy-var babashka.pods.sci/unload-pod nil)})

(defmacro use-ns [ns-name from-ns]
  `(sci/copy-ns ~from-ns (sci/create-ns ~ns-name)))

(def cli-namespace (use-ns 'cli clojure.tools.cli))
(def clj-namespace (use-ns 'clj ys.clj))
(def std-namespace (use-ns 'std ys.std))
(def ys-namespace (use-ns 'ys ys.ys))
(def fs-namespace (use-ns 'fs babashka.fs))
(def http-namespace (use-ns 'http babashka.http-client))
(def io-namespace (use-ns 'io clojure.java.io))
(def math-namespace (use-ns 'math clojure.math))
(def process-namespace (use-ns 'process babashka.process))
(def set-namespace (use-ns 'set clojure.set))
(def str-namespace (use-ns 'str clojure.string))
(def walk-namespace (use-ns 'walk clojure.walk))
(def json-namespace (use-ns 'json ys.json))
(def yaml-namespace (use-ns 'yaml ys.yaml))
(def taptest-namespace (use-ns 'ys.taptest ys.taptest))

(def namespaces
  {'main {}

   ;; These need to be first:
   'clojure.core clojure-core-ns 'core clojure-core-ns
   'ys      ys-namespace    'ys.ys   ys-namespace
   'std     std-namespace   'ys.std  std-namespace
   'clj     clj-namespace   'ys.clj  clj-namespace

   'cli     cli-namespace
   'fs      fs-namespace
   'http    http-namespace
   'io      io-namespace
   'json    json-namespace
   'math    math-namespace
   'pods    babashka-pods-ns
   'process process-namespace
   'set     set-namespace
   'str     str-namespace
   'walk    walk-namespace
   'yaml    yaml-namespace

   'ys.taptest taptest-namespace})

(defn classes-map [class-symbols]
  (loop [[class-symbol & class-symbols] class-symbols
         m '{}]
    (if class-symbol
      (let [symbol (-> class-symbol
                     str
                     (str/replace #".*\." "")
                     symbol)
            class (eval class-symbol)]
        (recur class-symbols (assoc m
                               symbol class
                               class-symbol class)))
      m)))

(def classes
  (classes-map
    '[clojure.lang.Atom
      clojure.lang.Fn
      clojure.lang.Keyword
      clojure.lang.Numbers
      clojure.lang.Range
      clojure.lang.Seqable
      clojure.lang.Sequential
      clojure.lang.Symbol

      java.io.File

      java.lang.Boolean
      java.lang.Byte
      java.lang.Character
      java.lang.Class
      java.lang.Double
      java.lang.Error
      java.lang.Exception
      java.lang.Float
      java.lang.Integer
      java.lang.Long
      java.lang.Math
      java.lang.Number
      java.lang.Object
      java.lang.Process
      java.lang.Runtime
      java.lang.String
      java.lang.System
      java.lang.Thread
      java.lang.Throwable

      java.math.BigDecimal
      java.math.BigInteger]))

(reset! ys/sci-ctx
  (sci/init
    {:namespaces namespaces
     :classes classes}))

(defn get-runtime-info []
  {:args (util/get-cmd-args)
   :bin (util/get-cmd-bin)
   :pid (util/get-cmd-pid)
   :versions {:clojure "1.11.1"
              :sci (->>
                     (io/resource "SCI_VERSION")
                     slurp
                     str/trim-newline)
              :yamlscript ys-version}
   :yspath (util/get-cmd-path)
   })

(defn eval-string
  ([clj]
   (eval-string clj @sci/file))

  ([clj file]
   (eval-string clj file []))

  ([clj file args]
   (sci/alter-var-root sci/out (constantly *out*))
   (sci/alter-var-root sci/err (constantly *err*))
   (sci/alter-var-root sci/in (constantly *in*))

   (let [clj (str/trim-newline clj)
         file (util/abspath (or file "NO-NAME"))]
     (if (= "" clj)
       ""
       (sci/binding
        [sci/file file
         ARGS (vec
                (map #(cond (re-matches re/inum %1) (parse-long %1)
                            (re-matches re/fnum %1) (parse-double %1)
                            :else %1)
                  args))
         ARGV args
         RUN (get-runtime-info)
         CWD (str (babashka.fs/cwd))
         ENV (into {} (System/getenv))
         ys/FILE file
         INC (util/get-yspath file)]
         (let [resp (sci/eval-string+
                      @ys/sci-ctx
                      clj
                      {:ns (sci/create-ns 'main)})]
           (ys/unload-pods)
           (shutdown-agents)
           (:val resp)))))))

(sci/intern @ys/sci-ctx 'clojure.core 'eval-string eval-string)

(comment
  )
