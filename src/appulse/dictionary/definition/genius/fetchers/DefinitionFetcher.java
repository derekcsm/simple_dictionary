package appulse.dictionary.definition.genius.fetchers;

import android.os.AsyncTask;

//import appulse.dictionary.definition.genius.DictionaryAPI;
import appulse.dictionary.definition.genius.objects.Definition;
import appulse.simple.dictionary.DefinitionList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class DefinitionFetcher extends AsyncTask<String, Void, ArrayList<Definition>> {
	private DefinitionList mContext;
	private final String base_url = "http://api.wordnik.com:80/v4/word.json/";
	private final String definitions_url = "/definitions?limit=12&includeRelated=true&sourceDictionaries=wiktionary,webster,century,wordnet,ahd&useCanonical=true&includeTags=false";

	public DefinitionFetcher(DefinitionList context) {
		this.mContext = context;
	}

	public ArrayList<Definition> doInBackground(String... strings) {
		ArrayList<Definition> definitions = new ArrayList<Definition>();
	

		try {
			URL url = new URL(base_url + strings[0] + definitions_url + "&api_key=" + "de46aea2a06a6bd33572d005afc01f025e0a2875bc6a089e8");
			URLConnection connection = url.openConnection();

			JSONArray response = new JSONArray(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());

			for (int i = 0; i < response.length(); i++) {
				JSONObject definition = response.getJSONObject(i);

				definitions.add(new Definition(definition.getString("word"), definition.getString("partOfSpeech"), definition.getString("text")));
			}
		} catch (IOException e) {

		} catch (JSONException e) {

		}

		return definitions;
	}

	@Override
	protected void onPostExecute(ArrayList<Definition> result) {
		mContext.mAdapter.addItems(result);
		mContext.setSupportProgressBarIndeterminateVisibility(false);
	}
}
