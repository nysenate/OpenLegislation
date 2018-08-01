
# coding: utf-8

# In[26]:


import sys
import datetime
import re
import statistics
from matplotlib import pyplot
import matplotlib.dates as mpdates


# In[35]:


class ImportRun:
    """
    Contains data for a single import run
    """
    def __init__(self, instance, start, end, first_run, bills_imported, import_action_times):
        self.instance = instance
        self.start = start
        self.end = end
        self.first_run = first_run
        self.bills_imported = bills_imported
        self.import_action_times = import_action_times
    def get_instance(self):
        return self.instance
    def get_start(self):
        return self.start
    def get_end(self):
        return self.end
    def get_first_run(self):
        return self.first_run
    def get_relative_start(self):
        return self.start - self.first_run
    def get_duration(self):
        return self.end - self.start
    def get_bills_imported(self):
        return self.bills_imported
    def get_bills_imported_count(self):
        return len(self.bills_imported)
    def get_import_action_times(self):
        return self.import_action_times
    
run_line_prefix = "^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) - "
run_line_suffix = " (?:default|import) website cron tasks for \[(\w+)\] environment"
start_line_re = re.compile(run_line_prefix + "Running" + run_line_suffix)
end_line_re = re.compile(run_line_prefix + "Completed execution of" + run_line_suffix)
bill_save_re = r'^Saving (\d{4}-[A-Z]\d+)[A-Z]? \.\.\.'
timestamp_format = "%Y-%m-%d %H:%M:%S"
import_action_timer_re = r'^0.(\d{3})\d{5} (\d{10}) - (starting|finished) (.+)$'

def get_runs(log_file):
    """
    Parses a website cron log, extracting run data
    """
    runs = []
    with open(log_file) as log:
        start_time_queue = []
        instance = None
        first_start_time = None
        saved_bills = None
        import_action_times = {}
        current_actions = []

        for line in log.readlines():
            start_match = start_line_re.search(line)
            end_match = end_line_re.search(line)
            save_match = re.search(bill_save_re, line)
            action_timer_match = re.search(import_action_timer_re, line)
            run_match = start_match or end_match
            if run_match:
                timestamp = datetime.datetime.strptime(run_match.group(1), timestamp_format)
                if first_start_time is None:
                    first_start_time = timestamp
                line_instance = run_match.group(2)
                if instance is None:
                    instance = line_instance
                elif instance != line_instance:
                    raise Exception("multiple instances detected: " + str(instance) +  ", " + str(line_instance))
                if start_match:
                    if start_time_queue:
                        print("run overlap! " + str(timestamp))
                    start_time_queue.insert(0, timestamp)
                    saved_bills = set()
                elif end_match:
                    if not start_time_queue:
                        raise Exception("No corresponding start time for end time: " + line)
                    start_time = start_time_queue.pop()
                    runs.append(ImportRun(instance, start_time, timestamp, first_start_time, saved_bills, import_action_times))
                    import_action_times = {}
                    saved_bills = None

            elif save_match:
                saved_bills.add(save_match.group(1))
            elif action_timer_match:
                millis = get_import_action_millis(action_timer_match)
                starting = action_timer_match.group(3) == 'starting'
                action = action_timer_match.group(4)
                if starting:
                    current_actions.append((action, millis))
                else:
                    if not current_actions:
                        raise Exception("Terminating non-existent action: " + line)
                    current_action = current_actions.pop()
                    if current_action[0] != action:
                        raise Exception(f"Terminating different action than was started: [{action}] vs [{current_action[0]}]\n{line}")
                    time_difference = millis - current_action[1]
                    prev_total = import_action_times[action] if action in import_action_times else 0
                    new_total = prev_total + time_difference
                    import_action_times[action] = new_total
    return runs

def get_import_action_millis(action_timer_match):
    seconds = int(action_timer_match.group(2))
    millis = int(action_timer_match.group(1))
    return 1000 * seconds + millis

def aggregate_import_action_times(runs):
    agg_action_times = {}
    for run in runs:
        action_times = run.get_import_action_times()
        for action, time in action_times.items():
            prev_total = agg_action_times[action] if action in agg_action_times else 0
            new_total = prev_total + time
            agg_action_times[action] = new_total
    return agg_action_times

