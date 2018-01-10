package fr.mk.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import mk.fr.localsqlapp.model.Contact;

/**
 * Created by Formation on 10/01/2018.
 */

public class ContactDAO {

    private DatabaseHandler db;

    public ContactDAO(DatabaseHandler db) {
        this.db = db;
    }


    //Récuperation d'un contact en fonction de sa clef primaire

    public Contact findOneById(long id)throws SQLiteException {

        //Execution de la requête
        String[] params={String.valueOf(id)};
        String sql="SELECT id, name, firstName, email FROM contacts WHERE id=?";
        Cursor cursor = this.db.getReadableDatabase().rawQuery(sql, params);

        //Instanciation d'un contact
        Contact contact = new Contact();

        //Hydratation du contact
        if(cursor.moveToNext()){
            contact = hydrateContact(cursor);
        }

        //Fermeture du cursor
        cursor.close();

        return contact;
    }

    private Contact hydrateContact(Cursor cursor) {
        Contact contact = new Contact() ;
        contact.setId(cursor.getLong(0));
        contact.setName(cursor.getString(1));
        contact.setFirstName(cursor.getString(2));
        contact.setEmail(cursor.getString(3));

        return contact;
    }


    public List<Contact> findAll() throws SQLiteException{
        //Instanciation de la liste des contacts
    List<Contact> contactList = new ArrayList<>();

    // Execution de la requête sql
    String sql = "SELECT id, name, first_name, email FROM contacts";
    Cursor cursor = this.db.getReadableDatabase().rawQuery(sql, null);

    //Boucle sur le curseur
    while(cursor.moveToNext()){
        contactList.add(this.hydrateContact(cursor));
    }

    //fermeture du curseur
        cursor.close();
    return contactList;

}

}
