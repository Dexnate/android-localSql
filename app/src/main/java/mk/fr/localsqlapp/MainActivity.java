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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reference au widget ListView sur le layout
         contactListView = findViewById(R.id.contactListView);

         contactListInit();


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
        startActivity(formIntent);

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

                break;
        }


        return true;
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



}
