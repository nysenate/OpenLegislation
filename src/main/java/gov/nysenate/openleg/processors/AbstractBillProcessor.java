package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.SOBIBlock;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

abstract public class AbstractBillProcessor
{
    private final Logger logger = Logger.getLogger(AbstractBillProcessor.class);

    public static class ParseError extends Exception
    {
        private static final long serialVersionUID = -2394555333799843588L;
        public ParseError(String message) { super(message); }
    }


    /**
     * Applies changes in the SOBI file to objects in storage.
     *
     * @throws IOException if File cannot be opened for reading.
     */
    public void process(File sobiFile, Storage storage) throws IOException
    {
        Date date = null;
        try {
            date = BillProcessor.sobiDateFormat.parse(sobiFile.getName());
        }
        catch (ParseException e) {
            logger.error("Unparseable date: "+sobiFile.getName());
            return;
        }

        // Catch exceptions on a per-block basis so that a single error won't corrupt the whole file.
        for (SOBIBlock block : getBlocks(sobiFile)) {
            logger.info("Processing "+block);
            try {

                if (block.getType() == '1' && block.getData().startsWith("DELETE")) {
                    // Special case here were we delete the whole bill
                    logger.info("DELETING "+block.getHeader());
                    deleteBill(block, storage);
                }
                else {
                    String data = block.getData().toString();
                    // Otherwise, apply the block to the bill normally
                    Bill bill = getOrCreateBill(block, storage);
                    switch (block.getType()) {
                        case '1': applyBillInfo(data, bill, date); break;
                        case '2': applyLawSection(data, bill, date); break;
                        case '3': applyTitle(data, bill, date); break;
                        case '4': applyBillEvent(data, bill, date); break;
                        case '5': applySameAs(data, bill, date); break;
                        case '6': applySponsor(data, bill, date); break;
                        case '7': applyCosponsors(data, bill, date); break;
                        case '8': applyMultisponsors(data, bill, date); break;
                        case '9': applyProgramInfo(data, bill, date); break;
                        case 'A': applyActClause(data, bill, date); break;
                        case 'B': applyLaw(data, bill, date); break;
                        case 'C': applySummary(data, bill, date); break;
                        case 'M':
                        case 'R':
                        case 'T': applyText(data, bill, date); break;
                        case 'V': applyVoteMemo(data, bill, date); break;
                        default: throw new ParseError("Invalid Line Code "+block.getType() );
                    }
                    logger.info("SAVING: "+bill.getSenateBillNo());
                    bill.addSobiReference(sobiFile.getName());
                    bill.setModified(date.getTime());
                    saveBill(bill, storage);
                }
            }
            catch (ParseError e) {
                logger.error("ParseError at "+block.getLocation(), e);
            }
            catch (Exception e) {
                logger.error("Unexpected Exception at "+block.getLocation(), e);
            }
        }
    }

    /**
     * Parses the given SOBI file into a list of blocks. See the Block class for more details.
     *
     * @param sobiFile
     * @return
     * @throws IOException if file cannot be opened for reading.
     */
    public List<SOBIBlock> getBlocks(File sobiFile) throws IOException
    {
        SOBIBlock block = null;
        List<SOBIBlock> blocks = new ArrayList<SOBIBlock>();
        List<String> lines = FileUtils.readLines(sobiFile);
        lines.add(""); // Add a trailing line to end the last block and remove edge cases

        for(int lineNum = 0; lineNum < lines.size(); lineNum++) {
            // Replace NULL bytes with spaces to properly format lines.
            String line = lines.get(lineNum).replace('\0', ' ');

            // Source file is not assumed to be 100% SOBI so we filter out other lines
            Matcher headerMatcher = SOBIBlock.blockPattern.matcher(line);

            if (headerMatcher.find()) {
                if (block == null) {
                    // No active block with a new matching line: create new block
                    block = new SOBIBlock(sobiFile, lineNum, line);
                }
                else if (block.getHeader().equals(headerMatcher.group()) && block.isMultiline()) {
                    // active multi-line block with a new matching line: extend block
                    block.extend(line);
                }
                else {
                    // active block does not match new line or can't be extended: create new block
                    blocks.add(block);
                    block = new SOBIBlock(sobiFile, lineNum, line);
                }
            }
            else if (block != null) {
                // Active block with non-matching line: end the current blockAny non-matching line ends the current block
                blocks.add(block);
                block = null;
            }
            else {
                // No active block with a non-matching line, do nothing
            }
        }

        return blocks;
    }

