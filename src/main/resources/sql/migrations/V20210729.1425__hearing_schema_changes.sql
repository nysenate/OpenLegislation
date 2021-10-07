-- Adds a new, auto-incrementing ID as the public_hearing primary key.
ALTER TABLE master.public_hearing
    DROP CONSTRAINT public_hearing_pkey CASCADE;
ALTER TABLE master.public_hearing
    ADD COLUMN id SERIAL;
ALTER TABLE master.public_hearing
    ADD PRIMARY KEY (id);
ALTER TABLE master.public_hearing
    ADD UNIQUE (filename);

-- Changes public_hearing_committee table to now store HearingHosts.
ALTER TABLE master.public_hearing_committee
    DROP COLUMN filename;
ALTER TABLE master.public_hearing_committee
    RENAME committee_chamber TO chamber;
CREATE TYPE hearing_host_type AS ENUM
    ('COMMITTEE', 'LEGISLATIVE_COMMISSION', 'TASK_FORCE', 'MAJORITY_COALITION', 'WHOLE_CHAMBER');
ALTER TABLE master.public_hearing_committee
    ADD COLUMN type hearing_host_type;
ALTER TABLE master.public_hearing_committee
    RENAME committee_name TO name;
ALTER TABLE master.public_hearing_committee
    ADD CONSTRAINT hearing_host_pkey PRIMARY KEY (chamber, type, name);
ALTER TABLE master.public_hearing_committee
    RENAME TO hearing_host;
-- Adds column to link to one-to-many table.
ALTER TABLE master.hearing_host
    ADD COLUMN id SERIAL UNIQUE;

-- Creates junction table to link hosts and hearings.
CREATE TABLE master.hearing_host_public_hearings (
    hearing_host_id int,
    public_hearing_id int,
    PRIMARY KEY (hearing_host_id, public_hearing_id),
    FOREIGN KEY (hearing_host_id)
        REFERENCES master.hearing_host(id)
        ON DELETE CASCADE,
    FOREIGN KEY (public_hearing_id)
        REFERENCES master.public_hearing(id)
        ON DELETE CASCADE
);
