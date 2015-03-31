package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import warc.ReadWarc;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


@SuppressWarnings("deprecation")
public class Indexer {

	public static void add (String title,String body,String url,String text) throws SolrServerException, IOException {
		HttpSolrServer server = new HttpSolrServer("http://localhost:8080/collection1/");
		SolrInputDocument doc1 = new SolrInputDocument();
		doc1.addField("url", url, 1.0f);
		doc1.addField("title", title, 1.0f);
		doc1.addField("body",body,1.0f); //solo il body dell'html
		doc1.addField("html", text, 1.0f);
		
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		docs.add( doc1 );
		server.add( docs );
		server.commit();
	}
}


