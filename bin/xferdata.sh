#!/bin/bash
#
# xferdata.sh - Pull data from the LBDC FTP server, and transfer it to
#               the production and development OpenLegislation servers.
#
# Project: OpenLegislation
# Author: Ken Zalewski
# Organization: New York State Senate
# Date: 2014-05-14
# Revised: 2014-12-01 - add config file for file destinations (local & remote)
# Revised: 2014-12-02 - add options: --keep-empty, --no-remove, --no-symlink
# Revised: 2014-12-03 - add options: --local-only, --remote-only
# Revised: 2015-11-09 - more robust config
# Revised: 2015-11-15 - add handling for 4 file types: sobi, law, sess, hear
# Revised: 2016-06-24 - prevent copying hidden files
# Revised: 2016-07-05 - remove SSH key file, since it's no longer used
# Revised: 2017-08-10 - re-enable multiple targets
# Revised: 2017-10-18 - add transfer of XML files and control over file types
# Revised: 2022-10-24 - LAW files are now transferred from LBDC (same as XMLs)
# Revised: 2024-04-23 - add --no-receive to skip transfer from LBDC
#                     - remove --no-symlink; implement no_send and no_archive
#

prog=`basename $0`
script_dir=`dirname $0`
sobi_file_glob="SOBI.*"
nyslaw_file_glob="*.UPDATE DATABASE.LAW*"

usage() {
  echo "Usage: $prog [-h] [-f config_file] [-i incoming_dir] [-o archive_dir] [--keep-empty] [--skip-xml] [--skip-sobi] [--skip-law] [--skip-sess] [--skip-hear] [--no-receive] [--no-remove] [--no-send] [--no-archive] [--local-only] [--remote-only] [--debug-scp]" >&2
}

read_config() {
  fpath="$1"
  sect="$2"
  sed -n -e "/^\[$sect\]/,/^\[/p" "$fpath" | egrep -v "(^[[;#]|^$)"
}

