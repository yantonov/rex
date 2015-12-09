#!/bin/sh

SCRIPT_DIR=$(cd `dirname $0` && pwd)
LIB_DIR="lib"

REPL_CLJ_PATH="$SCRIPT_DIR/../repl.clj"
CLJS_JAR_PATH="$SCRIPT_DIR/../$LIB_DIR/cljs.jar"
INSTALL_CLJS_JAR_PATH="$SCRIPT_DIR/get-cljs.sh"

$INSTALL_CLJS_JAR_PATH

java -cp $CLJS_JAR_PATH:src clojure.main $REPL_CLJ_PATH
