#!/bin/sh
#
# website_cron_d9.sh - Execute various website cron jobs remotely
#
# Project: OpenLegislation
# Author: Ken Zalewski
# Organization: New York State Senate
# Date: 2023-06-22
# Revised: 2023-12-21 - add transcripts (session and hearing) to the import task
# Revised: 2024-01-29 - add "notify" mode for bill subscription notifications
#

PATH=$PATH:/usr/local/bin
DEFAULT_SITE=d9
DEFAULT_ENV=live

prog=`basename $0`

usage() {
  echo "Usage: $prog [--import-all | --import-agendas | --import-bills | --import-calendars | --notify | --maint | --version] [--site SITE_ALIAS] [--arg drush_arg [--arg drush_arg ...]] [--verbose] [--help] [environ]" >&2
  echo "  where 'environ' is typically one of: live, test, dev" >&2
  echo "    and SITE_ALIAS is a reference for the Pantheon site (see pdrush)" >&2
}

run_pdrush() {
  pdrush @$penv --site "$psite" $@ $drush_args
  [ $? -ne 0 ] && pdrushrc=1
}

run_module_cron() {
  modname="$1"
  echo "Running Drupal cron for module [$modname] on [$psite/$penv]"
  run_pdrush php-eval "${modname}_cron()"
}


mode=version
scope=all
drush_args=
psite=$DEFAULT_SITE
penv=$DEFAULT_ENV
pdrushrc=0

while [ $# -gt 0 ]; do
  case "$1" in
    --import-all|-a) mode=import; scope=all ;;
    --import-agendas|--ia) mode=import; scope=agendas ;;
    --import-bills|--ib) mode=import; scope=bills ;;
    --import-calendars|--ic) mode=import; scope=calendars ;;
    --import-sessions|--is) mode=import; scope=floor_transcripts ;;
    --import-hearings|--ih) mode=import; scope=public_hearings ;;
    --notify|-n) mode=notify ;;
    --maint|-m) mode=maint ;;
    --version) mode=version ;;
    --site) shift; psite="$1" ;;
    --arg) shift; drush_args="$drush_args $1" ;;
    --verbose|-v) drush_args="$drush_args --verbose" ;;
    --help|-h) usage; exit 0 ;;
    -*) echo "$prog: $1: Invalid option" >&2; exit 1 ;;
    *) penv="$1" ;;
  esac
  shift
done

ts=`date +"%Y-%m-%d %H:%M:%S"`
echo "$ts - Running website cron tasks [mode=$mode, site=$psite, env=$penv]"

case "$mode" in
  maint)
    echo "About to run Drupal maintenance tasks (module crons)"
    run_module_cron captcha
    run_module_cron field
    run_module_cron honeypot
    run_module_cron job_scheduler
    run_module_cron node
    run_module_cron pantheon_api
    run_module_cron password_policy
    run_module_cron system
    run_module_cron update
    run_module_cron votingapi
    ;;
  import)
    for s in bills agendas calendars floor_transcripts public_hearings; do
      if [ $s = $scope -o $scope = all ]; then
        echo "About to import $s"
        run_pdrush nysol-iu $s
      fi
    done
    ;;
  notify)
    run_pdrush nysbn-pu
    run_pdrush nysub-pq --queues=bill_notifications
    ;;
  version)
    run_pdrush version
    ;;
  *)
    echo "$prog: $mode: Invalid mode specified" >&2
    usage
    exit 1
esac

if [ $pdrushrc -ne 0 ]; then
  echo "$prog: One or more Drush commands failed to execute successfully [mode=$mode, site=$psite, env=$penv]" >&2
fi

ts=`date +"%Y-%m-%d %H:%M:%S"`
echo "$ts - Completed execution of website cron tasks [mode=$mode, site=$psite, env=$penv]"

exit $pdrushrc
