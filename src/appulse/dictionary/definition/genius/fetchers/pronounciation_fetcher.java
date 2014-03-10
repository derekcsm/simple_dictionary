package appulse.dictionary.definition.genius.fetchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import android.os.AsyncTask;
import android.widget.TextView;
import appulse.simple.dictionary.R;
import appulse.simple.dictionary.DefinitionList;

public class pronounciation_fetcher extends AsyncTask<String, Void, String> {

	private DefinitionList mContext;
	private final String base_url = "http://api.wordnik.com:80/v4/word.json/";
	private final String definitions_url = "/pronunciations?useCanonical=false&typeFormat=ahd&limit=1&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";

	public pronounciation_fetcher(DefinitionList context) {
		this.mContext = context;
	}

	public String doInBackground(String... strings) {
		// ArrayList<Pronounciation> pronounciations = new
		// ArrayList<Pronounciation>();

		try {
			URL url = new URL(base_url + strings[0] + definitions_url);
			URLConnection connection = url.openConnection();

			JSONArray response = new JSONArray(
					new BufferedReader(new InputStreamReader(
							connection.getInputStream())).readLine());

			if (response.length() != 0)
				return response.getJSONObject(0).getString("raw");

		} catch (IOException e) {

		} catch (JSONException e) {

		}

		return null;

	}

	@Override
	protected void onPostExecute(String result) {

		TextView txt = (TextView) mContext
				.findViewById(R.id.top_pronounciation);
		txt.setText(result);
	}
}

