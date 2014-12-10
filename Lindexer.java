package lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.pff.PSTAttachment;
import com.pff.PSTException;
import com.pff.PSTMessage;
@SuppressWarnings("deprecation")
public class Lindexer
{
	public void indexFromPst(String IndexDir,PSTMessage emsg) throws IOException{
		long start = new Date().getTime();
		int numIndexed = index(IndexDir,emsg);
		long end = new Date().getTime();
		System.out.println(numIndexed+"files finished in time"+(end-start));
	}
	public void indexFromFolder(String s1, String s2) throws IOException {
		 
		File indexDir = new File (s1);
		File dataDir =new File(s2);
		//System.out.print("\n"+indexDir.listFiles());
		//System.out.print("\n"+dataDir.listFiles().length);
		System.out.println(s1+s2+indexDir+dataDir);
		long start = new Date().getTime();
		int numIndexed = index(indexDir,dataDir);
		long end = new Date().getTime();
		System.out.println(numIndexed+"files finished in time"+(end-start));
	}
	 private int index(String IndexDir,PSTMessage emsg) throws IOException{
		 	Directory  Idir = FSDirectory.open(new File(IndexDir));
			StandardAnalyzer ana = new StandardAnalyzer();
			IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, ana );
			int j=0;
			try {
				IndexWriter ixw = new IndexWriter(Idir,conf);
				Document doc = new Document();
				doc.add(new Field("InternetMessageId",emsg.getInternetMessageId(), Store.YES, Index.ANALYZED));
				doc.add(new Field("Subject",emsg.getSubject(), Store.YES, Index.ANALYZED));
				doc.add(new Field("Body",emsg.getBody(), Store.YES, Index.ANALYZED));
				doc.add(new Field("DisplayTo",emsg.getDisplayTo(), Store.YES, Index.ANALYZED));
				doc.add(new Field("DisplayCC",emsg.getDisplayCC(), Store.YES, Index.ANALYZED));
				doc.add(new Field("DisplayBCC",emsg.getDisplayBCC(), Store.YES, Index.ANALYZED));				
				doc.add(new Field("SenderName",emsg.getSenderName(), Store.YES, Index.ANALYZED));
				doc.add(new Field("SenderAddress",emsg.getSenderEmailAddress(), Store.YES, Index.ANALYZED));
				doc.add(new Field("SenderName",emsg.getSenderName(), Store.YES, Index.ANALYZED));
				doc.add(new Field("MessageDeliveryTime",emsg.getMessageDeliveryTime().toLocaleString(), Store.YES, Index.ANALYZED));
				if(emsg.hasAttachments()){
					int noAtt = emsg.getNumberOfAttachments();
					for(int i=0;i<noAtt;++i){
						PSTAttachment attach = emsg.getAttachment(i);
						InputStream attachStream = attach.getFileInputStream();
						BodyContentHandler contentHandler = new BodyContentHandler();
						Metadata metadata=new Metadata();
						AutoDetectParser parser = new AutoDetectParser();
						parser.parse(attachStream, contentHandler, metadata);
						doc.add(new Field("ATTACHMENT","YES",StoredField.TYPE));
						doc.add(new Field("CONTENT",contentHandler.toString(),StoredField.TYPE));
						//doc.add(new Field("TITLE",metadata.get(Metadata.TITLE),Store.YES,Index.ANALYZED));
						doc.add(new Field("DATE",metadata.get(Metadata.DATE),Store.YES,Index.ANALYZED));
						//doc.add(new Field("LANGUAGE",metadata.get(Metadata.CONTENT_LANGUAGE),Store.YES,Index.ANALYZED));
						//doc.add(new Field("CONTENTENCODING",metadata.get(Metadata.CONTENT_ENCODING),Store.YES,Index.ANALYZED));
						attachStream.close();
					}
					
				}
				ixw.addDocument(doc);
				j= ixw.numDocs();
				ixw.commit();
				ixw.close();
				
			} 
			
			catch (IOException | PSTException | SAXException | TikaException e) {
				e.printStackTrace();
			} 
			
			return j;
	 }
	private int index(File indexDir, File dataDir) throws IOException{
		if(!dataDir.exists())
		{
			System.out.println("directory not found or not exist");
		}
		
		File[] files  = dataDir.listFiles();
		int num=0;
		
		for(int i = 0;i<files.length;++i)
		{	
			Directory  Idir = FSDirectory.open(indexDir);
			StandardAnalyzer ana = new StandardAnalyzer();
			IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, ana );
			IndexWriter ixw = new IndexWriter(Idir,conf);
			
			//parsing the mail 
			InputStream stream = new FileInputStream(files[i]);
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata =new Metadata();
			AutoDetectParser parser = new AutoDetectParser();
			try {
				parser.parse(stream, handler, metadata);
			} catch (SAXException | TikaException e) {
				// TODO Auto-generated catch block
				System.err.println("error");
			}
			Document doc1 = new Document();
			if (metadata.get(Metadata.MESSAGE_TO) != null){
				System.out.println("in document");
				
			//Field f1 = new Field();
			doc1.add(new Field("content",handler.toString(),Store.YES,Index.ANALYZED));
			doc1.add(new Field("path",files[i].getCanonicalPath(),Store.YES,Index.ANALYZED));
			//doc1.add(new Field("subject",metadata.get(Metadata.MESSAGE_CC),Store.NO,Index.ANALYZED));
			doc1.add(new Field("to",metadata.get(Metadata.MESSAGE_TO),Store.YES,Index.ANALYZED));
			doc1.add(new Field("from",metadata.get(Metadata.MESSAGE_FROM),Store.YES,Index.ANALYZED));
			//doc1.add(new Field("creationdate",metadata.get(Metadata.CREATION_DATE), null));
			}
			ixw.addDocument(doc1);
			num  = ixw.numDocs();
			ixw.commit();
			ixw.close();
		}
		return num;
	}
	
}


