package main;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;



@SuppressWarnings("deprecation")
public class Query {
	static HttpSolrServer server = new HttpSolrServer("http://localhost:8080/collection1/");
	static SolrQuery query = new SolrQuery();

	public static void main(String[] args) throws SolrServerException {

		String keywords = "*.*";
		
		SolrDocumentList resultQueryQ1 = doQuery(keywords,"AND");
		SolrDocumentList resultQueryQ3 = doQuery(keywords,"OR");
		System.out.println("\n----------------------------------------------------------------------------------");
		SolrDocumentList resultQueryQ2 = checkMispelling(keywords, "AND");
		SolrDocumentList resultQueryQ4 = checkMispelling(keywords, "OR");
		System.out.println("\n----------------------------------------------------------------------------------");
		
		SolrDocumentList s = new SolrDocumentList();
		s.addAll(resultQueryQ1);
		s.addAll(resultQueryQ3);
		
		SolrDocumentList s2 = new SolrDocumentList();
		s2.addAll(resultQueryQ2);
		s2.addAll(resultQueryQ4);
		
		
		if(s.size()>=s2.size()) {
			System.out.println("Maggiori risultati con la query corretta (#"+s.size()+")");
			System.out.println("(#mispellng: "+s2.size()+")");
			printResult(s);
		}
		else {
			System.out.println("Maggiori risultati con la query mispelling (#"+s2.size()+")");
			System.out.println("(#corrette: "+s.size()+")");
			printResult(s2);
		}
	}

	public static SolrDocumentList checkMispelling(String keywords, String operator) {
		/*settaggio parametri per il mispelling*/
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("qt", "/spell");
		params.set("q", keywords);
		params.set("spellcheck", "true");
		params.set("spellcheck.build", "true");
		query.add(params);
		QueryResponse response = null;
		try {
			response = server.query(params);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*alternative per ogni keyword*/
		List<String> alternativesForWord = new ArrayList<String>();

		//System.out.println("==== Server Response ====\nServer response: "+response);
		SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
		//System.out.println("SpellCheckResponse: "+spellCheckResponse+"\n=========================\n");
		if (spellCheckResponse!=null && !spellCheckResponse.isCorrectlySpelled()) {
			System.out.println("\n==== Dejavu? [Forse Cercavi]====");
			for (Suggestion suggestion : response.getSpellCheckResponse().getSuggestions()) {
				System.out.println("original token: " + suggestion.getToken() + " - alternatives: " + suggestion.getAlternatives());
				alternativesForWord = suggestion.getAlternatives();
				keywords = keywords.replaceAll(suggestion.getToken(), alternativesForWord.get(0));
			}
			System.out.println("================================");
		}
		
		SolrDocumentList resultQuery = doQuery(keywords,operator);
		return resultQuery;
	}
	
	
	public static SolrDocumentList doQuery(String keywords, String operator) {
		String searchString="";

		if(!keywords.contains(" ")) {
			searchString = keywords;
		}
		else {
			searchString = keywords.replaceAll(" ", " "+operator+" ");
		}
		System.out.println("\nSEARCH STRING: "+searchString);
		query.setQuery(searchString);

		QueryResponse rsp = null;
		try {
			rsp = server.query(query);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SolrDocumentList resultQuery = rsp.getResults();
		return resultQuery;
	}
	
	/*stampa eventuali risultati presenti*/
	public static void printResult(SolrDocumentList resultQuery) {
		System.out.println("==== Risultati ====");
		if(!resultQuery.isEmpty()) {
			Iterator<SolrDocument> iter = resultQuery.iterator();
			while (iter.hasNext()) {
				SolrDocument resultDoc = iter.next();
				String url =  (String) resultDoc.getFieldValue("url"); //uniqueField
				String title = (String) resultDoc.getFieldValue("title");
				String body = (String) resultDoc.getFieldValue("body");
				String html = (String) resultDoc.getFieldValue("html"); 
				System.out.println("\nURL: "+url+ "\nTITLE: "+title+"");
			}
		}
	}

}