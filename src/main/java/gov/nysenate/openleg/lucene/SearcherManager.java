package gov.nysenate.openleg.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

/*
 * pg. 361 Lucene in Action
 */
public class SearcherManager {
	private IndexSearcher currentSearcher;
	private IndexWriter writer;
	private boolean reopening;
	
	
	public SearcherManager(Directory dir) throws IOException {
		currentSearcher = new IndexSearcher(IndexReader.open(dir));
		//warm
	}

	private synchronized void startReopen() throws InterruptedException {
		while (reopening) {
			wait();
		}
		reopening = true;
	}

	private synchronized void doneReopen() {
		reopening = false;
		notifyAll();
	}

	public void maybeReopen() throws InterruptedException, IOException {
		startReopen();
		try {
			final IndexSearcher searcher = get();
			try {
				IndexReader newReader = currentSearcher.getIndexReader()
						.reopen();
				if (newReader != currentSearcher.getIndexReader()) {
					IndexSearcher newSearcher = new IndexSearcher(newReader);
					if (writer == null) {
						//warm
					}
					swapSearcher(newSearcher);
				}
			} finally {
				release(searcher);
			}
		} finally {
			doneReopen();
		}
	}

	public synchronized IndexSearcher get() {
		currentSearcher.getIndexReader().incRef();
		return currentSearcher;
	}

	public synchronized void release(IndexSearcher searcher) throws IOException {
		searcher.getIndexReader().decRef();
	}

	private synchronized void swapSearcher(IndexSearcher newSearcher)
			throws IOException {
		release(currentSearcher);
		currentSearcher = newSearcher;
	}

	public void close() throws IOException {
		swapSearcher(null);
	}
}