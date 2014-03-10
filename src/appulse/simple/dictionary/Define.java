package appulse.simple.dictionary;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import appulse.simple.dictionary.R;
import appulse.dictionary.definition.genius.objects.Definition;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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
	TextView word;
	TextView definition;
	AdView adView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		
		// Look up the AdView as a resource and load a request.
	    adView = (AdView)this.findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);

		define = (EditText)findViewById(R.id.define_card_input);
		define.setTypeface(tf_b);
		
		search_btn = (BootstrapButton)findViewById(R.id.search_button);
		search_btn.setLeftIcon("fa-search");
		//search_btn.setTypeface(tf_r);
		
		wod = (TextView)findViewById(R.id.wod);
		wod.setTypeface(mon_reg);
		
		word = (TextView)findViewById(R.id.word);
		word.setTypeface(tf_b);
		
		word = (TextView)findViewById(R.id.word);
		word.setTypeface(tf_b);
		
		type = (TextView)findViewById(R.id.type);
		type.setTypeface(tf_it);
		
		definition = (TextView)findViewById(R.id.definition);
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

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	  public void onResume() {
	    super.onResume();
	    if (adView != null) {
	      adView.resume();
	    }
	  }

	  @Override
	  public void onPause() {
	    if (adView != null) {
	      adView.pause();
	    }
	    super.onPause();
	  }
	
	/** Called before the activity is destroyed. */
	  @Override
	  public void onDestroy() {
	    // Destroy the AdView.
	    if (adView != null) {
	      adView.destroy();
	    }
	    super.onDestroy();
	  }

	public void getWordOfDay() {
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
				((TextView) findViewById(R.id.word)).setText(result.word);
				((TextView) findViewById(R.id.type)).setText(result.type);
				((TextView) findViewById(R.id.definition))
						.setText(result.definition);
				findViewById(R.id.word_of_day_container).setVisibility(
						View.VISIBLE);

				findViewById(R.id.word_of_day_container).setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View view) {
								getDefinition(result.word);
							}
						});
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
