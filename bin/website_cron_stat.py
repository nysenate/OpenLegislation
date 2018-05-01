
# coding: utf-8

# In[26]:


import sys
import datetime
import re
import statistics
from matplotlib import pyplot


# In[35]:


class ImportRun:
    """
    Contains data for a single import run
    """
    def __init__(self, instance, start, end, first_run):
        self.instance = instance
        self.start = start
        self.end = end
        self.first_run = first_run
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
    
run_line_prefix = "^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) - "
run_line_suffix = " default website cron tasks for \[(\w+)\] environment"
start_line_re = re.compile(run_line_prefix + "Running" + run_line_suffix)
end_line_re = re.compile(run_line_prefix + "Completed execution of" + run_line_suffix)
timestamp_format = "%Y-%m-%d %H:%M:%S"

def get_runs(log_file):
    """
    Parses a website cron log, extracting run data
    """
    runs = []
    with open(log_file) as log:
        start_time_queue = []
        instance = None
        first_start_time = None
        
        for line in log.readlines():
            start_match = start_line_re.search(line)
            end_match = end_line_re.search(line)
            match = start_match or end_match
            timestamp = None
            if match:
                timestamp = datetime.datetime.strptime(match.group(1), timestamp_format)
                if first_start_time is None:
                    first_start_time = timestamp
                line_instance = match.group(2)
                if instance is None:
                    instance = line_instance
                elif instance != line_instance:
                    raise Exception("multiple instances detected: " + str(instance) +  ", " + str(line_instance))
            if start_match:
                if start_time_queue:
                    print("run overlap! " + str(timestamp))
                start_time_queue.insert(0, timestamp)
            if end_match:
                if not start_time_queue:
                    raise Exception("No corresponding start time for end time: " + line)
                start_time = start_time_queue.pop()
                runs.append(ImportRun(instance, start_time, timestamp, first_start_time))
    return runs
        


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
    if (relative_time):
        start_time_fn = lambda run: run.get_relative_start()
    start_times =  list(map(start_time_fn, runs))
    durations = list(map(lambda run: run.get_duration().total_seconds(), runs))
    fig, ax = pyplot.subplots()
    ax.plot_date(start_times, durations)
    ax.set(xlabel="Run Start", ylabel="Run Duration (s)", 
           title="Updates Import Run Times - " + instance)
    ax.grid()
    pyplot.annotate(get_stats(runs), (0,0), (0, -20), 
                    xycoords='axes fraction', 
                    textcoords='offset points', 
                    va='top')
    
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
    
    shortest = min(durations)
    longest = max(durations)
    median = statistics.median(durations)
    mean = statistics.mean(durations)
    std = statistics.stdev(durations)
    
    stat_str  = "\nmin: {0:.1f}s".format(shortest) +                 "\nmax: {0:.1f}s".format(longest) +                 "\nmedian: {0:.1f}s".format(median) +                 "\nmean: {0:.1f}s".format(mean) +                 "\nstdev: {0:.1f}s".format(std)
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

