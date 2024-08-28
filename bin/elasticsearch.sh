#!/bin/bash

# Script to automatically handle an Elasticsearch install or upgrade.
# Downloads and installs the update while preserving primary index data.
# Non-primary indices will be repopulated when OL starts again.

prog=$(basename "$0")
indices=("notifications" "apilog" "bills" "agendas" "calendars" "committees" "transcripts" "hearings" "laws" "members")
usage() {
  echo "Usage: $prog [--install] [--delete-indices] [--new-version es_version_number]" >&2
}

check_exit_code() {
  if [ $? != 0 ]; then
    exit 1;
  fi
}

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
  echo "Done reindexing."
}

es_delete() {
  http_code=$(curl -s -o /dev/null -w "%{http_code}" "localhost:9200/$1")
  if [ "$http_code" = 200 ]; then
    curl -X DELETE "localhost:9200/$1"
    echo "Deleted $1 index."
  fi
}

if [[ $EUID -ne 0 ]]
  then echo "Please run as root."
  exit 1
fi

install=""
delete=""
new_version=""

while [ $# -gt 0 ]; do
  case "$1" in
    --install) install=1 ;;
    --delete-indices) delete=1 ;;
    --new-version) new_version="$2"; shift; ;;
    --help) usage; exit 0 ;;
    *) usage; exit 1 ;;
  esac
  shift
done

if [ "$install" ]; then
    echo "Will install Elasticsearch and ignore other options."
    delete=""
    # Grabs the ES version from the pom.
    new_version=$(grep '<elasticsearch.version>' ../pom.xml | grep -o '[0-9][0-9.]*')
elif [ ! "$new_version" ]; then
    usage
    exit 1
fi

wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-"${new_version}"-amd64.deb >&1
check_exit_code

if [ "$delete" ]; then
    for primary_store in "${indices[0]}" "${indices[1]}"; do
      es_reindex "$primary_store" "$primary_store"-old
    done
    for index in "${indices[@]}"; do
      es_delete "$index"
    done
fi

if [ ! "$install" ]; then
  # A flush should speed things up.
  curl -X POST "localhost:9200/_flush"
  systemctl stop elasticsearch.service
fi
dpkg -i elasticsearch-"${new_version}"-amd64.deb >&1
check_exit_code
systemctl daemon-reload
if [ "$install" ]; then
    systemctl enable elasticsearch.service
fi
systemctl start elasticsearch.service
rm elasticsearch-"${new_version}"-amd64.deb

# Primary stores must be restored, and the backups deleted.
if [ "$delete" ]; then
  read -rp "Restart OpenLegislation and wait for index creation to finish. Then, press enter to continue."
  for primary_store in "${indices[0]}" "${indices[1]}"; do
    es_reindex "$primary_store"-old "$primary_store"
    es_delete "$primary_store"-old
  done
  echo "Primary store indices have been restored."
fi
