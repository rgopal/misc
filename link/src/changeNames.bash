#!/bin/bash
# change names of all Kmz files (remove ? after name) and uncompress
cd sites.google.com/site/satkmz/satellite-kmz-files
for f in *kmz*
do
  echo "Processing $f file "
  # remove .kmz and everything after ? replace " " with _
  new=$(echo "$f" | sed 's/\.kmz\?.*//' | sed 's/ /\_/g')
  echo "created file name ${new}.kml.gz"
  cp "$f" "${new}.kml.gz"
  cat "${new}.kml.gz" | gunzip > "${new}.kml"
done
