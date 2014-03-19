package appulse.simple.dictionary;

import java.util.Locale;

import uk.co.androidalliance.edgeeffectoverride.EdgeEffectListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import appulse.simple.dictionary.R;
import appulse.dictionary.definition.genius.adapters.DefinitionAdapter;
import appulse.dictionary.definition.genius.adapters.SynonymAdapter;
import appulse.dictionary.definition.genius.fetchers.DefinitionFetcher;
import appulse.dictionary.definition.genius.fetchers.Synonym_Fetcher;
import appulse.dictionary.definition.genius.fetchers.pronounciation_fetcher;

public class DefinitionList extends SherlockActivity implements TextToSpeech.OnInitListener{

	public DefinitionAdapter mAdapter;
	public SynonymAdapter sAdapter;
	private EdgeEffectListView mListView;
	private ListView bottomListView;
	TextView word;
	TextView pronounce;
	TextView bottomtitle;
	String definethis;
	String the_word;
	String failsafe;
	Typeface tf_b;
	Typeface tf_r;
	//Menu menu;
	MenuItem item;
	private TextToSpeech tts;
    AdView adView;

	public void onCreate(Bundle savedInstanceState) { //START OF ON CREATE!

		super.onCreate(savedInstanceState);
		//make a spinner in the actionbar and create the activity
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.definition_list_layout);
		setSupportProgressBarIndeterminateVisibility(true);
		
		//getting the searched word to a string
		the_word = getIntent().getStringExtra("WORD");
		
		
        //Async tasks go here
		getPronounciation(the_word);
		getDefinition(the_word);
		getSynonyms(the_word);
		
		// Look up the AdView as a resource and load a request.
	    adView = (AdView)this.findViewById(R.id.listadView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);

		//set the header and footer adapters
		this.mAdapter = new DefinitionAdapter(this);
		this.sAdapter = new SynonymAdapter(this);
		//customize the overscroll color for good measure
		this.mListView = (EdgeEffectListView) findViewById(R.id.definition_list);
		
		
		//Do some funky computer language gibberish here
		tts = new TextToSpeech(this, this);

		
		//initialize all things header
		View top = getLayoutInflater().inflate(R.layout.top, null);
		mListView.addHeaderView(top);
		top.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
						.setPrimaryClip(ClipData
								.newPlainText("word", the_word));
				Toast.makeText(DefinitionList.this, "copied word to clipboard",
						Toast.LENGTH_LONG).show();
			}
		});


		//BOOM fonts
		tf_b = Typeface.createFromAsset(getAssets(), "fonts/Georgia Bold.ttf");
		tf_r = Typeface.createFromAsset(getAssets(), "fonts/Georgia.ttf");
		Typeface tf_it = Typeface.createFromAsset(getAssets(),
				"fonts/Georgia Italic.ttf");
		Typeface mon_reg = Typeface.createFromAsset(getAssets(),
				"fonts/Montserrat-Regular.ttf");
		Typeface mon_bold = Typeface.createFromAsset(getAssets(),
				"fonts/Montserrat-Bold.ttf");

		word = (TextView) findViewById(R.id.top_word);
		word.setTypeface(tf_b);
		word.setText(getIntent().getStringExtra("WORD"));
		pronounce = (TextView) findViewById(R.id.top_pronounciation);
		pronounce.setTypeface(tf_it);
		
		

		
		//Set the main listview adapeter right here
		mListView.setAdapter(mAdapter);
		
		
		//Customize the actionbar
		int titleId = Resources.getSystem().getIdentifier("action_bar_title",
				"id", "android");
		if (titleId == 0
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
			titleId = R.id.abs__action_bar_title;
		}
		final TextView appName = (TextView) findViewById(titleId);
		appName.setTypeface(mon_reg);

		getSupportActionBar().setTitle(the_word);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportActionBar().setIcon(R.drawable.ic_find);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#43484A")));
		
//END OF ON CREATE!
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			//Shut down TTS!
			if (tts != null) {
	            tts.stop();
	            tts.shutdown();
	        }
			// Destroy the AdView.
		    if (adView != null) {
		      adView.destroy();
		    }
            return true;
        case R.id.speak_it:
        	speakOut();
            return true;
        case R.id.refresh_it:
        	refresh();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		item = menu.findItem(R.id.refresh_it);
		
		if (failsafe == "on"){
			item.setVisible(true);
		}
		else {
			item.setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	public void getDefinition(String word) {
		new DefinitionFetcher(this).execute(word);
	}

	public void getPronounciation(String word) {
		new pronounciation_fetcher(this).execute(word);
	}

	public void getSynonyms(String word) {
		new Synonym_Fetcher(this).execute(word);
	}

	public void refresh() {
		
		Intent intent = new Intent(DefinitionList.this, DefinitionList.class);
		intent.putExtra("WORD", the_word);
		DefinitionList.this.startActivity(intent);
		
		this.finish();
		//Async tasks go here
//		setSupportProgressBarIndeterminateVisibility(true);
//
//        mListView.invalidateViews();
//        mListView.refreshDrawableState();
//        
//				getPronounciation(the_word);
//				getDefinition(the_word);
//				getSynonyms(the_word);
//				AdRequest adRequest = new AdRequest.Builder().build();
//				adView.loadAd(adRequest);
//				mListView.setAdapter(mAdapter);
	}
	
	public void addSynonyms() {
		View bottom = getLayoutInflater().inflate(R.layout.bottom, null);
		this.bottomListView = (ListView) bottom.findViewById(R.id.list);

		TextView bottomtitle = (TextView) bottom
				.findViewById(R.id.txt_bottom_title);
		bottomtitle.setTypeface(tf_b);

		mListView.addFooterView(bottom);
		bottomListView.setAdapter(sAdapter);
	}
	
	//Create a failsafe
	public void createFailsafe() {
		View failcatcher = getLayoutInflater().inflate(R.layout.fail_catcher, null);
		this.bottomListView = (ListView) failcatcher.findViewById(R.id.list);
		
		failsafe="on";
		
		TextView shucks = (TextView) 
				failcatcher.findViewById(R.id.shucks);
		shucks.setTypeface(tf_r);
		
		BootstrapButton wikipedia = (BootstrapButton)
				failcatcher.findViewById(R.id.wikipedia_it);
		wikipedia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://en.wikipedia.org/wiki/" + the_word)));
			}
			
		});
		
		BootstrapButton google = (BootstrapButton)
				failcatcher.findViewById(R.id.google_it);
		google.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/#q=definition+of+" + the_word + "&safe=off")));
			}
			
		});

		mListView.addFooterView(failcatcher);
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
	
	@Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
     // Destroy the AdView.
	    if (adView != null) {
	      adView.destroy();
	    }
        super.onDestroy();
    }

	@Override
	public void onInit(int status) {
		 if (status == TextToSpeech.SUCCESS) {
			 
			    //Accent!
	            int result = tts.setLanguage(Locale.UK);
	 
	            if (result == TextToSpeech.LANG_MISSING_DATA
	                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	                Log.e("TTS", "This Language is not supported");
	            } else {
	            	//Do nothing here just chill shit's fine.
	                //btnSpeak.setEnabled(true);
	                //speakOut();
	            }
	 
	        } else {
	            Log.e("TTS", "Initilization Failed!");
	        }
	}
	
	private void speakOut() {
        tts.speak(the_word, TextToSpeech.QUEUE_FLUSH, null);
    }

}
