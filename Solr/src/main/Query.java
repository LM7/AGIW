package main;
import java.applet.Applet;
import java.io.IOException;
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
public class Query extends Applet{
	static HttpSolrServer server = new HttpSolrServer("http://localhost:8080/collection1/");
	static SolrQuery query = new SolrQuery();
	
	public static void main(String[] args) throws SolrServerException, IOException {
		String[] r = doSearch("soup sausage");
		System.out.println("RISULTATI IN AND: "+r[0]+"\n");
		System.out.println("RISULTATI IN OR: "+r[1]+"\n");
		System.out.println("QUERY: "+r[2]);
	}
	
	public static String[] doSearch(String keywords) throws SolrServerException {

		/* 
		 * result[0]= risultati in AND
		 * result[1]= risultati in OR
		 * result[2]= eventuale query se Mispelling
		 */
		String[] result = new String[3];
	
		//System.out.println("\n----------------------------------------------------------------------------------------------------------------");
		//System.out.println("RISULTATI con QUERY ORIGINALE");
		QueryResponse resultQueryQ1 = doQuery(keywords,"AND");
		SolrDocumentList q1List = resultQueryQ1.getResults();
		//System.out.println("== AND ===");
		//printResult(q1List);
		//System.out.println("\n-----------------------------------------------------------");
		QueryResponse resultQueryQ3 = doQuery(keywords,"OR");
		SolrDocumentList q3List = resultQueryQ3.getResults();
		//System.out.println("== OR ===");
		//printResult(q3List);
		//System.out.println("\n----------------------------------------------------------------------------------------------------------------");
		//System.out.println("RISULTATI con QUERY MODIFICATA DAL MISPELLING");
		QueryResponse resultQueryQ2 = checkMispelling(keywords, "AND");
		SolrDocumentList q2List = resultQueryQ2.getResults();
		//System.out.println("== AND ===");
		//printResult(q2List);
		//System.out.println("\n-----------------------------------------------------------");
		QueryResponse resultQueryQ4 = checkMispelling(keywords, "OR");
		SolrDocumentList q4List = resultQueryQ4.getResults();
		//System.out.println("== OR ===");
		//printResult(q4List);
		//System.out.println("\n----------------------------------------------------------------------------------------------------------------");
		
		if((q1List.size()+q3List.size())>=(q2List.size()+q4List.size())) {
			result[0]=resultQueryQ1.toString();
			result[1]=resultQueryQ3.toString();
			result[2]="";
		}
		else {
			result[0]=resultQueryQ2.toString();
			result[1]=resultQueryQ4.toString();
			result[2]="";
		}
		return result;
	}

	public static QueryResponse checkMispelling(String keywords, String operator) {
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
			//System.out.println("FORSE CERCAVI: \n");
			for (Suggestion suggestion : response.getSpellCheckResponse().getSuggestions()) {
			//	System.out.println("original token: " + suggestion.getToken() + " - alternatives: " + suggestion.getAlternatives());
				alternativesForWord = suggestion.getAlternatives();
				keywords = keywords.replaceAll(suggestion.getToken(), alternativesForWord.get(0));
			}
		}
		QueryResponse resultQuery = doQuery(keywords,operator);
		return resultQuery;
	}
	
	public static QueryResponse doQuery(String keywords, String operator) {
		String searchString="";

		if(!keywords.contains(" ")) {
			searchString = keywords;
		}
		else {
			searchString = keywords.replaceAll(" ", " "+operator+" ");
		}
		//System.out.println("\nSEARCH STRING: "+searchString);
		query.setQuery(searchString);

		QueryResponse rsp = null;
		try {
			rsp = server.query(query);
			//System.out.println("SERVER RESPONSE: "+rsp);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//SolrDocumentList resultQuery = rsp.getResults();
		return rsp;
	}
	
	/*stampa eventuali risultati presenti*/
	public static void printResult(SolrDocumentList resultQuery) {
		System.out.println("---> Risultati <---");
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