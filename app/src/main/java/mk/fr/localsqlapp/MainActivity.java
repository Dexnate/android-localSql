package mk.fr.localsqlapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.mk.database.DatabaseHandler;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

  private   ListView contactListView;
  private List<Map<String, String>> contactList;
  private Integer selectedIndex;
  private Map<String, String> selectedPerson;
  private final String LIFE_CYCLE = "cycle de vie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LIFE_CYCLE, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reference au widget ListView sur le layout
         contactListView = findViewById(R.id.contactListView);

         contactListInit();

         //récuperation des données persistées dans le bundle onSaveinstanceState
        if(savedInstanceState != null){
            //récupération de l'index de selection sauvegardé
            this.selectedIndex = savedInstanceState.getInt("selectedIndex");
            if(this.selectedIndex != null){
                this.selectedPerson=this.contactList.get(this.selectedIndex);
                contactListView.requestFocusFromTouch();
                contactListView.setSelection(this.selectedIndex);
            }
        }


    }

    private void contactListInit() {
        //Recuperation de la liste des contacts
        contactList = this.getAllContacts();

        //Création d'un contactArrayAdapter
        ContactArrayAdapter contactAdapter = new ContactArrayAdapter(this, contactList);

        //Definition de l'adapter de notre listView
        contactListView.setAdapter(contactAdapter);

        //definition d'un écouteur d'évenement pour onItemclick
        contactListView.setOnItemClickListener(this);
    }

    /**
     * lancement de l'activité formulaire en appuyant sur le bouton
     * @param view
     */

    public void onAddContact(View view) {
        Intent formIntent = new Intent(this, FormActivity.class);
        startActivityForResult(formIntent,1);

    }

    private List<Map<String, String>> getAllContacts() {
        //Instanciation de la connexion à la base de données
        DatabaseHandler db = new DatabaseHandler(this);

        //Execution de la requête de selection
        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT name, first_name, email, id FROM contacts", null);

        //Instanciation de la liste qui recevra les données
        List<Map<String, String>> contactList=new ArrayList<>();


        //Parcours du curseur
        while (cursor.moveToNext()){
            Map<String, String> contactCols = new HashMap<>();
            contactCols.put("name", cursor.getString(0));
            contactCols.put("firstName", cursor.getString(1));
            contactCols.put("email", cursor.getString(2));
            contactCols.put("id", cursor.getString(3));

            //Ajout du map à la list
            contactList.add(contactCols);
        }

        return contactList;
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.selectedIndex=position;
        this.selectedPerson=this.contactList.get(position);
        Toast.makeText(this, "Contact sélectionné: "+selectedPerson.get("name")+" "+selectedPerson.get("firstName"), Toast.LENGTH_SHORT).show();
    }


    @Override
    //Creation d'un menu d'option
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Ajout des entrées du fichier main_option_menu
        au menu contextuel de l'activité*/
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mainMenuOptionDelete:
                this.deleteSelectedContact();
                break;
            case R.id.mainmenuOptionEdit:
                //Creation d'une intention
               Intent modifyIntent = new Intent(this, FormActivity.class);
               if(this.selectedIndex != null){
                   //Passage des paramètres à l'intention
                   modifyIntent.putExtra("id",this.selectedPerson.get("id"));
                   modifyIntent.putExtra("firstName",this.selectedPerson.get("firstName"));
                   modifyIntent.putExtra("name",this.selectedPerson.get("name"));
                   modifyIntent.putExtra("email",this.selectedPerson.get("email"));

                   //Lancement de l'activité FormActivity
                   startActivityForResult(modifyIntent, 1);
               }

                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == RESULT_OK){
            Toast.makeText(this, "Mise à jour effectuée", Toast.LENGTH_SHORT).show();

            //Réinitialisation de l'affichage
            this.contactListInit();
        }

    }

    //Methode de suppression de contact selectionné
    private void deleteSelectedContact(){
        //Suppression uniquement si un contact est sélectionné
        if (this.selectedIndex != null){
            try{
                //Définition de la requête sql et des paramètres
                String sql="DELETE FROM contacts WHERE id=?";
                String [] params = {this.selectedPerson.get("id")};
                //Execution de la requête
                DatabaseHandler db = new DatabaseHandler(this);
                db.getWritableDatabase().execSQL(sql, params);

                //réinitiamisation de la liste des contacts
                this.contactList = this.getAllContacts();
                contactListInit();
            }catch (SQLiteException ex){
                Toast.makeText(this,"Impossible de supprimer", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,"Veuillez selectionner un contact", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LIFE_CYCLE, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LIFE_CYCLE, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LIFE_CYCLE, "onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LIFE_CYCLE, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LIFE_CYCLE, "onDestroy");
    }

    //persistance des données avant la destruction de l'activité
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selectedIndex", this.selectedIndex);
        super.onSaveInstanceState(outState);
    }
}
