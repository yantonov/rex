(ns test-runner
  (:require [cljs.test :refer-macros [run-tests]]
            [rex.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'rex.core-test
        'rex.cursor-test
        ))
    0
    1))
