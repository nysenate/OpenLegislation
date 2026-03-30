-- junction tables to links bill amendments to laws, replacing related_laws JSON
CREATE TABLE IF NOT EXISTS master.bill_amendment_related_laws(
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version char NOT NULL,
    action_type text NOT NULL,
    law text NOT NULL,
    FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version)
        REFERENCES master.bill_amendment(bill_print_no, bill_session_year, bill_amend_version)
);

ALTER TABLE master.bill_amendment_related_laws
    ADD CONSTRAINT uq_bill_amendment_related_laws
        UNIQUE (bill_print_no, bill_session_year, bill_amend_version, action_type, law);

INSERT INTO master.bill_amendment_related_laws(bill_print_no, bill_session_year, bill_amend_version, action_type, law)
SELECT ba.bill_print_no, ba.bill_session_year, ba.bill_amend_version, action_entry.key AS action_type, law_entry.law AS law
FROM master.bill_amendment AS ba
CROSS JOIN LATERAL jsonb_each(ba.related_laws::jsonb) AS action_entry
CROSS JOIN lateral jsonb_array_elements_text(action_entry.value) AS law_entry(law)
WHERE ba.related_laws IS NOT NULL
ON CONFLICT DO NOTHING;

-- refrain from dropping bill_amendment.related_laws for now