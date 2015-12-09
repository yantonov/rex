#!/bin/bash

SCRIPT_DIR=$(cd `dirname $0` && pwd)

CLJS_VERSION="1.7.170"
CLJS_JAR_FILE="cljs.jar"
CLJS_JAR_URL="https://github.com/clojure/clojurescript/releases/download/r$CLJS_VERSION/$CLJS_JAR_FILE"

LIB_DIR="$SCRIPT_DIR/../lib"
DESTINATION_FILE="$LIB_DIR/$CLJS_JAR_FILE"

cd $SCRIPT_DIR

if [ ! -f $DESTINATION_FILE ];
then
    echo "[INFO] downloading $CLJS_JAR_FILE"
    curl -L -o $DESTINATION_FILE $CLJS_JAR_URL
    echo "[INFO] file $CLJS_JAR_FILE has successfully downloaded"
fi
