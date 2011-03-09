package gov.nysenate.openleg.qa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import gov.nysenate.openleg.model.bill.Bill;

class ReportBill {
		double heat;
		long modified;
		long lastActionDate;
		String bill;
		List<String> missingFields;
		
		public ReportBill() {
			
		}
		
		public ReportBill(long modified, Bill bill, String field) {
			this.modified = modified;
			
			if(bill.getBillEvents() != null) {
				this.setLastActionDate(
						bill.getBillEvents().get(
								bill.getBillEvents().size()-1).getEventDate().getTime());
			}
			else {
				this.setLastActionDate(-1);
			}
			
			this.addMissingField(field);
			
			this.bill = bill.getSenateBillNo();
		}

		public double getHeat() {
			return heat;
		}

		public long getModified() {
			return modified;
		}

		public long getLastActionDate() {
			return lastActionDate;
		}

		public String getBill() {
			return bill;
		}
		
		public List<String> getMissingFields() {
			return missingFields;
		}

		public void setHeat(double heat) {
			this.heat = heat;
		}
		
		public void setModified(long modified) {
			this.modified = modified;
		}

		public void setLastActionDate(long lastActionDate) {
			this.lastActionDate = lastActionDate;
		}

		public void setBill(String bill) {
			this.bill = bill;
		}
		
		public void setMissingfields(List<String> missingFields) {
			this.missingFields = missingFields;
		}
		
		public void addMissingField(String missingField) {
			if(missingFields ==  null)
				missingFields = new ArrayList<String>();
			
			if(!missingFields.contains(missingField)) {
				missingFields.add(missingField);
			}
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Bill))
				return false;
			
			return ((Bill)obj).equals(this.bill);
		}
		
		public static class ByHeat implements Comparator<ReportBill> {
			public int compare(ReportBill one, ReportBill two) {
				int ret = ((Double)two.getHeat()).compareTo((Double)one.getHeat());
				if(ret == 0) {
					ret = ((Long)one.getModified()).compareTo((Long)two.getModified());
					if(ret == 0) {
						ret = ((Long)one.getLastActionDate()).compareTo((Long)two.getLastActionDate());
						if(ret == 0) {
							ret = one.getBill().compareTo(two.getBill());
						}
					}
				}
				return ret;
			}
		}
	}