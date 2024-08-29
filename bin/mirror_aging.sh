#!/bin/bash
#
# mirror_aging.sh - Mirror the LBDC bill "aging" data to a local directory
#
# Project: OpenLegislation
# Author: Ken Zalewski
# Organization: New York State Senate
# Date: 2024-05-04
#

prog=`basename $0`
script_dir=`dirname $0`

usage() {
  echo "Usage: $prog [-h] [-f config_file] [-i incoming_dir]" >&2
}

read_config() {
  fpath="$1"
  sect="$2"
  sed -n -e "/^\[$sect\]/,/^\[/p" "$fpath" | egrep -v "(^[[;#]|^$)"
}


cfg_file=/etc/xferdata.cfg
ftp_host=
ftp_user=
ftp_aging_dir=aging_files
inc_data_dir=
opt_inc_data_dir=


while [ $# -gt 0 ]; do
  case "$1" in
    -h|--help) usage; exit 0 ;;
    -f) shift; cfg_file="$1" ;;
    -i) shift; opt_inc_data_dir="$1" ;;
    *) echo "$prog: $1: Invalid option" >&2; usage; exit 1 ;;
  esac
  shift
done

if [ ! -r "$cfg_file" ]; then
  echo "$prog: $cfg_file: Config file not found" >&2
  exit 1
fi

# Read in config file sections
settings=`read_config $cfg_file settings`
# The "[settings]" section is evaluated by the shell
eval $settings

# Command line params override config file params.
for opt in inc_data_dir; do
  optname="opt_$opt"
  optval="${!optname}"
  if [ "$optval" ]; then
    eval "$opt=$optval"
  fi
done

# Check for mandatory config parameters and for file/directory existence.
if [ ! "$ftp_host" ]; then
  echo "$prog: FTP host (ftp_host) must be specified in config file" >&2
  exit 1
elif [ ! "$ftp_user" ]; then
  echo "$prog: FTP user (ftp_user) must be specified in config file" >&2
  exit 1
elif [ ! "$inc_data_dir" ]; then
  echo "$prog: Incoming data directory (inc_data_dir) must be specified in config file or on command line" >&2
  exit 1
elif [ ! -d "$inc_data_dir" ]; then
  echo "$prog: $inc_data_dir: Incoming data directory not found" >&2
  exit 1
fi


echo "Starting data transfer at `date`"

echo "Mirroring bill aging data from $ftp_host"
      lftp "$ftp_prot://$ftp_host" <<END_SCRIPT
user "$ftp_user" "$ftp_pass"
cache off
lcd $inc_data_dir/lbdc/aging
cd $ftp_aging_dir
mirror -v
END_SCRIPT

echo "Finished data transfer at `date`"

exit 0
