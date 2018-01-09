package mk.fr.localsqlapp;

import android.app.ActionBar;
import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import fr.mk.database.DatabaseHandler;

public class FormActivity extends AppCompatActivity {

    private EditText editTextNom;
    private EditText editTextPrenom;
    private EditText editTextEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);

        editTextNom = (EditText) findViewById(R.id.editTextNom);
        editTextPrenom = (EditText) findViewById(R.id.editTextPrenom);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

     /*  Initialisation pour test
        editTextNom.setText("Allen");
       editTextPrenom.setText("Barry");
        editTextEmail.setText("barry.allen@flash.com"); */

        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void onValid(View v){
        //Récuperation de la saisie de l'utilisateur
        String name = ((EditText) findViewById(R.id.editTextNom)).getText().toString();
        String firstName = ((EditText) findViewById(R.id.editTextPrenom)).getText().toString();
        String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();


        //Instanciation de la connexion à la base de données
        DatabaseHandler db = new DatabaseHandler(this);

        //définition des données à inserer
        ContentValues insertValues = new ContentValues();
        insertValues.put("name", name);
        insertValues.put("first_name", firstName);
        insertValues.put("email", email);

        //Insertion des données
       try {
           db.getWritableDatabase().insert("contacts", null, insertValues);
           Toast.makeText(this, "Insertion OK", Toast.LENGTH_SHORT).show();

           editTextNom.setText("");
           editTextPrenom.setText("");
           editTextEmail.setText("");

       }catch (SQLiteException ex){
            Log.e("SQL EXCEPTION", ex.getMessage());
       }


    }




}
