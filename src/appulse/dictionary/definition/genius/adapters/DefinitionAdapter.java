package appulse.dictionary.definition.genius.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.jseb.define.R;
import appulse.dictionary.definition.genius.objects.Definition;
import appulse.simple.dictionary.CustomTouchListener;
import appulse.simple.dictionary.DefinitionList;
import appulse.simple.dictionary.R;

import java.util.ArrayList;


public class DefinitionAdapter extends BaseAdapter {
	ArrayList<Definition> mDefinitions;
	Context mContext;

	public DefinitionAdapter(Context context) {
		this.mContext = context;
		this.mDefinitions = new ArrayList<Definition>();
	}

	public void addItems(ArrayList<Definition> newItems) {
		mDefinitions.addAll(newItems);
		if(mDefinitions.size() == 0){
			((DefinitionList) mContext).createFailsafe();
		}
		notifyDataSetChanged();
	}

	public void clear() {
		mDefinitions.clear();
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public Definition getItem(int i) {
		return this.mDefinitions.get(i);
	}

	@Override
	public int getCount() {
		return this.mDefinitions.size();
	}

	@Override
	public View getView(final int i, View view, ViewGroup viewGroup) {

		
		View mView = LayoutInflater.from(mContext).inflate(R.layout.definition_card, null);
		TextView type = ((TextView) mView.findViewById(R.id.type));
		TextView definition = ((TextView) mView.findViewById(R.id.definition));
		Typeface tf_reg = Typeface.createFromAsset(mContext.getAssets(), "fonts/Georgia.ttf");
		Typeface tf_it = Typeface.createFromAsset(mContext.getAssets(), "fonts/Georgia Italic.ttf");
		type.setTypeface(tf_it);
		definition.setTypeface(tf_reg);
		((TextView) mView.findViewById(R.id.type)).setText(getItem(i).type);
		((TextView) mView.findViewById(R.id.definition)).setText(getItem(i).definition);

		mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				View mView = LayoutInflater.from(mContext).inflate(R.layout.definition_options, null);
				((TextView) mView.findViewById(R.id.definition)).setText(getItem(i).definition);

				new AlertDialog.Builder(mContext).setNegativeButton("copy word", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						((ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("word", getItem(i).word));
						Toast.makeText(mContext, "copied word to clipboard", Toast.LENGTH_LONG).show();
					}
				}).setPositiveButton("copy definition", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						((ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("definition", getItem(i).definition));
						Toast.makeText(mContext, "copied definition to clipboard", Toast.LENGTH_LONG).show();
					}
				}).setView(mView).create().show();
			}
		});

		return mView;
	}
	

}
