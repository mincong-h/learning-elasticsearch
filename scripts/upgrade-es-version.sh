#!/bin/bash
#
# Usage:
#
#     scripts/upgrade-es-version.sh <old_version> <new_version>
#
# Sample upgrading from 7.8.0 to 7.10.0:
#
#     scripts/upgrade-es-version.sh 7.8.0 7.10.0
#
old_version="$1"
new_version="$2"
filepaths=($(rg --files-with-matches --glob "**/*.{xml,yml}" CURRENT_ES_VERSION))

for filepath in "${filepaths[@]}"
do
    echo $filepath
done

echo "Finished."
