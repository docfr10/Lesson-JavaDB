package com.example.lesson_javadb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lesson_javadb.db.Contact;
import com.example.lesson_javadb.db.DatabaseHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText nameInput = findViewById(R.id.name_input);
        EditText phoneInput = findViewById(R.id.phone_input);
        Button saveButton = findViewById(R.id.save_button);
        Button deleteButton = findViewById(R.id.delete_button);
        Button findButton = findViewById(R.id.find_button);
        RecyclerView contactsList = findViewById(R.id.contacts_list);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Contact> contacts = dbHelper.getAllContacts();

        ContactAdapter adapter = new ContactAdapter(contacts);
        contactsList.setLayoutManager(new LinearLayoutManager(this));
        contactsList.setAdapter(adapter);

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String phone = phoneInput.getText().toString();
            if (dbHelper.addContact(new Contact(0, name, phone))) {
                contacts.add(new Contact(0, name, phone)); // Просто добавляем в список для отображения
                adapter.notifyItemInserted(contacts.size() - 1);
                Toast.makeText(this, "Contact saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save contact", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString();
            if (dbHelper.deleteContact(phone)) {
                int position = -1;
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).getPhone().equals(phone)) {
                        position = i;
                        contacts.remove(i);
                        break;
                    }
                }
                if (position != -1) {
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Contact deleted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to delete contact", Toast.LENGTH_SHORT).show();
            }
        });

        findButton.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString();
            Contact foundContact = dbHelper.findContact(phone);
            if (foundContact != null) {
                nameInput.setText(foundContact.getName());
                phoneInput.setText(foundContact.getPhone());
                Toast.makeText(this, "Contact found: " + foundContact.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
            }
        });

        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {
            String oldPhone = phoneInput.getText().toString(); // Считаем что это старый номер для поиска
            String newName = nameInput.getText().toString(); // Новое имя для обновления
            String newPhone = phoneInput.getText().toString(); // Новый номер для обновления

            if (dbHelper.updateContact(oldPhone, newName, newPhone)) {
                Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show();
                // Обновляем список и адаптер
                refreshContactsList(dbHelper, contacts, adapter, contactsList);
            } else {
                Toast.makeText(this, "Failed to update contact", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Метод для обновления списка контактов после изменения в базе данных
    private void refreshContactsList(DatabaseHelper dbHelper, List<Contact> contacts, ContactAdapter adapter, RecyclerView contactsList) {
        contacts = dbHelper.getAllContacts(); // Загружаем обновленный список
        adapter = new ContactAdapter(contacts);
        contactsList.setAdapter(adapter);
    }
}
