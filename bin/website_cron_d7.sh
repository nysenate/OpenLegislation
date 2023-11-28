#!/bin/sh
#
# website_cron_d7.sh - Execute various website cron jobs remotely
#
# Project: OpenLegislation
# Author: Ken Zalewski
# Organization: New York State Senate
# Date: 2016-01-12
# Revised: 2016-01-26 - Now using new pdrush script to run drush
# Revised: 2016-02-10 - Add environment option, which defaults to "live"
# Revised: 2016-03-04 - Add /usr/local/bin to PATH since crond is missing it
# Revised: 2016-03-18 - Add --qa option to run QA-style drush scripts
# Revised: 2016-03-21 - Add --arg option to specify drush arguments
# Revised: 2016-05-04 - Add calendar-qa drush command
# Revised: 2016-12-22 - Add --disqus option to run Disqus integration
# Revised: 2017-01-12 - Add safe_cache_form_clear drush command
# Revised: 2017-02-09 - Add --maint option to run Drupal maintenance tasks
# Revised: 2017-08-14 - Add --update-statutes to update all statutes
# Revised: 2017-08-24 - Modify --qa option to use spotcheck-dump drush command
# Revised: 2017-10-10 - Add --accum option to run Drupal accumulator integrity
# Revised: 2018-02-22 - Add new process-queues drush command
# Revised: 2018-05-01 - Add --import-all and --import-leg options
# Revised: 2018-12-14 - Add --styles to update styles
# Revised: 2019-09-30 - Modify --qa option to add law spotcheck
# Revised: 2019-12-06 - Add nys_petitions to Drupal maintenance tasks
# Revised: 2019-12-16 - Remove feeds from Drupal maintenance tasks
# Revised: 2020-04-10 - Add nys_sage to Drupal maintenance tasks
# Revised: 2021-04-23 - Add accumulator-archive drush task
# Revised: 2021-07-29 - Remove all law-related processing, since laws are now
#                       accessed in OpenLegislation in real-time by the website
#                     - Remove --update-all-statutes and "uas" mode
#                     - Remove law spotcheck from the --qa option
# Revised: 2023-06-22 - Refresh using newer logic from D9 script
#

PATH=$PATH:/usr/local/bin
DEFAULT_SITE=d7
DEFAULT_ENV=live

prog=`basename $0`

usage() {
  echo "Usage: $prog [--import-all | --import-leg | --qa | --maint | --accum | --disqus | --styles | --version] [--site SITE_ALIAS] [--arg drush_arg [--arg drush_arg ...]] [--verbose] [--help] [environ]" >&2
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
    --import-leg|-l) mode=import; scope=leg ;;
    --qa) mode=qa ;;
    --maint|-m) mode=maint ;;
    --accum) mode=accum ;;
    --disqus) mode=disqus ;;
    --styles|--esu) mode=esu ;;
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
  qa)
    echo "About to generate QA report for all bills during the current session"
    run_pdrush spotcheck-dump bill

    echo "About to generate QA report for all calendars during the current year"
    run_pdrush spotcheck-dump calendar

    echo "About to generate QA report for all agendas during the current year"
    run_pdrush spotcheck-dump agenda
    ;;
  disqus)
    echo "About to run Disqus integration"
    run_pdrush disqus-identifier-migration
    ;;
  esu)
    echo "About to update enhanced styles"
    run_pdrush enhanced-styles-update
    ;;
  maint)
    echo "About to run Drupal maintenance tasks (module crons)"
    run_module_cron captcha
    run_module_cron ctools
    run_module_cron field
    run_module_cron googleanalytics
    run_module_cron honeypot
    run_module_cron htmlpurifier
    run_module_cron job_scheduler
    run_module_cron node
    run_module_cron nys_petitions
    run_module_cron nys_sage
    run_module_cron oauth_common
    run_module_cron pantheon_api
    run_module_cron password_policy
    run_module_cron privatemsg
    run_module_cron redirect
    run_module_cron rules_scheduler
    run_module_cron search
    run_module_cron session_api
    run_module_cron system
    run_module_cron update
    run_module_cron views_bulk_operations
    run_module_cron views_data_export
    run_module_cron votingapi
    run_module_cron xmlsitemap_menu
    run_module_cron xmlsitemap_node
    run_module_cron xmlsitemap_taxonomy
    run_module_cron xmlsitemap
    run_module_cron xmlsitemap_engines
    run_module_cron rules
    ;;
  accum)
    echo "About to run website accumulator integrity check"
    run_pdrush accumulator-integrity all
    run_pdrush accumulator-archive
    ;;
  import)
    echo "About to import bills"
    run_pdrush bill-import

    echo "About to detect bill changes"
    run_pdrush detect-bill-changes

    echo "About to import agendas"
    run_pdrush agenda-import

    echo "About to import calendars"
    run_pdrush calendar-import

    echo "About to import transcripts"
    run_pdrush transcript-import

    if [ "$scope" = "all" ]; then
      echo "About to process subscription queues for notifications"
      run_pdrush process-queues

      echo "About to import Twitter data"
      run_pdrush twitter-import

      echo "About to index new content in Solr"
      run_pdrush solr-index

      echo "About to clear items from the cache_form table"
      run_pdrush safe-cache-form-clear
    fi
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