array_key_exists() {
  # Create local reference to orignial array.
  local -n a="$1"
  k="$2"
  if [ ${a[$k]+isset} ]; then
    return 0
  else
    return 1
  fi
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
ftp_prot=ftp
ftp_host=
ftp_user=
ftp_pass=
ftp_xml_dir=xml_files
ftp_law_dir=law_files
inc_data_dir=
arc_data_dir=
opt_inc_data_dir=
opt_arc_data_dir=
keep_empty=0
no_receive=0
no_remove=0
no_send=0
no_archive=0
local_only=0
remote_only=0
scp_opt=

# There are five types of files that can be transferred:
#   XML, SOBI, NYSLAW, session transcripts, and hearing transcripts
# XML, NYSLAW, and the now-deprecated SOBI files are transferred by this
# script from LBDC to the OpenLegislation transfer server (which is typically
# the same server on which this script is running).
# Transcripts (both hearing and session) are transferred to the
# OL transfer server by STS staff, after receiving them from the Senate
# stenographer.
# Remove SOBI files from the list, as they are no longer sent by LBDC.
#declare -A xfer_filetypes=([xml]= [sobi]= [law]= [sess]= [hear]=)
declare -A xfer_filetypes=([xml]= [law]= [sess]= [hear]=)

while [ $# -gt 0 ]; do
  case "$1" in
    -h|--help) usage; exit 0 ;;
    -f) shift; cfg_file="$1" ;;
    -i) shift; opt_inc_data_dir="$1" ;;
    -o) shift; opt_arc_data_dir="$1" ;;
    --keep-empty) keep_empty=1 ;;
    --skip-xml) unset xfer_filetypes[xml] ;;
    --skip-sobi) unset xfer_filetypes[sobi] ;;
    --skip-law) unset xfer_filetypes[law] ;;
    --skip-sess) unset xfer_filetypes[sess] ;;
    --skip-hear) unset xfer_filetypes[hear] ;;
    --no-receive|--no-recv) no_receive=1 ;;
    --no-remove) no_remove=1 ;;
    --no-send) no_send=1 ;;
    --no-archive) no_archive=1 ;;
    --local*) local_only=1 ;;
    --remote*) remote_only=1 ;;
    --debug-scp) scp_opt="-v" ;;
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
elif [ ${#xfer_filetypes[*]} -eq 0 ]; then
  echo "$prog: At least one file type must be active" >&2
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

# Files are transferred through three locations: incoming, archive, and target.
# Incoming - The location where files are transferred by this script from
#            LBDC (eg. XML, NYSLAW) or where files are deposited by STS
#            staff (eg. session/hearing transcripts)
# Archive - The location where these files are placed after being processed
#           by this script.
# Target - The location(s) (either local or remote) where files are copied
#          for further processing by OpenLegislation.

declare -A inc_dirs=([xml]=lbdc/xml [sobi]=lbdc/sobi [law]=lbdc/nyslaw [sess]=transcripts/session [hear]=transcripts/public_hearing)
declare -A arc_dirs=([xml]=xmls [sobi]=sobis [law]=nyslaws [sess]=session_transcripts [hear]=pubhear_transcripts)
declare -A tgt_dirs=([xml]=xmls [sobi]=sobis [law]=laws [sess]=session_transcripts [hear]=hearing_transcripts)

declare -A ftp_dirs=([xml]=$ftp_xml_dir [law]=$ftp_law_dir)
declare -A file_lists file_counts

# Create the archive directory and subdirectories
mkdir -p $arc_data_dir/
for f in ${!xfer_filetypes[@]}; do
  mkdir -p $arc_data_dir/${arc_dirs[$f]} || exit 1
done

# Perform all of our work in the incoming data directory
cd "$inc_data_dir/" || exit 1

echo "Starting data transfer at `date`"

# Most of the files that OpenLegislation processes are either XML files or
# LAW files.  These files are pulled into the incoming/ directory from LBDC.
# Some files, namely transcripts, are transferred by external users (typically
# an STS staff member) into the incoming/ directory.

if [ $no_receive -ne 1 ]; then
  for f in ${!ftp_dirs[@]}; do
    if array_key_exists xfer_filetypes "$f"; then
      if [ $no_remove -ne 1 ]; then
        remove_text="and removing"
        remove_opt="-E"
      else
        remove_text="but not deleting"
        remove_opt=""
      fi

      echo "Retrieving $remove_text latest $f data from $ftp_host"
      lftp "$ftp_prot://$ftp_host" <<END_SCRIPT
user "$ftp_user" "$ftp_pass"
cache off
lcd $inc_data_dir/${inc_dirs[$f]}
cd ${ftp_dirs[$f]}
mget $remove_opt *
END_SCRIPT
    else
      echo "Skipping the retrieval of $f files from $ftp_host"
    fi
  done
else
  echo "Skipping the retrieval of any files from $ftp_host"
fi


# Time to start deprecating the old SOBI file logic
#if [ $keep_empty -ne 1 ]; then
#  echo "Removing empty SOBI files"
#  find ${inc_dirs[sobi]} -name "$sobi_file_glob" -size 240c | xargs -r -t rm
#else
#  echo "Keeping empty SOBI files"
#fi


# At this point, all files should be in the incoming/ file tree.

for f in ${!xfer_filetypes[@]}; do
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

for f in ${!xfer_filetypes[@]}; do
  if [ $no_send -ne 1 ]; then
    echo "Copying ${file_counts[$f]} $f files from ${inc_dirs[$f]} to ${tgt_dirs[$f]}"
    for tgt in $targets; do
      if is_local "$tgt"; then
        # The current target is a local destination
        if [ $remote_only -ne 1 ]; then
          echo "=> Copying $f files to local destination: $tgt/${tgt_dirs[$f]}"
          echo "${file_lists[$f]}" | xargs -I{} cp -av "${inc_dirs[$f]}/{}" "$tgt/${tgt_dirs[$f]}"
        else
          echo "Skipping local destination: $tgt"
        fi
      else
        # The current target is a remote destination
        if [ $local_only -ne 1 ]; then
          echo "=> Copying $f files to remote destination: $tgt/${tgt_dirs[$f]}"
          echo "${file_lists[$f]}" | xargs -I{} scp $scp_opt "${inc_dirs[$f]}/{}" "$tgt/${tgt_dirs[$f]}"
        else
          echo "Skipping remote destination: $tgt"
        fi
      fi
    done
  else
    echo "Skipping the copying/sending of ${file_counts[$f]} $f files"
  fi

  if [ $no_archive -ne 1 ]; then
    echo "Moving ${file_counts[$f]} $f files from ${inc_dirs[$f]} to ${arc_dirs[$f]}"
    echo "${file_lists[$f]}" | xargs -I{} mv -v "${inc_dirs[$f]}/{}" "$arc_data_dir/${arc_dirs[$f]}"
  else
    echo "Skipping the archiving of ${file_counts[$f]} $f files; they will remain in ${inc_dirs[$f]}"
  fi
done

echo "Finished data transfer at `date`"

exit 0
