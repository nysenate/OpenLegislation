#!/bin/bash
#
# xferdata.sh - Pull data from the senwww FTP server, and transfer it to
#               the open and open-beta servers.
#
# Project: OpenLegislation
# Author: Ken Zalewski
# Organization: New York State Senate
# Date: 2014-05-14
# Revised: 2014-12-01 - added config file for file destinations (local & remote)
# Revised: 2014-12-02 - added options: --keep-empty, --no-remove, --no-symlink
# Revised: 2014-12-03 - added options: --local-only, --remote-only
# Revised: 2015-11-09 - more robust config
# Revised: 2015-11-15 - added handling for 4 file types: sobi, law, sess, hear
# Revised: 2016-06-24 - prevent copying hidden files
# Revised: 2016-07-05 - removed SSH key file, since it's no longer used
# Revised: 2017-08-10 - re-enabled multiple targets
#

prog=`basename $0`
script_dir=`dirname $0`
sobi_file_glob="SOBI.*"
nyslaw_file_glob="*.UPDATE DATABASE.LAW*"

usage() {
  echo "Usage: $prog [-h] [-f config_file] [-i incoming_dir] [-o archive_dir] [--keep-empty] [--no-get] [--no-remove] [--no-send] [--no-archive] [--no-symlink] [--local-only] [--remote-only]" >&2
}

read_config() {
  fpath="$1"
  sect="$2"
  sed -n -e "/^\[$sect\]/,/^\[/p" "$fpath" | egrep -v "(^[[;#]|^$)"
}

# Return 0 ("ok") if the argument references a local path.
# Return 1 if the argument references a remote path.
is_local() {
  if echo "$1" | egrep -q '^[^:]*(/|$)'; then
    return 0
  else
    return 1
  fi
}

cfg_file=/etc/xferdata.cfg
ftp_host=
ftp_user=
ftp_pass=
inc_data_dir=
arc_data_dir=
opt_inc_data_dir=
opt_arc_data_dir=
keep_empty=0
no_get=0
no_remove=0
no_send=0
no_archive=0
no_symlink=0
local_only=0
remote_only=0

while [ $# -gt 0 ]; do
  case "$1" in
    -h|--help) usage; exit 0 ;;
    -f) shift; cfg_file="$1" ;;
    -i) shift; opt_inc_data_dir="$1" ;;
    -o) shift; opt_arc_data_dir="$1" ;;
    --keep-empty) keep_empty=1 ;;
    --no-get) no_get=1 ;;
    --no-remove) no_remove=1 ;;
    --no-send) no_send=1 ;;
    --no-archive) no_archive=1 ;;
    --no-symlink) no_symlink=1 ;;
    --local*) local_only=1 ;;
    --remote*) remote_only=1 ;;
    *) echo "$prog: $1: Invalid option" >&2; usage; exit 1 ;;
  esac
  shift
done

if [ ! -r "$cfg_file" ]; then
  echo "$prog: $cfg_file: Config file not found" >&2
  exit 1
elif [ $local_only -eq 1 -a $remote_only -eq 1 ]; then
  echo "$prog: Only one of --local-only and --remote-only can be specified" >&2
  exit 1
elif [ $no_symlink -ne 1 -a $no_archive -eq 1 ]; then
  echo "$prog: --no-archive cannot be used unless --no-symlink is also specified" >&2
  exit 1
fi

# Read in config file sections
settings=`read_config $cfg_file settings`
targets=`read_config $cfg_file targets`
# The "[settings]" section is evaluated by the shell
eval $settings

# Command line params override config file params.
for opt in inc_data_dir arc_data_dir; do
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
elif [ ! "$ftp_pass" ]; then
  echo "$prog: FTP password (ftp_pass) must be specified in config file" >&2
  exit 1
elif [ ! "$inc_data_dir" ]; then
  echo "$prog: Incoming data directory (inc_data_dir) must be specified in config file or on command line" >&2
  exit 1
elif [ ! "$arc_data_dir" ]; then
  echo "$prog: Archive data directory (arc_data_dir) must be specified in config file or on command line" >&2
  exit 1
elif [ ! -d "$inc_data_dir" ]; then
  echo "$prog: $inc_data_dir: Incoming data directory not found" >&2
  exit 1
elif [ ! -d "$arc_data_dir" ]; then
  echo "$prog: $arc_data_dir: Data archive directory not found" >&2
  exit 1
