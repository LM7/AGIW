package warc;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import main.Indexer;

import org.apache.solr.client.solrj.SolrServerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;



public class ReadWarc {

	public static void main(String[] args) throws FileNotFoundException, IOException, SolrServerException {
		String inputWarcFile="04.warc.gz";
		// open our gzip input stream
		GZIPInputStream gzInputStream=new GZIPInputStream(new FileInputStream(inputWarcFile));

		// cast to a data input stream
		DataInputStream inStream=new DataInputStream(gzInputStream);

		// iterate through our stream
		WarcRecord thisWarcRecord;
		int i = 0;
		while ((thisWarcRecord=WarcRecord.readNextWarcRecord(inStream))!=null) {
			// see if it's a response record
			if (thisWarcRecord.getHeaderRecordType().equals("response")) {
				// it is - create a WarcHTML record
				WarcHTMLResponseRecord htmlRecord=new WarcHTMLResponseRecord(thisWarcRecord);
				// get our TREC ID and target URI
				//String thisTRECID=htmlRecord.getTargetTrecID();
				String thisTargetURI=htmlRecord.getTargetURI();

				String charset = new String(thisWarcRecord.getContent());
				int charsetPos = charset.indexOf("charset=");
				if(charsetPos!=-1){
					charset = charset.substring(charsetPos+8, charsetPos+20);
					try{
						charset = charset.substring(0, charset.indexOf("\""));
					}catch(Exception e) {
						Pattern pattern = Pattern.compile("(.*?)\n");
						Matcher matcher = pattern.matcher(charset);
						if (matcher.find())
							charset = matcher.group(1);
					}
				}
				else {
					charset = "UTF-8";
				}
				String testo;
				try {
					testo = new String(thisWarcRecord.getContent(),charset.toUpperCase());
				}catch(Exception e) {
					testo = thisWarcRecord.getContentUTF8();
				}
				/*if(thisTRECID.equalsIgnoreCase("clueweb09-en0000-04-00000")) {
					System.out.println(thisTRECID + " : " + thisTargetURI);
					System.out.println(s);
				}*/
				//System.out.println(testo);
				String testoHtml ;
				try{
					testoHtml = testo.substring(testo.indexOf("<"));
				}catch (Exception e) {
					testoHtml = testo.substring(testo.indexOf("Content-Length:"));
					testoHtml = testoHtml.replaceFirst("Content-Length: [0-9]+", "").trim();
				}

				Document doc = Jsoup.parse(testoHtml);
				Element body = doc.body();
				String title = doc.title();
				i++;
				if(i%1000==0) {
					Indexer.add(title.trim(), body.text().trim(), thisTargetURI, testoHtml);;
					System.out.println(i/1000);
				}
			}
		}

		inStream.close();
	}
}
