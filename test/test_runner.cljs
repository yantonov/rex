(ns test-runner
  (:require [cljs.test :refer-macros [run-tests]]
            [rex.core-test]
            [rex.reducer-test]
            [rex.middleware-test]
            [rex.subscriber-test]
            [rex.ext.cursor-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'rex.core-test
        'rex.reducer-test
        'rex.middleware-test
        'rex.subscriber-test
        'rex.ext.cursor-test
        ))
    0
    1))
