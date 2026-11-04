#!/bin/bash

# Installs or upgrades Elasticsearch from Elastic's .deb package.
#
# With --delete-indices, OpenLegislation's indices are dropped so the app
# recreates them with current mappings on its next start, repopulating them
# from Postgres.

set -euo pipefail

prog=$(basename "$0")
pom="$(dirname "$(readlink -f "$0")")/../pom.xml"
es="http://localhost:9200"

indices=(bills agendas calendars committees transcripts hearings laws members)

usage() {
  echo "Usage: $prog [--install] [--delete-indices] [--new-version es_version_number]" >&2
}

die() {
  echo "$prog: $*" >&2
  exit 1
}

es_curl() {
  curl --silent --show-error --fail "$@"
}

index_exists() {
  [ "$(curl --silent --output /dev/null --write-out '%{http_code}' "$es/$1")" = 200 ]
}

es_delete() {
  es_curl -X DELETE "$es/$1" >/dev/null
  echo "Deleted the $1 index."
}

wait_for_es() {
  echo "Waiting for Elasticsearch on $es..."
  for _ in $(seq 60); do
    if curl --silent --fail "$es/_cluster/health?wait_for_status=yellow&timeout=5s" >/dev/null; then
      return 0
    fi
    sleep 5
  done
  die "Elasticsearch isn't answering plain HTTP on $es. OpenLegislation connects without TLS" \
      "or credentials, so if this is a fresh install, disable security in" \
      "/etc/elasticsearch/elasticsearch.yml and restart it."
}

tmp_dir=""
on_exit() {
  [ -z "$tmp_dir" ] || rm -rf "$tmp_dir"
}
trap on_exit EXIT

if [[ $EUID -ne 0 ]]; then
  die "Please run as root."
fi

install=""
delete=""
new_version=""

while [ $# -gt 0 ]; do
  case "$1" in
    --install) install=1 ;;
    --delete-indices) delete=1 ;;
    --new-version)
      [ $# -ge 2 ] || { usage; exit 1; }
      new_version="$2"; shift ;;
    --help) usage; exit 0 ;;
    *) usage; exit 1 ;;
  esac
  shift
done

if [ "$install" ]; then
  echo "Will install Elasticsearch and ignore other options."
  delete=""
  new_version=$(grep -o '<elasticsearch.version>[^<]*' "$pom" | cut -d'>' -f2)
  [ -n "$new_version" ] || die "Couldn't read elasticsearch.version from $pom."
elif [ ! "$new_version" ]; then
  usage
  exit 1
fi

deb="elasticsearch-$new_version-amd64.deb"
url="https://artifacts.elastic.co/downloads/elasticsearch/$deb"
tmp_dir=$(mktemp -d)
echo "Downloading $deb..."
curl --fail --location --silent --show-error --output "$tmp_dir/$deb" "$url"
curl --fail --location --silent --show-error --output "$tmp_dir/$deb.sha512" "$url.sha512"
(cd "$tmp_dir" && sha512sum --check --quiet "$deb.sha512")

if [ "$delete" ]; then
  for index in "${indices[@]}"; do
    if index_exists "$index"; then
      es_delete "$index"
    fi
  done
fi

if [ ! "$install" ]; then
  # Shortens shard recovery on restart; skipped if Elasticsearch is already down.
  curl --silent --output /dev/null -X POST "$es/_flush" || true
  systemctl stop elasticsearch.service
fi
dpkg -i "$tmp_dir/$deb"
systemctl daemon-reload
if [ "$install" ]; then
  systemctl enable elasticsearch.service
fi
systemctl start elasticsearch.service
wait_for_es
