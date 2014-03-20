package appulse.simple.dictionary;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import appulse.simple.dictionary.R;
import appulse.dictionary.definition.genius.objects.Definition;

import me.grantland.widget.AutofitTextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Define extends SherlockActivity {

	EditText define;
	BootstrapButton search_btn;
	TextView wod;
	TextView type;
	AutofitTextView word;
	TextView definition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// make a spinner in the actionbar and create the activity
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.define_layout);

		getSupportActionBar().setIcon(R.drawable.ic_search);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#43484A")));

		Typeface tf_b = Typeface.createFromAsset(getAssets(),
				"fonts/Georgia Bold.ttf");
		Typeface tf_it = Typeface.createFromAsset(getAssets(),
				"fonts/Georgia Italic.ttf");
		Typeface mon_reg = Typeface.createFromAsset(getAssets(),
				"fonts/Montserrat-Regular.ttf");
		Typeface tf_r = Typeface.createFromAsset(getAssets(),
				"fonts/Georgia.ttf");

		define = (EditText) findViewById(R.id.define_card_input);
		define.setTypeface(tf_b);
		
		define.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				getDefinition();
				return false;
			}
		});


		search_btn = (BootstrapButton) findViewById(R.id.search_button);
		search_btn.setLeftIcon("fa-search");

		wod = (TextView) findViewById(R.id.wod);
		wod.setTypeface(mon_reg);

		word = (AutofitTextView) findViewById(R.id.word);
		word.setTypeface(tf_b);

		type = (TextView) findViewById(R.id.type);
		type.setTypeface(tf_it);

		definition = (TextView) findViewById(R.id.definition);
		definition.setTypeface(tf_r);

		int titleId = Resources.getSystem().getIdentifier("action_bar_title",
				"id", "android");
		if (titleId == 0
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
			titleId = R.id.abs__action_bar_title;
		}

		final TextView appName = (TextView) findViewById(titleId);
		appName.setTypeface(mon_reg);

		Intent intent = getIntent();
		if (intent.getAction().equals(Intent.ACTION_SEND)
				&& intent.getType() != null)
			handleIntent(intent);

		((BootstrapButton) findViewById(R.id.search_button))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						getDefinition();
					}
				});

		getWordOfDay();
	}

	public void handleIntent(Intent intent) {
		if (intent.getType().equals("text/plain"))
			getDefinition(intent.getStringExtra(Intent.EXTRA_TEXT));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void getWordOfDay() {

		findViewById(R.id.word_of_day_container).setVisibility(View.INVISIBLE);
		setSupportProgressBarIndeterminateVisibility(true);

		new AsyncTask<Void, Void, Definition>() {
			String base_url = "http://api.wordnik.com/v4/words.json/wordOfTheDay";

			public Definition doInBackground(Void... voids) {
				try {
					URL url = new URL(
							base_url
									+ "?api_key="
									+ "de46aea2a06a6bd33572d005afc01f025e0a2875bc6a089e8");
					URLConnection connection = url.openConnection();

					JSONObject response = new JSONObject(
							new BufferedReader(new InputStreamReader(
									connection.getInputStream())).readLine());
					return new Definition(response.getString("word"), response
							.getJSONArray("definitions").getJSONObject(0)
							.getString("partOfSpeech"), response
							.getJSONArray("definitions").getJSONObject(0)
							.getString("text"));
				} catch (IOException e) {
				} catch (JSONException e) {
				}

				return null;
			}

			@Override
			protected void onPostExecute(final Definition result) {

				if (result != null) {
					setSupportProgressBarIndeterminateVisibility(false);
					((TextView) findViewById(R.id.word)).setText(result.word);
					((TextView) findViewById(R.id.type)).setText(result.type);
					((TextView) findViewById(R.id.definition))
							.setText(result.definition);
					findViewById(R.id.word_of_day_container).setVisibility(
							View.VISIBLE);

					findViewById(R.id.word_of_day_container)
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View view) {
									getDefinition(result.word);
								}
							});
				}

				if (result == null) {
					setSupportProgressBarIndeterminateVisibility(false);
					word.setText("No network connection");
					type.setText("Touch to refresh.");
					// definition.setText("TOUCH TO REFRESH");
					findViewById(R.id.word_of_day_container).setVisibility(
							View.VISIBLE);

					findViewById(R.id.word_of_day_container)
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View view) {
									getWordOfDay();
								}
							});
				}

			}
		}.execute();
	}

	public void getDefinition() {
		getDefinition(((TextView) findViewById(R.id.define_card_input))
				.getText().toString());
	}

	public void getDefinition(String word) {
		Intent intent = new Intent(this, DefinitionList.class);
		intent.putExtra("WORD", word);
		startActivity(intent);
	}

}
