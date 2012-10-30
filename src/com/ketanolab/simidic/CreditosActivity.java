package com.ketanolab.simidic;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class CreditosActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar_ForceOverflow);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creditos);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		TextView urlKetanolab = (TextView) findViewById(R.id.urlKetanolab);
		urlKetanolab.setText("www.ketanolab.com");
		Linkify.addLinks(urlKetanolab, Linkify.ALL);
		
		TextView urlIllaa = (TextView) findViewById(R.id.urlIllaa);
		urlIllaa.setText("www.illa-a.org");
		Linkify.addLinks(urlIllaa, Linkify.ALL);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return true;
	}
}