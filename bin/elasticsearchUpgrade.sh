#!/bin/bash

prog=$(basename "$0")
indices=("notifications" "apilog" "bills" "agendas" "calendars" "committees" "transcripts" "hearings" "laws" "members")
usage() {
  echo "Usage: $prog [--delete-indices] [--new-version es_version_number]" >&2
}
check_exit_code() {
  if [ $? != 0 ]; then
    exit 1;
  fi
}
#TODO: check for superuser privileges
es_reindex() {
  echo "Reindexing documents from $1 to $2..."
  curl -HContent-Type:application/json -XPOST localhost:9200/_reindex?pretty -d'{
    "source": {
      "index": "'"$1"'"
    },
    "dest": {
      "index": "'"$2"'"
    }
  }'
  check_exit_code
  echo "Done reindexing."
}

es_delete() {
  # TODO: check doesn't work
  if curl -I "localhost:9200/$1"; then
    curl -X DELETE "localhost:9200/$1"
    check_exit_code
    echo "Deleted $1 index."
  fi
}

delete=0
new_version=0

while [ $# -gt 0 ]; do
  case "$1" in
    --delete-indices) delete=1 ;;
    --new-version) new_version="$2"; shift; ;;
    --help) usage; exit 0 ;;
    *) usage; exit 1 ;;
  esac
  shift
done

if [ "$new_version" == 0 ]; then
    usage
    exit 1
fi

wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-"${new_version}"-amd64.deb >&1
check_exit_code

if [ "$delete" != 0 ]; then
    for primary_store in "${indices[0]}" "${indices[1]}"; do
      es_reindex "$primary_store" "$primary_store"-old
    done
    for index in "${indices[@]}"; do
      es_delete "$index"
    done
fi

# A flush should speed things up
curl -X POST "localhost:9200/_flush"
systemctl stop elasticsearch.service
dpkg -i elasticsearch-"${new_version}"-amd64.deb >&1
check_exit_code
systemctl daemon-reload
systemctl start elasticsearch.service

read -rp "Restart OpenLegislation and wait for index creation to finish. Press enter to continue."

# Primary stores must be restored, and the backups deleted
if [ "$delete" != 0 ]; then
  for primary_store in "${indices[0]}" "${indices[1]}"; do
    es_reindex "$primary_store"-old "$primary_store"
    es_delete "$primary_store"-old
  done
fi
echo "Primary store indices have been restored."
