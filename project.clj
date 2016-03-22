(defproject rex "0.1.0-SNAPSHOT"
  :description "simple state management for client side development"
  :url "https://github.com/yantonov/rex"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]]
  :profiles {:dev {:plugins [[lein-cljsbuild "1.1.3"]]}}
  :source-paths ["src"]
  :cljsbuild
  {:builds
   {:production
    {:source-paths ["src"]
     :compiler {
                :output-to "target/rex.js"
                :source-map "target/rex.js.map"
                :optimizations :whitespace
                :pretty-print false}}

    :dev
    {:source-paths ["src" "test"]
     :compiler {:output-to "target/rex.testable.js"
                :optimizations :whitespace
                :pretty-print false}
     :notify-command ["phantomjs"
                      "phantom/unit-test.js"
                      "phantom/unit-test.html"]}}
   :test-commands {"unit-tests"
                   ["phantomjs"
                    "phantom/unit-test.js"
                    "phantom/unit-test.html"]}})
