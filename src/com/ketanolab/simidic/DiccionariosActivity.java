package com.ketanolab.simidic;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ketanolab.simidic.adapters.AdaptadorViewPager;
import com.ketanolab.simidic.util.Constants;
import com.viewpagerindicator.CirclePageIndicator;

public class DiccionariosActivity extends SherlockActivity {

	// Paginado
	private ViewPager viewPager;
	private CirclePageIndicator indicator;
	private AdaptadorViewPager adaptadorViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar_ForceOverflow);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diccionarios);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// Paginado
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		adaptadorViewPager = new AdaptadorViewPager(this);
		viewPager.setAdapter(adaptadorViewPager);
		indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(viewPager);
		// Fin Paginado
		// DiccionariosDbAdapter dbDiccionarios = new
		// DiccionariosDbAdapter(this);
		// dbDiccionarios.abrir();
		// Cursor cursor = dbDiccionarios.obtenerTodosDiccionarios();
		// if (cursor.moveToFirst()) {
		// do {
		// String nombre = cursor.getString(1);
		// String origen = cursor.getString(2);
		// String destino = cursor.getString(3);
		// String autor = cursor.getString(4);
		// String descripcion = cursor.getString(5);
		// adaptadorViewPager.adicionarItem(
		// R.drawable.ic_launcher_diccionario, origen + " - "
		// + destino, autor, descripcion);
		// } while (cursor.moveToNext());
		// }
		// cursor.close();
		// dbDiccionarios.cerrar();
		File directory = new File(Constants.PATH_DICTIONARIES);
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files.length > 0) {
				for (int i = 0; i < files.length; ++i) {
					if (files[i].getName().length() < 15) { // For files that
						// are not database
					SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(files[i], null);
					setDataDictionary(db);
					db.close();
					}
				}
			}
		}
	}

	private void setDataDictionary(SQLiteDatabase db) {
		Cursor cursor = db.rawQuery("SELECT author, language_begin, language_end, description FROM info", null);
		if (cursor.moveToFirst()) {
			String author = cursor.getString(0);
			String beginLanguage = cursor.getString(1);
			String endLanguage = cursor.getString(2);
			String description = cursor.getString(3);
			cursor.close();
			adaptadorViewPager.adicionarItem(R.drawable.img_dictionary, beginLanguage + " - " + endLanguage, author, description);
		}
		cursor.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return true;

	}

}
