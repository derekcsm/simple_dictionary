package appulse.dictionary.definition.genius.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import appulse.dictionary.definition.genius.objects.Synonym;
import appulse.simple.dictionary.DefinitionList;
import appulse.simple.dictionary.R;

public class SynonymAdapter extends BaseAdapter {
	ArrayList<Synonym> mSynonyms;
	Context mContext;

	public SynonymAdapter(Context context) {
		this.mContext = context;
		this.mSynonyms = new ArrayList<Synonym>();
	}

	public void addItems(ArrayList<Synonym> newItems) {
		mSynonyms.addAll(newItems);
		if(mSynonyms.size()>0){
			((DefinitionList) mContext).addSynonyms();
		}
		
		notifyDataSetChanged();
	}

	public void clear() {
		mSynonyms.clear();
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public Synonym getItem(int i) {
		return this.mSynonyms.get(i);
	}

	@Override
	public int getCount() {
		return this.mSynonyms.size();
	}

	@Override
	public View getView(final int u, View view, ViewGroup viewGroup) {
		
		Typeface tf_reg = Typeface.createFromAsset(mContext.getAssets(), "fonts/Georgia.ttf");
		Typeface tf_b = Typeface.createFromAsset(mContext.getAssets(), "fonts/Georgia Bold.ttf");
		
		View sView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
		TextView txt_synonym = ((TextView) sView.findViewById(R.id.text));
		txt_synonym.setTypeface(tf_reg);

		((TextView) sView.findViewById(R.id.text)).setText(getItem(u).synonym);

		
		sView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(mContext, DefinitionList.class);
				intent.putExtra("WORD", getItem(u).synonym);
				mContext.startActivity(intent);
				
			}
		});
		
		return sView;
		

	}

}
