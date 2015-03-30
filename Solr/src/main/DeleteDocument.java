package main;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;


public class DeleteDocument {

	public static void main(String[] args) throws SolrServerException, IOException {
		HttpSolrServer server = new HttpSolrServer("http://localhost:8080/collection1/");
		server.deleteByQuery("*:*");
	}
}
