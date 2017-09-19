#!/bin/sh
#
# run_website_cron.sh - Execute various website cron jobs remotely
#
# Project: OpenLegislation
# Author: Ken Zalewski
# Organization: New York State Senate
# Date: 2016-01-12
# Revised: 2016-01-26 - Now using new pdrush script to run drush
# Revised: 2016-02-10 - Added environment option, which defaults to "live"
# Revised: 2016-03-04 - Add /usr/local/bin to PATH since crond is missing it
# Revised: 2016-03-18 - Added --qa option to run QA-style drush scripts
# Revised: 2016-03-21 - Added --arg option to specify drush arguments
# Revised: 2016-05-04 - Added calendar-qa drush command
# Revised: 2016-12-22 - Added --disqus option to run Disqus integration
# Revised: 2017-01-12 - Added safe_cache_form_clear drush command
# Revised: 2017-02-09 - Added --maint option to run Drupal maintenance tasks
# Revised: 2017-08-14 - Added --update-statutes to update all statutes
# Revised: 2017-08-24 - Modified --qa option to use spotcheck-dump drush command
#

PATH=$PATH:/usr/local/bin
DEFAULT_ENV=live

prog=`basename $0`
penv=$DEFAULT_ENV

usage() {
  echo "Usage: $prog [--qa | --maint | --update-statutes | --disqus] [--arg drush_arg [--arg drush_arg ...]] [--help] [environ]" >&2
  echo "  where 'environ' is typically one of: live, test, dev" >&2
}

run_module_cron() {
  penv="$1"
  modname="$2"
  echo "Running Drupal cron for module [$modname] on [$penv]"
  pdrush @$penv php-eval "${modname}_cron()"
}


mode=default
drush_args=

while [ $# -gt 0 ]; do
  case "$1" in
    --qa) mode=qa ;;
    --maint) mode=maint ;;
    --update-statutes|--uas) mode=uas ;;
    --disqus) mode=disqus ;;
    --arg) shift; drush_args="$drush_args $1" ;;
    --help|-h) usage; exit 0 ;;
    -*) echo "$prog: $1: Invalid option" >&2; exit 1 ;;
    *) penv="$1" ;;
  esac
  shift
done

ts=`date +"%Y-%m-%d %H:%M:%S"`

echo "$ts - Running $mode website cron tasks for [$penv] environment"

if [ "$mode" = "qa" ]; then
  echo "About to generate QA report for all bills during the current session"
  pdrush @$penv spotcheck-dump bill

  echo "About to generate QA report for all calendars during the current year"
  pdrush @$penv spotcheck-dump calendar

  echo "About to generate QA report for all agendas during the current year"
  pdrush @$penv spotcheck-dump agenda

elif [ "$mode" = "disqus" ]; then

  echo "About to run Disqus integration"
  pdrush @$penv disqus-identifier-migration

elif [ "$mode" = "maint" ]; then

  echo "About to run Drupal maintenance tasks (module crons)"
  run_module_cron $penv captcha
  run_module_cron $penv ctools
  run_module_cron $penv feeds
  run_module_cron $penv field
  run_module_cron $penv googleanalytics
  run_module_cron $penv honeypot
  run_module_cron $penv htmlpurifier
  run_module_cron $penv job_scheduler
  run_module_cron $penv node
  run_module_cron $penv oauth_common
  run_module_cron $penv pantheon_api
  run_module_cron $penv password_policy
  run_module_cron $penv privatemsg
  run_module_cron $penv redirect
  run_module_cron $penv rules_scheduler
  run_module_cron $penv search
  run_module_cron $penv session_api
  run_module_cron $penv system
  run_module_cron $penv update
  run_module_cron $penv views_bulk_operations
  run_module_cron $penv views_data_export
  run_module_cron $penv votingapi
  run_module_cron $penv xmlsitemap_menu
  run_module_cron $penv xmlsitemap_node
  run_module_cron $penv xmlsitemap_taxonomy
  run_module_cron $penv xmlsitemap
  run_module_cron $penv xmlsitemap_engines
  run_module_cron $penv rules

elif [ "$mode" = "uas" ]; then

  echo "About to update all statutes"
  pdrush @$penv update-all-statutes $drush_args

else

  echo "About to import bills"
  pdrush @$penv bill-import $drush_args

  echo "About to import agendas"
  pdrush @$penv agenda-import $drush_args

  echo "About to import calendars"
  pdrush @$penv calendar-import $drush_args

  echo "About to import transcripts"
  pdrush @$penv transcript-import $drush_args

  echo "About to import Twitter data"
  pdrush @$penv twitter-import $drush_args

  echo "About to index new content in Solr"
  pdrush @$penv solr-index $drush_args

  echo "About to clear items from the cache_form table"
  pdrush @$penv safe-cache-form-clear
fi

ts=`date +"%Y-%m-%d %H:%M:%S"`

echo "$ts - Completed execution of $mode website cron tasks for [$penv] environment"

exit 0