    /**
     * Safely gets the bill specified by the block from storage. If the bill does not
     * exist then it is created. When loading an amendment, if the parent bill does not
     * exist then it is created as well.
     *
     * New bills will pull sponsor and amendment information up from prior versions if
     * available.
     */
    public Bill getOrCreateBill(SOBIBlock block, Storage storage) throws ParseError
    {
        String billKey = block.getPrintNo()+block.getAmendment()+"-"+block.getYear();
        Bill bill = storage.getBill(billKey);

        if (bill != null) {
            // We retrieved the bill successfully!
            return bill;
        }
        else {
            // We need to create a new bill with this key
            if (block.getAmendment().isEmpty()) {
                // We need to create an original bill, this is easy!
                return new Bill(billKey, block.getYear());
            }
            else {
                // We need to create an amendment, this is trickier
                Bill amendment = new Bill(billKey, block.getYear());

                // All new amendments are based on the original bill
                String baseKey = block.getPrintNo()+"-"+block.getYear();
                Bill baseBill = storage.getBill(baseKey);

                if (baseBill == null) {
                    // Amendments should always have original bills already made, make it happen
                    logger.error("Bill Amendment filed without initial bill at "+block.getLocation()+" - "+block.getHeader());
                    baseBill = new Bill(baseKey, block.getYear());
                    storage.saveBill(baseBill);
                }

                // Pull sponsor information up from the base bill
                amendment.setSponsor(baseBill.getSponsor());
                amendment.setCoSponsors(baseBill.getCoSponsors());
                amendment.setMultiSponsors(baseBill.getMultiSponsors());

                // Pull up the list of existing versions and add yourself
                amendment.addAmendment(baseKey);
                amendment.addAmendments(baseBill.getAmendments());

                // Broadcast yourself to all other versions and deactivate them
                Bill activeBill = null;
                for (String versionKey : amendment.getAmendments()) {
                    Bill billVersion = storage.getBill(versionKey);
                    if (billVersion == null) {
                        throw new ParseError("Recorded bill version not found in storage: "+versionKey);
                    }
                    else {
                        billVersion.addAmendment(billKey);
                        if(billVersion.isActive()) {
                            activeBill = billVersion;
                            billVersion.setActive(false);
                        }
                        storage.saveBill(billVersion);
                    }
                }

                if (activeBill == null) {
                    logger.error("Unable to find active bill for "+amendment.getSenateBillNo()+". BIG PROBLEM!");
                    activeBill = baseBill;
                }

                // Pull some other information up from previously active bill
                amendment.setSummary(activeBill.getSummary());
                amendment.setLaw(activeBill.getLaw());

                // Activate yourself
                amendment.setActive(true);
                storage.saveBill(amendment);
                return amendment;
            }
        }
    }


    /**
     * Safely saves the bill to storage. This makes sure to sync sponsor data across all amendments
     * so that different versions of the bill cannot get out of sync.
     *
     * @param bill
     * @param storage
     */
    public void saveBill(Bill bill, Storage storage)
    {
        // Sponsor information needs to be synced at all times.
        // Normally it is always sent to the base bill and broadcasted to amendments
        // In our 2009 data set we are missing tons of base amendments and it actually
        // needs to be broadcasted backwards to the original bill.
        for (String versionKey : bill.getAmendments()) {
            Bill billVersion = storage.getBill(versionKey);
            billVersion.setSponsor(bill.getSponsor());
            billVersion.setCoSponsors(bill.getCoSponsors());
            billVersion.setMultiSponsors(bill.getMultiSponsors());
            storage.saveBill(billVersion);
        }
        storage.saveBill(bill);
    }

    /**
     * Safely deletes the bill specified from by the block. This removes all references to the bill
     * from its amendments and, if the bill was currently active, re-sets the active bill to the
     * newest amendment.
     *
     * @param block
     * @param storage
     * @throws ParseError
     */
    public void deleteBill(SOBIBlock block, Storage storage) throws ParseError
    {
        String billKey = block.getPrintNo()+block.getAmendment()+"-"+block.getYear();
        Bill bill = storage.getBill(billKey);

        if (bill == null) {
            // If we can't find a bill they are asking us to delete, don't worry about it.
            throw new ParseError("New bill with DELETE command only. Skipping "+block.getHeader()+block.getData().toString());
        }
        else {
            List<String> amendments = bill.getAmendments();
            if (amendments.size() > 0) {
                // Set the previous amendment to be the active one
                String newActiveBill = amendments.get(amendments.size()-1);

                // Remove all references to the current bill/amendment.
                for (String versionKey : bill.getAmendments()) {
                    Bill billVersion = storage.getBill(versionKey);
                    billVersion.removeAmendment(billKey);
                    if (bill.isActive() && versionKey.equals(newActiveBill)) {
                        billVersion.setActive(true);
                    }
                    storage.saveBill(billVersion);
                }
            }
            storage.del(bill.getYear()+"/bill/"+billKey);
        }
    }


    abstract public void applySummary(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyTitle(String data, Bill bill, Date date) throws ParseError;
    abstract public void applySameAs(String data, Bill bill, Date date) throws ParseError;
    abstract public void applySponsor(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyCosponsors(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyMultisponsors(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyLaw(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyLawSection(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyActClause(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyProgramInfo(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyBillInfo(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyVoteMemo(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyText(String data, Bill bill, Date date) throws ParseError;
    abstract public void applyBillEvent(String data, Bill bill, Date date) throws ParseError;
}
