#!/bin/bash
FILES=`ls $1`
for f in $FILES
do
  echo "Processing $f file..."
  # take action on each file. $f store current file name
  cat $1/$f
done
