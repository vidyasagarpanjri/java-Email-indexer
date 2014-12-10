package lucene;

import java.io.File;
import java.io.IOException;

import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searchr {

	public static void main(String[] args) throws Exception {
		
		File indexDir = new File(".\\index");
		String q = "Accomplishments";
		if (!indexDir.exists() || !indexDir.isDirectory()) {
		throw new Exception(indexDir +" does not exist or is not a directory.");
		}
		search(indexDir, q);
		}

	public static void search(File indexDir, String q) throws IOException, ParseException {
		
		Directory fsdir = FSDirectory.open(indexDir); 
		@SuppressWarnings("deprecation")
		IndexReader r = IndexReader.open(fsdir);
		//IndexWriter ix = new IndexWriter(r);
		IndexSearcher is = new IndexSearcher(r);
		System.out.println(is);
		String[] fields = {"InternetMessageId","Body","Subject","SenderName","MessageDeliveryTime"};
		QueryParser query  = new MultiFieldQueryParser(fields, new StandardAnalyzer());
		//QueryParser query =new QueryParser(Version.LATEST, "content", new StandardAnalyzer());
		Query qu=query.parse(q);
		long start = new Date().getTime();
		TopDocs t = is.search(qu, 4);
		ScoreDoc[] hit = t.scoreDocs;
		System.out.println("t.totalHits"+t.totalHits);
		long end = new Date().getTime();
		System.err.println("Found " + t.totalHits +"document(s) (in"+ (end - start)+" milliseconds) that matched query '"+q+"':");
		for (int i = 0; i < hit.length; i++) 
		{	
			 Document d = is.doc(hit[i].doc);
			 System.out.println(d.get("Subject"));
			 String id = d.get("Body");
			 
			 System.out.println(id);
		}
	}
	}