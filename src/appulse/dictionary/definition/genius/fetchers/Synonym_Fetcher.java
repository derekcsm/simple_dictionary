package appulse.dictionary.definition.genius.fetchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import appulse.dictionary.definition.genius.objects.Synonym;
import appulse.simple.dictionary.DefinitionList;

public class Synonym_Fetcher extends AsyncTask<String, Void, ArrayList<Synonym>> {
	private DefinitionList mContext;
	private final String base_url = "http://api.wordnik.com:80/v4/word.json/";
	private final String definitions_url = "/relatedWords?useCanonical=true&relationshipTypes=synonym&limitPerRelationshipType=8";

	public Synonym_Fetcher(DefinitionList context) {
		this.mContext = context;
	}

	public ArrayList<Synonym> doInBackground(String... strings) {
		ArrayList<Synonym> synonyms = new ArrayList<Synonym>(0);
	

		try {
			URL url = new URL(base_url + strings[0] + definitions_url + "&api_key=" + "de46aea2a06a6bd33572d005afc01f025e0a2875bc6a089e8");
			URLConnection connection = url.openConnection();

			JSONArray response = new JSONArray(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());

			for (int i = 0; i < response.length(); i++) {
				JSONObject synonym = response.getJSONObject(i);

				JSONArray words = synonym.getJSONArray("words");
				for (int word = 0; word < words.length(); ++word) {
					synonyms.add(new Synonym(words.getString(word)));
				}
			}
		} catch (IOException e) {

		} catch (JSONException e) {

		}

		return synonyms;
	}

	@Override
	protected void onPostExecute(ArrayList<Synonym> sResult) {
		mContext.sAdapter.addItems(sResult);
	}
}
