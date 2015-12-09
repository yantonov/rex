(ns rex.repl.core
  (:require [clojure.browser.repl :as repl])
  ;; (:require [rex.core :as core])
  )

(defonce conn
  (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(println "Hello Cruel World!")

(defonce init-state {:text "Hello, this is: " :numbers ["one" "two" "four"]})

(defonce app-state (atom init-state))

(defn foo [a b]
  (* a b))

(defn bar [d e]
  (+ d e))