elif [ "$inc_data_dir" = "$arc_data_dir" ]; then
  echo "$prog: Incoming and archive directories cannot be the same" >&2
  exit 1
fi

# Files are transferred through three locations:  incoming, archive, and
# target.  Incoming is the location where files are FTP'd from LBDC.
# Archive is where they are placed after being processed by this script.
# Target is the one or more locations where files are copied for further
# processing by OpenLegislation.
#
# There are four types of files that are transferred:
# SOBI files, NYSLAW files, session transcripts, and public hearing transcripts

file_types="sobi law sess hear"
#file_types="sobi law"

declare -A inc_dirs=([sobi]=lbdc/sobi [law]=lbdc/nyslaw [sess]=transcripts/session [hear]=transcripts/public_hearing)
declare -A arc_dirs=([sobi]=sobis [law]=nyslaws [sess]=session_transcripts [hear]=pubhear_transcripts)
declare -A tgt_dirs=([sobi]=sobis [law]=laws [sess]=session_transcripts [hear]=hearing_transcripts)
declare -A file_lists file_counts

# Create the archive directory and subdirectories
mkdir -p $arc_data_dir/
for f in $file_types; do
  mkdir -p $arc_data_dir/${arc_dirs[$f]} || exit 1
done

# Perform all of our work in the incoming data directory
cd "$inc_data_dir/" || exit 1

echo "Starting data transfer at `date`"

if [ $no_get -ne 1 ]; then
  if [ $no_remove -ne 1 ]; then
    remove_text="and removing"
    remove_opt="--Remove-source-files"
  else
    remove_text="but not deleting"
    remove_opt=""
  fi

  echo "Retrieving $remove_text latest data from $ftp_host"
  lftp "$ftp_host" <<END_SCRIPT
user $ftp_user $ftp_pass
cache off
lcd $inc_data_dir/${inc_dirs[sobi]}
mirror -r -v -I SOBI.D*.T*.TXT -I CMS.TEXT $remove_opt
cd nyslaw
lcd $inc_data_dir/${inc_dirs[law]}
mirror -r -v -I *.UPDATE -I DATABASE.LAW* $remove_opt
END_SCRIPT
else
  echo "Skipping the retrieval of SOBI files from $ftp_host"
fi

if [ $keep_empty -ne 1 ]; then
  echo "Removing empty SOBI files"
  find ${inc_dirs[sobi]} -name "$sobi_file_glob" -size 240c | xargs -r -t rm
else
  echo "Keeping empty SOBI files"
fi

for f in $file_types; do
  pushd ${inc_dirs[$f]} >/dev/null
  file_lists[$f]=`find . -maxdepth 1 -type f -not -name ".??*"`
  if [ "${file_lists[$f]}" ]; then
    file_counts[$f]=`echo "${file_lists[$f]}" | wc -l`
  else
    file_counts[$f]=0
  fi
  echo "Number of [$f] files: ${file_counts[$f]}"
  popd >/dev/null
done

for f in $file_types; do
  echo "Copying ${file_counts[$f]} $f files from ${inc_dirs[$f]} to ${tgt_dirs[$f]}"
  for tgt in $targets; do
    if is_local "$tgt"; then
      # The current target is a local destination
      if [ $remote_only -ne 1 ]; then
        echo "=> Copying $f files to local destination: $tgt/${tgt_dirs[$f]}"
        echo "${file_lists[$f]}" | xargs -L1 -I{} cp -av "${inc_dirs[$f]}/{}" "$tgt/${tgt_dirs[$f]}"
      else
        echo "Skipping local destination: $tgt"
      fi
    else
      # The current target is a remote destination
      if [ $local_only -ne 1 ]; then
        echo "=> Copying $f files to remote destination: $tgt/${tgt_dirs[$f]}"
        echo "${file_lists[$f]}" | xargs -L1 -I{} scp -v "${inc_dirs[$f]}/{}" "$tgt/${tgt_dirs[$f]}"
      else
        echo "Skipping remote destination: $tgt"
      fi
    fi
  done

  echo "Moving ${file_counts[$f]} $f files from ${inc_dirs[$f]} to ${arc_dirs[$f]}"
  echo "${file_lists[$f]}" | xargs -L1 -I{} mv -v "${inc_dirs[$f]}/{}" "$arc_data_dir/${arc_dirs[$f]}"
done

exit 0

