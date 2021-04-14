#!/bin/bash
#
# Description:
#
#     Export Kibana dashboard via the experimental Kibana Export Dashboard API.
#     <https://www.elastic.co/guide/en/kibana/7.x/dashboard-api-export.html>
#     This script assumes that the Kibana instance is running on localhost:5601
#
# Usage:
#
#     export-kibana-dashboard.sh <dashboard-id>
#
# Example:
#
#     export-kibana-dashboard.sh 49e18890-9cf5-11eb-b207-efcdf249253b
#
dashboard_id="$1"

if [[ -z $dashboard_id ]]
then
    echo "Cannot export dashboard because the dashboard ID is missing. Usage:"
    echo
    echo "    export-kibana-dashboard.sh <dashboard-id>"
    echo
    exit 1
fi

# How can I get the source directory of a Bash script from within the script itself?
# https://stackoverflow.com/questions/59895/
current_dir="$(cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)"
backup_path="${current_dir}/.kibana/dashboard.${dashboard_id}.current.json"
backup_rotate_path="${current_dir}/.kibana/dashboard.${dashboard_id}.$(date +'%s').json"

if [[ -f $backup_path ]]
then
    echo "The target file already exists (path: ${backup_path})"
    echo "Ratating it to ${backup_rotate_path}"
    mv "$backup_path" "$backup_rotate_path"
fi

response=$(curl "localhost:5601/api/kibana/dashboards/export?dashboard=${dashboard_id}")

if [[ $? != "0" ]]
then
    echo "Export failed with response:"
    echo -e "$response" | jq
    exit 1
fi

echo -e "$response" | jq > "$backup_path"

echo "Done. Dashboard ${dashboard_id} expored to:"
echo "${backup_path}"
