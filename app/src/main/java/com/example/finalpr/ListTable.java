package com.example.finalpr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
public class ListTable extends AppCompatActivity {
    public static List<GameObject> dbGamesList;
    FirebaseHelper fbh;
    Button back,go,delete, edit; // כפתור אחורה, להכנס למשחק, למחוק את המשחק
    Intent  out,gamePage, editGamePage; // אינטנט ליציאה מהדף
    TextView selectedGame; // הצגה של המשחק שנבחר
    int selctedID; // מיקום האינדקס שנבחר
    ListView lv; // רשימה של כל המשחקים
    ArrayList<String> game_description,site_comments;
    boolean isGameSeleted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_table);
        // מאתחל משתנים
        isGameSeleted = false;
        fbh =  new FirebaseHelper("Games");
        back = findViewById(R.id.back1);
        go = findViewById(R.id.gotosite);
        delete = findViewById(R.id.editsite);
        edit = findViewById(R.id.editsite3);
        selectedGame = findViewById(R.id.textView3);
        out = new Intent(this,EnterAppPage.class);
        out.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        gamePage = new Intent(this,GameObjectPage.class);
        gamePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        editGamePage = new Intent(this,AdminPageEdit.class);
        editGamePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        game_description = new ArrayList<>();
        site_comments = new ArrayList<>();
        lv = findViewById(R.id.lv);
        // מושך ממאגר הנתונים את כל המשחקים ל 2 רשימות,
        // רשימת אובייקט משחק ורשימת מחרוזת של שמות להציג בlistView
        dbGamesList = FirebaseHelper.convertToGamesList();
        ArrayList<String>  games_names = new ArrayList<>();
        ArrayList<GameObject>  games = new ArrayList<>();
        if(!dbGamesList.isEmpty())
            if(dbGamesList != null)
     for (int i =0; i < dbGamesList.size();i++)
     {
         if(dbGamesList.get(i) != null) {
             games.add(dbGamesList.get(i));
             games_names.add(dbGamesList.get(i).getName());
         }
     }
        delete.setVisibility(View.INVISIBLE);
        edit.setVisibility(View.INVISIBLE);

        selectedGame.setText("There are currently " + FirebaseHelper.fromDBListGames.size() + " games to view");
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,games_names);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selctedID=i;// מציב את האינדקס שנבחר מהרשימה
                selectedGame.setText("Selected game is:\n"+ games.get(i).getName()); // משנה את הטקסט להצגה של המשחק שנבחר במסך
                if(LoggedInUser.loggedUser.getAccessLevel() ==1 && !isGameSeleted)
                {
                    delete.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    isGameSeleted = true;
                }
            }
        });
        lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selctedID=position;// מציב את האינדקס שנבחר מהרשימה
               selectedGame.setText("Selected game is:\n"+ dbGamesList.get(position)); // משנה את הטקסט להצגה של האתר שנבחר במסך
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override // עובר למשחק
            public void onClick(View v) {
                if(games.size() != 0) // אם יש לפחות משחק אחד
                {
                try {
                    // מעביר נתונים אודות המשחק שנבחר לעמוד הבא בהתאמה
                    gamePage.putExtra("gameKeyIndex",selctedID);
                    gamePage.putExtra("gameName",games.get(selctedID).getName());
                    gamePage.putExtra("gameDescription",games.get(selctedID).getDescription());
                    gamePage.putExtra("gameURLimage",games.get(selctedID).getGameImageURL());
                    // תגובות לא בשימוש
                    gamePage.putExtra("gameComments",games.get(selctedID).getComments());
                    startActivity(gamePage);
                }
               catch (Exception e)
               {// במקרה של שגיאה כלשהי מציג הודעה בהתאם
                   Toast.makeText(getApplicationContext(),"Error, check game URL, if not vaild edit to a valid one and try again",Toast.LENGTH_LONG).show();
               }
            }
                else // אם אין משחקים בכלל מציג הודעה בהתאם
                Toast.makeText(getApplicationContext(), "list is empty no game selected",Toast.LENGTH_SHORT).show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(FirebaseHelper.fromDBListGames.size() !=0)
                            confirmDialog();
                        else
                            Toast.makeText(getApplicationContext(), "list is empty no game selected",Toast.LENGTH_SHORT).show();
                    }
                });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editGamePage.putExtra("gameKeyIndex",selctedID);
                startActivity(editGamePage);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(out);
            }
        });
   }
    // דיאלוג שאלה אם למחוק את המשחק שנבחר
    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + dbGamesList.get(selctedID).getName());
        builder.setMessage("Are you sure you want to delete " +  dbGamesList.get(selctedID).getName() + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseHelper.deleteGame(selctedID);
                startActivity(out);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public static class inputGame extends AppCompatActivity {

        FirebaseHelper fbh;
        EditText name , url,urlImage, desc, linkDisplay; // הטקסט שהוכנס על ידי האדמין, שם, ,תיאור וכתובת
        Button back , insert; // כפתורים חזרה למסך הראשי או הכנסת המשחק למאגר הנתונים
        Intent  out;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_input_site);

            //אתחול משתנים
            name = findViewById(R.id.name2);
            url = findViewById(R.id.url2);
            urlImage = findViewById(R.id.url4);
            back = findViewById(R.id.back);
            insert = findViewById(R.id.Insert);
            desc = findViewById(R.id.description);
            linkDisplay = findViewById(R.id.linkdisplayName);

            // הפניה לרפרנס של ה path למשחקים במאגר נתונים
            fbh =  new FirebaseHelper("Games");

            insert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String x= name.getText().toString();
                    String y= url.getText().toString();
                    String k= urlImage.getText().toString();
                    String z= desc.getText().toString();
                    if(x.isEmpty() || y.isEmpty() || z.isEmpty())
                    {// אם האדמין מנסה להוסיף והטקסט ריק מציג הודעה בהתאם
                        if(x.isEmpty())
                            Toast.makeText(getApplicationContext(),"Game name is empty, input name and try again",Toast.LENGTH_LONG).show();
                        if(y.isEmpty() )
                            Toast.makeText(getApplicationContext(),"Game url is empty, input url and try again",Toast.LENGTH_LONG).show();
                        if(k.isEmpty() )
                            Toast.makeText(getApplicationContext(),"Game url image is empty, input url image and try again",Toast.LENGTH_LONG).show();
                        if(z.isEmpty() )
                            Toast.makeText(getApplicationContext(),"Game description is empty, description and try again",Toast.LENGTH_LONG).show();
                    }
                    else{// אם לא ריק אז מוסיף
                        try {
                            // מציב את אובייקט המשחק
                            GameObject obj = new GameObject(x,k,z);
                            boolean status = FirebaseHelper.registerGame(obj);
                            if(status) {
                                Toast.makeText(inputGame.this, "Added Successful", Toast.LENGTH_SHORT).show();
                                FirebaseHelper.registerGameSites(y);
                                FirebaseHelper.registerGameSitesName(linkDisplay.getText().toString());
                            }
                            else
                                Toast.makeText(inputGame.this, "Failed to Add", Toast.LENGTH_SHORT).show();
                         name.setText("");// מאפס את הטקסט שהכניס המשתמש כדי שיוכל להקליד שוב
                            url.setText("");
                            urlImage.setText("");
                            desc.setText("");
                            linkDisplay.setText("");
                        }
                        catch (Exception e)
                        {// במקרה של שגיאה כלשהי מציג הודעה בהתאם
                            Toast.makeText(getApplicationContext(),"Error, try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            out = new Intent(this,EnterAppPage.class);
            out.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(out);
                }
            });
        }
    }
}