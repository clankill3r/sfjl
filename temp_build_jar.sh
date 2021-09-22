#!/bin/bash

if [ $# -eq 0 ]
  then
    echo "You need to provide the bin directory of where the class files can be found as the first argument"
    echo "Sorry for the inconvenience, without this I need to rely too much on the java extensions for vscode made by Microsoft and they have been to buggy the last years..."
    exit 1
fi


bin_folder=$1
start_dir=$PWD


if [ ! -d "$bin_folder" ]
then
    echo "bin dir does not exist!"
    exit 1
fi

cd "$bin_folder"
jar cf $start_dir/lib/SFJL.jar \
    sfjl/SFJL_*.class

cd $start_dir/src
jar uf $start_dir/lib/SFJL.jar \
    sfjl/SFJL_*.java