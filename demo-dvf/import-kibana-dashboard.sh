#!/bin/bash
#
# Description:
#
#     Import Kibana dashboard via the experimental Kibana Import Dashboard API.
#     <https://www.elastic.co/guide/en/kibana/7.x/dashboard-import-api.html>
#     This script assumes that the Kibana instance is running on localhost:5601
#
# Usage:
#
#     import-kibana-dashboard.sh <dashboard-id>
#
# Example:
#
#     import-kibana-dashboard.sh 49e18890-9cf5-11eb-b207-efcdf249253b
#
dashboard_id="$1"

if [[ -z $dashboard_id ]]
then
    echo "Cannot import dashboard because the dashboard ID is missing. Usage:"
    echo
    echo "    import-kibana-dashboard.sh <dashboard-id>"
    echo
    exit 1
fi

# How can I get the source directory of a Bash script from within the script itself?
# https://stackoverflow.com/questions/59895/
current_dir="$(cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)"
backup_path="${current_dir}/.kibana/dashboard.${dashboard_id}.current.json"

data=$(cat "$backup_path")
response=$(curl -X POST "localhost:5601/api/kibana/dashboards/import" \
  -H "Content-Type: application/json" \
  -H "kbn-xsrf: reporting" \
  -d "$data")

if [[ $? != "0" ]]
then
    echo "Import failed with response:"
    echo -e "$response" | jq
    exit 1
fi

echo -e "$response" | jq

echo "Done. Dashboard ${dashboard_id} imported to Kibana."