# In[124]:


def plot_run_duration(runs, save_file=None, relative_time=False):
    """
    Plots the run time of each import run, saving and/or displaying the plot.
    """
    if not runs:
        print("No runs detected")
        return
    instance = runs[0].get_instance()
    start_time_fn = lambda run: run.get_start()
    if relative_time:
        start_time_fn = lambda run: run.get_relative_start()
    start_times =  list(map(start_time_fn, runs))
    durations = list(map(lambda run: run.get_duration().total_seconds(), runs))
    minute_durations = list(map(lambda seconds: seconds / 60, durations))
    bills_imported = list(map(ImportRun.get_bills_imported_count, runs))
    print('max imported: ' + str(max(bills_imported)))
    fig, duration_ax = pyplot.subplots()

    duration_ax.plot_date(start_times, minute_durations, markersize=4)
    duration_ax.set(
        xlabel="Run Start",
        ylabel="Run Duration (minutes)",
        title="Updates Import Run Times - " + instance)
    duration_ax.grid()

    duration_ax.xaxis.set_major_formatter(mpdates.DateFormatter('%H:%M'))

    pyplot.annotate(get_stats(runs), (0,0), (0, -20), 
                    xycoords='axes fraction', 
                    textcoords='offset points', 
                    va='top')
    duration_ax.axhline(5, color='red')

    saved_ax = duration_ax.twinx()
    saved_ax.plot_date(start_times, bills_imported, marker='x', color='g')
    saved_ax.set(ylabel="Saved Bills")
    saved_ax.tick_params('y', colors='g')
    saved_ax.xaxis.set_major_formatter(mpdates.DateFormatter('%H:%M'))

    if save_file:
        print('saving graph to ' + save_file)
        fig.savefig(save_file, bbox_inches="tight", dpi=300)
    else:
        print('rendering graph..')
        pyplot.tight_layout()
        pyplot.show()
    
def get_stats(runs):
    """
    Gets a string containing stats for the durations of the given runs.
    """
    stat_str = ""
    durations = list(map(lambda run: run.get_duration().total_seconds(), runs))

    if len(durations) > 1:
        shortest = min(durations)
        longest = max(durations)
        median = statistics.median(durations)
        mean = statistics.mean(durations)
        std = statistics.stdev(durations)
        stat_str  += "\nmin: {0:.1f}s".format(shortest) + \
                     "\nmax: {0:.1f}s".format(longest) + \
                     "\nmedian: {0:.1f}s".format(median) + \
                     "\nmean: {0:.1f}s".format(mean) + \
                     "\nstdev: {0:.1f}s".format(std)

    total_imp = sum(map(ImportRun.get_bills_imported_count, runs))
    stat_str += "\ntotal imports: " + str(total_imp)

    iats = aggregate_import_action_times(runs)
    print(str(len(iats.items())))
    if iats.items():
        stat_str += "\n\nimport action times:\n"
    for action, total_millis in sorted(iats.items(), key=lambda t: t[1], reverse=True):
        total_seconds, millis = divmod(total_millis, 1000)
        total_minutes, seconds = divmod(total_seconds, 60)
        hours, minutes = divmod(total_minutes, 60)
        hourtxt = f'{hours} hrs ' if hours > 0 else ''
        mintxt = f'{minutes} min ' if minutes > 0 else ''
        secstxt = f'{seconds} sec ' if seconds > 0 else ''
        stat_str += f'\t{action}: {hourtxt}{mintxt}{secstxt}{millis} ms\n'

    return stat_str
    


# In[121]:


def chart_runs(log_file, dest_file):
    """
    Produces a duration plot for the given log file, saving results in dest_file
    """
    runs = get_runs(log_file)

    plot_run_duration(runs, dest_file)


# In[126]:


if __name__ == '__main__' and '__file__' in globals():
    if len(sys.argv) < 2:
        raise Exception("You must provide a log file")
    log_file = sys.argv[1]
    output_file = sys.argv[2] if len(sys.argv) > 2 else None
    
    chart_runs(log_file, output_file)
else:
    chart_runs('/tmp/website_cron_openlegdev.log', None)

