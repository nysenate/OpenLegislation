
-- Create an index on law_id and doc_id fields
CREATE INDEX law_tree_law_id_doc_id_idx
  ON master.law_tree (law_id, doc_id);
