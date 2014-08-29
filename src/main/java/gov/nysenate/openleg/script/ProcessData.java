package gov.nysenate.openleg.script;

import gov.nysenate.openleg.processor.DataProcessor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ProcessData extends BaseScript
{
    private static final Logger logger = LoggerFactory.getLogger(ProcessData.class);

    @Autowired
    private DataProcessor dataProcessor;

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx = init();
        ProcessData processData = ctx.getBean(ProcessData.class);
        CommandLine cmd = getCommandLine(processData.getOptions(), args);
        processData.execute(cmd);
        shutdown(ctx);
    }

    protected Options getOptions() {
        Options options = new Options();
        // Fill in options here
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {
        dataProcessor.run();
    }
}
