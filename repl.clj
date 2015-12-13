(require 'cljs.repl)
(require 'cljs.build.api)
(require 'cljs.repl.browser)

(def sources (cljs.build.api/inputs "src" "repl-src"))

(cljs.build.api/build sources
                      {:main 'rex.repl.core
                       :output-to "target/rex.repl.js"
                       :verbose true})

(cljs.repl/repl (cljs.repl.browser/repl-env)
                :watch sources
                :output-dir "target")
