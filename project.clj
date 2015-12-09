(defproject rex "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]]
  :plugins [[lein-cljsbuild "1.1.1"]]
  :cljsbuild {
              :builds {
                       :production
                       {
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "target/main.js"
                                   :optimizations :whitespace
                                   :pretty-print false}}
                       :unittest
                       {
                        :source-paths ["src" "test"]
                        :notify-command ["phantomjs"
                                         "phantom/unit-test.js"
                                         "phantom/unit-test.html"]
                        :compiler {
                                   :output-to "target/testable.js"
                                   :optimizations :whitespace
                                   :pretty-print false}}}
              :test-commands {"unit-tests"
                              ["phantomjs"
                               "phantom/unit-test.js"
                               "phantom/unit-test.html"]}})
