package gov.nysenate.openleg.scripts.admin;

import gov.nysenate.openleg.scripts.BaseScript;
import gov.nysenate.openleg.util.Application;

import org.apache.commons.cli.CommandLine;

public class LucenePurge extends BaseScript
{

    /**{@inheritDoc}*/
    @Override
    protected boolean luceneReadOnly() {
        return false;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception
    {
        String[] args = opts.getArgs();
        Application.getLucene().deleteDocumentsByQuery(args[0]);
    }

    public static void main(String[] args) throws Exception
    {
        new LucenePurge().run(args);
    }
}
