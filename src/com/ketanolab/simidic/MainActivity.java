package com.ketanolab.simidic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ketanolab.simidic.util.Constants;
import com.ketanolab.simidic.util.Util;

public class MainActivity extends SherlockActivity implements
		ActionBar.OnNavigationListener, OnItemClickListener {

	// Paths dictionaries
	private ArrayList<String> pathsDictionaries;

	// Navigation
	private int itemSelectedNavigation = 0;
	private ArrayAdapter<String> listNavigationAdapter;

	// List
	private ListView listView;
	private SimpleCursorAdapter simpleCursorAdapter;
	SQLiteDatabase db;

	// EditText
	private EditText cajaConsulta;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar_ForceOverflow);
		super.onCreate(savedInstanceState);
		Log.i(Constants.DEBUG, "onCreate()");
		setContentView(R.layout.activity_main);
		getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide title
		getSupportActionBar().setDisplayShowHomeEnabled(false); // Hide icon

		// Navigation
		Context context = getSupportActionBar().getThemedContext();
		listNavigationAdapter = new ArrayAdapter<String>(context,
				R.layout.sherlock_spinner_item);
		listNavigationAdapter
				.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(listNavigationAdapter,
				this);

		// List
		listView = (ListView) findViewById(R.id.lista);
		listView.setOnItemClickListener(this);

		cajaConsulta = (EditText) findViewById(R.id.caja_consulta);
		cajaConsulta.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().length() > 0) {
					simpleCursorAdapter.getFilter().filter(s.toString());
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(Constants.DEBUG, "onStart()");

		// Preferences
		// SharedPreferences settings = getSharedPreferences("simidic", 0);
		// int x = settings.getInt("dictionary", 0);
		// getSupportActionBar().setSelectedNavigationItem(x);

		Log.i(Constants.DEBUG, "Escaneando...");
		scanDictionaries(); // and put in navigation

		// Load words
		Log.i(Constants.DEBUG,
				"Cargando palabras... dics" + pathsDictionaries.size());
		if (pathsDictionaries.size() > 0) {
			if (db != null) {
				db.close();
			}
			db = SQLiteDatabase.openOrCreateDatabase(
					pathsDictionaries.get(itemSelectedNavigation), null);
			loadAllWords();
			Log.i(Constants.DEBUG, "> " + pathsDictionaries.size());
		}
		// Re-query
		String s = cajaConsulta.getText().toString();
		if (s.toString().length() > 0) {
			simpleCursorAdapter.getFilter().filter(s.toString());
		}
	}

	private void loadAllWords() {
		final String[] columns = { "_id", "word", "summary" };
		int[] to = { 0, R.id.word_item, R.id.meaning_item };
		// Cursor cursor = db.rawQuery("SELECT _id, word, summary FROM words",
		// null);
		Cursor cursor = db
				.query("words", columns, null, null, null, null, null);

		simpleCursorAdapter = new SimpleCursorAdapter(this,
				R.layout.word_list_item, cursor, columns, to, 0);

		simpleCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {

				String word = constraint.toString() + "%";
				Cursor cursor = null;
				if (pathsDictionaries.get(itemSelectedNavigation)
						.contains("gn")) {
					cursor = db
							.rawQuery(
									"SELECT _id, word, summary FROM words WHERE replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(word,'á','a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'(',''),')',''),'ñ','n'),'ï','i'),'ä','a'),'ë','e'),'ö','o'),'ü','u') LIKE ?",
									new String[] { word });
				} else {
					cursor = db
							.rawQuery(
									"SELECT _id, word, summary FROM words WHERE replace(replace(replace(replace(replace(replace(replace(word,'á','a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n'),'ä','a') LIKE ?",
									new String[] { word });
				}

				return cursor;
			}
		});

		listView.setAdapter(simpleCursorAdapter);

	}

	// protected void showWords(String word) {
	// SQLiteDatabase db =
	// SQLiteDatabase.openOrCreateDatabase(pathsDictionaries.get(currentNavigation),
	// null);
	// Cursor cursor = getWords(word, db);
	// Log.i(Constants.DEBUG, "Numero de cursor: " + cursor.getCount());
	// // loadWordsTask = new LoadWordsTask();
	// // loadWordsTask.execute(cursor);
	// db.close();
	// }

	// private void loadDefaultWords() {
	// if (pathsDictionaries.size() > 0) {
	// SQLiteDatabase db =
	// SQLiteDatabase.openOrCreateDatabase(pathsDictionaries.get(currentNavigation),
	// null);
	// Cursor cursor = db.rawQuery("SELECT value FROM words", null);
	// Log.i(Constants.DEBUG, "numero de cursor: " + cursor.getCount());
	// loadWordsTask = new LoadWordsTask();
	// loadWordsTask.execute(cursor);
	//
	// // putAllWords(db);
	// db.close();
	// }
	// }

	private void putDataDictionariesInNavigation() {
		listNavigationAdapter.clear();
		for (int i = 0; i < pathsDictionaries.size(); i++) {
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
					pathsDictionaries.get(i), null);
			try {
				String[] nameAndAuthor = Util.getNameAndAuthorDictionary(db);
				listNavigationAdapter.add(nameAndAuthor[0]); // ***
			} catch (Exception ex) {
				Log.e(Constants.DEBUG,
						"Error en la consulta a la base de datos. ---");
				File archivo = new File(pathsDictionaries.get(i));
				Log.i(Constants.DEBUG, "Delete: " + archivo.getAbsolutePath());
				archivo.delete();
				listNavigationAdapter.notifyDataSetChanged();
				onStart();
			}
			db.close();
		}
		listNavigationAdapter.notifyDataSetChanged();
	}

	private void scanDictionaries() {
		if (Util.checkExternalStorageAvailable()) {
			pathsDictionaries = new ArrayList<String>();
			// itemSelectedNavigation = 0;
			File directory = new File(Constants.PATH_DICTIONARIES);
			if (directory.exists()) {
				File[] files = directory.listFiles();
				Arrays.sort(files);
				// Collections.sort(files);
				if (files.length > 0) {
					int c = 0;
					for (int i = 0; i < files.length; i++) {
						Log.i(Constants.DEBUG, "FindAll " + files[i].getName());
						if (Util.checkFilenameDictionary(files[i].getName())) {
							Log.i(Constants.DEBUG, "Find " + files[i].getName());
							pathsDictionaries.add(files[i].getAbsolutePath());
							/*
							 * SQLiteDatabase db =
							 * SQLiteDatabase.openOrCreateDatabase(files[i],
							 * null); try { String[] nameAndAuthor =
							 * getNameAndAuthorDictionary(db);
							 * namesDictionaries.add(nameAndAuthor[0]);
							 * authorsDictionaries.add(nameAndAuthor[1]); }
							 * catch (Exception ex) { Log.e(Constants.DEBUG,
							 * "Error en la consulta a la base de datos."); }
							 * db.close();
							 */
							c++;
						}
					}
					if (c == 0) {
						Util.showAlertNoDictionaries(this);
					} else {
						putDataDictionariesInNavigation();
					}
				} else {
					Log.i(Constants.DEBUG, "No hay ningun archivo.");
					Util.showAlertNoDictionaries(this);
				}
			} else {
				Log.i(Constants.DEBUG, "No existe ruta.");
				Util.showAlertNoDictionaries(this);
			}
		} else {
			Log.i(Constants.DEBUG, "No hay SD.");
			Util.showAlertNoExternalStorage(this);
		}
	}

	public boolean onNavigationItemSelected(int position, long id) {
		// Toast.makeText(this, "" + namesDictionaries.get(position),
		// Toast.LENGTH_SHORT).show();
		// Update list

		itemSelectedNavigation = position;
		if (pathsDictionaries.size() > 0) {
			db = SQLiteDatabase.openOrCreateDatabase(
					pathsDictionaries.get(itemSelectedNavigation), null);
			loadAllWords();
		}

		// Re-query
		String word = cajaConsulta.getText().toString();
		if (word.length() > 0) {
			simpleCursorAdapter.getFilter().filter(word);
		}

		return true;
	}

	// MENU
	// ****************************************************************************************************
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
			return false;
		}
		switch (item.getItemId()) {
		case R.id.menu_download:
			Intent intentDescarga = new Intent(this, DescargaActivity.class);
			startActivity(intentDescarga);
			break;
		case R.id.menu_favorites:
			Intent intentFavoritos = new Intent(this, FavoritosActivity.class);
			startActivity(intentFavoritos);
			break;
		case R.id.menu_information:
			Intent intentCreditos = new Intent(this, CreditosActivity.class);
			startActivity(intentCreditos);
			break;
		case R.id.menu_dictionaries:
			Intent intentDiccionarios = new Intent(this,
					DiccionariosActivity.class);
			startActivity(intentDiccionarios);
			break;
		}
		return true;
	}

	// ****************************************************************************************************

	public void onItemClick(AdapterView<?> arg0, View v, int posicion, long id) {
		// Start PalabraActivity
		String word = ((TextView) v.findViewById(R.id.word_item)).getText()
				.toString();
		Intent intent = new Intent(this, PalabraActivity.class);
		// intent.putExtra("name", namesDictionaries.get(currentNavigation));
		// intent.putExtra("author",
		// authorsDictionaries.get(currentNavigation));
		intent.putExtra("path", pathsDictionaries.get(getSupportActionBar()
				.getSelectedNavigationIndex()));
		intent.putExtra("word", word);
		startActivity(intent);
	}

	// ****************************************************************************************************

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(Constants.DEBUG, "onStop()");
		// Preferences
		// SharedPreferences settings = getSharedPreferences("simidic", 0);
		// SharedPreferences.Editor editor = settings.edit();
		// editor.putString("name",
		// pathsDictionaries.get(getSupportActionBar().getSelectedNavigationIndex()));
		// editor.commit();

		if (simpleCursorAdapter != null) {
			simpleCursorAdapter.getCursor().close();
			simpleCursorAdapter = null;
		}
		if (db != null) {
			db.close();
		}
	}
}
