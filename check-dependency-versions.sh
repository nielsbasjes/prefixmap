#!/bin/bash

echo "==============================="
echo "===- CHECKING DEPENDENCIES -==="
echo "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv"
mvn versions:display-dependency-updates versions:display-plugin-updates | grep -E '( -> | Building )' | grep -v 'com\.esotericsoftware:kryo .\+ 4.0.2 -> 5'
echo "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"
echo "===-         DONE!         -==="
echo "==============================="
