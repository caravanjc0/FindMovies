package com.jccr;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class FindMovies {
	
	private static String HACKER_RANK_BASE_URL = "https://jsonmock.hackerrank.com/api/movies/search";
	private static String TITLE = "Title";
	private static String PAGE = "page";
	private static String SLASH_TOKEN = "/";
	private static String QUESTION_MARK_TOKEN = "?";
	private static String EQUALS_TOKEN = "=";
	private static String AMPERSAND_TOKEN = "&";
	private static String TOTAL_PAGES_ELEMENT = "total_pages";
	private static String DATA_ELEMENT = "data";
	private static String NUMBER_ZERO = "0";
	
	public static void main(String[] args) {
		FindMovies findMovies = new FindMovies();
		String word;
		
		System.out.println("This program searches movies titles containing a given word in a Hackerrank mock API");
		
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.print("\nType a word and press Enter, press 0 to exit: ");
				word = scanner.nextLine();
				
				if (word.equals(NUMBER_ZERO)) break;
				
				System.out.println("Searching...");
				
				List<String> movies = findMovies.findByWord(word);
				
				System.out.println("Results:");
				
				if (movies.isEmpty()) {
					System.out.println("No movies found :(");
				}
				else {
					movies.forEach(x -> System.out.println(String.format("- %s", x)));
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("End of program");
		System.exit(0);
	}
	
	public List<String> findByWord(String word) {
		List<String> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		String fullURL;
		JSONObject rootElement;
		JSONArray jsonArray;
		int totalPages;
		
		sb.append(HACKER_RANK_BASE_URL).append(SLASH_TOKEN).append(QUESTION_MARK_TOKEN).append(TITLE).append(EQUALS_TOKEN).append(word.toLowerCase());
		
		fullURL = sb.toString();
		
		try {
			rootElement = getJSONfromURL(fullURL);
			
			totalPages = rootElement.getInt(TOTAL_PAGES_ELEMENT);
			
			for (int i = 1; i <= totalPages; i++) {
				List<String> titlesList;
				StringBuilder sb1 = new StringBuilder(fullURL);
				
				sb1.append(AMPERSAND_TOKEN).append(PAGE).append(EQUALS_TOKEN).append(i);
				rootElement = getJSONfromURL(sb1.toString());
				jsonArray = rootElement.getJSONArray(DATA_ELEMENT);
				Stream<Object> stream = StreamSupport.stream(jsonArray.spliterator(), false);

				titlesList = stream.map(element -> ((JSONObject)element).get(TITLE).toString()).collect(Collectors.toList());

				list.addAll(titlesList);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public JSONObject getJSONfromURL(String url) throws URISyntaxException {
		JSONObject json = null;
		URI uri = new URI(url);
		HttpGet request = new HttpGet();
		
		request.setURI(uri);
		
		try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(request)) {
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				String result = EntityUtils.toString(entity);
			
				json = new JSONObject(result);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return json;
	}

}
