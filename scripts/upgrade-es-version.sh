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

if [[ -z $old_version || -z $new_version ]]
then
    echo "Missing argument(s). Usage:"
    echo
    echo "    upgrade-es-version.sh 7.8.0 7.10.0"
    echo
    exit 1
fi

# Update configuration files
filepaths=($(rg --files-with-matches --glob "**/*.{xml,yml}" CURRENT_ES_VERSION))
for filepath in "${filepaths[@]}"
do
    sed -i '' -e "s/${old_version}/${new_version}/g" $filepath
    echo "✅ ${filepath}"
done

# Update README
start=$(grep -n MANAGED_BLOCK_RUN_ES_START README.md | cut -f 1 -d :)
end=$(grep -n MANAGED_BLOCK_RUN_ES_END README.md | cut -f 1 -d :)
sed -i '' "${start},${end}s/${old_version}/${new_version}/g" README.md
echo "✅ README.md"

echo "Finished."
