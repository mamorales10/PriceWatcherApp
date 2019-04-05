package cs4330.cs.utep.mypricewatcher;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ItemManager itemManager;
    private Item[] items = {new Item("Movie", 30.00, "https://www.amazon.com/AVENGERS-INFINITY-Robert-Downey-Jr/dp/B07BZC5KHW"),
            new Item("Vizio TV", 999.00, "https://www.bestbuy.com/site/vizio-70-class-led-e-series-2160p-smart-4k-uhd-tv-with-hdr/6259880"),
            new Item("Water Bottle", 1.00, "https://www.target.com/p/purified-water-24pk-16-9-fl-oz-bottles-market-pantry-153/-/A-13319038"),
            new Item("Game", 59.99, "https://www.gamestop.com/product/ps4/games/sekiro-shadows-die-twice/164383"),
            new Item("Roku Streaming Stick", 49.99, "https://www.roku.com/products/streaming-stick")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        itemManager = new ItemManager();

        for(Item item : items){
            itemManager.addItem(item);
        }

        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, R.layout.activity_listview, R.id.theTextView, itemManager.getItemList());

        listView = findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setNestedScrollingEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
            {

                //Toast.makeText(getApplicationContext(), (CharSequence) items[itemPosition],Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                i.putExtra("name", items[itemPosition].getName());
                i.putExtra("init", Double.toString(items[itemPosition].getInitial_Price()));
                i.putExtra("url", items[itemPosition].getUrl());

                startActivity(i);
            }
        });





    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        createMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return menuChoice(item);
    }


    private void createMenu(Menu menu){
        MenuItem menuItem1 = menu.add(0, 0, 0, "Add Item");
    }
     private boolean menuChoice(MenuItem item){
        switch(item.getItemId()){
            case 0:
                Log.d("tag", "Went through the switch statement");
                //showAddItemDialog();
                showEditItemDialog();
                return true;
        }
        Log.d("tag", "Did not go throught the switch.");
        return false;
     }


    public void showAddItemDialog(){

        new AlertDialog.Builder(this)
                .setTitle("Add Item")
                .setView(R.layout.fragment_new_item)
                .setMessage("Enter item information")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("tag", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void showEditItemDialog(){

        AlertDialog editDialog = new AlertDialog.Builder(this)
                .setTitle("Edit Item")
                .setView(R.layout.fragment_new_item)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("tag", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        TextView itemName = (TextView) editDialog.findViewById(R.id.editName);
        itemName.setText("This is a test");
    }




}
