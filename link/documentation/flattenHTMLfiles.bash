#!/bin/bash
# look for all htm files in the directory
mkdir $1_flat
cd $1
FILES=`ls *htm`
for f in $FILES
do
  first=${f/\.htm/}
  echo "Processing $first.htm file in directory $1..."

  cat ${first}.htm | sed 's/src[[:space:]]*=[[:space:]]*'$first'\_files\//src='$first'_flat_/' > "../${1}_flat/${first}.htm"

  
  echo "moving to directory ${first}_files"
  cd ${first}_files
  for d in `ls *`
  do
     echo "Copying file $d in `pwd` to ../../$1_flat/${first}_flat_$d"
     cp $d ../../${1}_flat/${first}_flat_$d

  done
  cd ..
done
