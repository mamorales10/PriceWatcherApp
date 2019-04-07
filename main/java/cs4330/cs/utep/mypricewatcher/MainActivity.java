package cs4330.cs.utep.mypricewatcher;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.AdapterView.AdapterContextMenuInfo;
import java.util.List;
import android.view.ContextMenu;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ItemManager itemManager;
    private Item[] items = {new Item("Movie", 30.00, "https://www.amazon.com/AVENGERS-INFINITY-Robert-Downey-Jr/dp/B07BZC5KHW"),
            new Item("Vizio TV", 999.00, "https://www.bestbuy.com/site/vizio-70-class-led-e-series-2160p-smart-4k-uhd-tv-with-hdr/6259880"),
            new Item("Water Bottle", 1.00, "https://www.target.com/p/purified-water-24pk-16-9-fl-oz-bottles-market-pantry-153/-/A-13319038"),
            new Item("Game", 59.99, "https://www.gamestop.com/product/ps4/games/sekiro-shadows-die-twice/164383"),
            new Item("Roku Streaming Stick", 49.99, "https://www.roku.com/products/streaming-stick")};
    private  int lastChosenPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        itemManager = new ItemManager();

        for(Item item : items){
            itemManager.addItem(item);
        }

        //ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, R.layout.activity_listview, R.id.theTextView, itemManager.getItemList());

        ItemsAdapter adapter = new ItemsAdapter(this, itemManager.getItemList());

        listView = findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setNestedScrollingEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
            {
                List itemList = itemManager.getItemList();
                Item item = (Item) itemList.get(itemPosition);
                String name =  item.getName();
                double price = item.getInitial_Price();
                String url = item.getUrl();

                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                i.putExtra("name", name);
                i.putExtra("init", Double.toString(price));
                i.putExtra("url", url);

                // Added this
                i.putExtra("position", Integer.toString(itemPosition));

                lastChosenPosition = itemPosition;

                //View chosenItem = listView.getChildAt(itemPosition);
               // TextView listPrice = chosenItem.findViewById(R.id.percentage_in_list);
                //listPrice.setText(String.format("$%.2f", 1234.43));
                //startActivity(i);

                // Added this
                startActivityForResult(i, 0);

            }
        });
        registerForContextMenu(listView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if(resultCode == RESULT_OK) {
                int position = Integer.parseInt(data.getStringExtra("position"));
                double price = Double.parseDouble(data.getStringExtra("price"));


                List itemList = itemManager.getItemList();
                Item item = (Item) itemList.get(position);

                View chosenItem = listView.getChildAt(position);
                TextView listPrice = chosenItem.findViewById(R.id.price_in_list);
                listPrice.setText(String.format("$%.2f", price));

                ArrayAdapter<Item> adapter = (ArrayAdapter) listView.getAdapter();
                adapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final CharSequence input = item.getTitle();
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        if(input == "Delete"){
            showDeleteItemDialog(listPosition);
        }
        else if (input == "Edit"){
            showEditItemDialog(listPosition);
        }
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
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
                showAddItemDialog();
                return true;
        }
        Log.d("tag", "Did not go throught the switch.");
        return false;
    }


    public void showAddItemDialog(){

        AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.fragment_new_item, null);
        editDialog.setView(dialogView);
        editDialog.setTitle("Add Item");
        editDialog.setMessage("Enter item information");
        final EditText itemName = (EditText) dialogView.findViewById(R.id.editName);
        final EditText itemURL = (EditText) dialogView.findViewById(R.id.editURL);
        editDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = itemName.getText().toString();
                String url = itemURL.getText().toString();

                PriceFinder priceFinder = new PriceFinder();

                Item newItem = new Item(name, priceFinder.getPrice(url), url);
                itemManager.addItem(newItem);
                ArrayAdapter<Item> adapter = (ArrayAdapter) listView.getAdapter();

                adapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = editDialog.create();
        dialog.show();

    }

    public void showEditItemDialog(int itemIndex){

        List itemList = itemManager.getItemList();
        Item item = (Item) itemList.get(itemIndex);

        AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.fragment_new_item, null);
        editDialog.setView(dialogView);
        editDialog.setTitle("Edit Item");
        final EditText itemName = dialogView.findViewById(R.id.editName);
        final EditText itemURL = dialogView.findViewById(R.id.editURL);

        itemName.setText(item.getName());
        itemURL.setText(item.getUrl());

        editDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = itemName.getText().toString();
                String url = itemURL.getText().toString();

                itemManager.editItemName(item, name);
                itemManager.editItemURL(item, url);
                ArrayAdapter<Item> adapter = (ArrayAdapter) listView.getAdapter();

                adapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = editDialog.create();
        dialog.show();

    }

    public void showDeleteItemDialog(int index){
        List itemList = itemManager.getItemList();
        Item it = (Item) itemList.get(index);
        itemManager.removeItem(it);
        ArrayAdapter<Item> adapter = ( ArrayAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();
    }

}
