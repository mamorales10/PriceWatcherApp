package cs4330.cs.utep.mypricewatcher;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.view.ContextMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity  {

    private ListView listView;
    private ItemManager itemManager;
    private ItemDbAdapter db;
    public double websitePrice;
    WebSiteReader webReader = new WebSiteReader();
    //ReadWebsiteTask readWebsiteTask = new ReadWebsiteTask();
    protected static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //readWebsiteTask.delegate = this;
        db = new ItemDbAdapter(this);



        db.open();
        itemManager = new ItemManager();

        Cursor c = db.getAllItems();
        if(c.moveToFirst()) {
            do {
                int rowID = c.getInt(0);
                String itemName = c.getString(1);
                double itemIPrice = c.getFloat(2);
                double itemCPrice = c.getFloat(3);
                String itemURL = c.getString(4);
                DatabaseItem item = new DatabaseItem(rowID, itemName, itemIPrice, itemURL);
                item.setCurrent_Price(itemCPrice);
                itemManager.addItem(item);
            } while (c.moveToNext());
        }
        c.close();
        db.close();

        ItemsAdapter adapter = new ItemsAdapter(this, itemManager.getItemList());//itemManager.getItemList());

        listView = findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setNestedScrollingEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
            {
                List itemList = itemManager.getItemList();
                Item item = (Item) itemList.get(itemPosition - listView.getFirstVisiblePosition());
                String name =  item.getName();
                double price = item.getInitial_Price();
                double currentPrice = item.getCurrent_Price();
                String url = item.getUrl();

                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                i.putExtra("name", name);
                i.putExtra("init", Double.toString(price));
                i.putExtra("current", Double.toString(currentPrice));
                i.putExtra("url", url);
                i.putExtra("position", Integer.toString(itemPosition - listView.getFirstVisiblePosition()));

                startActivityForResult(i, 0);

            }
        });
        registerForContextMenu(listView);

        // Get the intent that started this activity
        String action = getIntent().getAction();
        String type = getIntent().getType();

        // Figure out what to do based on the intent type
        if (Intent.ACTION_SEND.equalsIgnoreCase(action) && type != null && ("text/plain").equals(type)) {
            String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            showAddItemDialog(url);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if(resultCode == RESULT_OK) {
                int position = Integer.parseInt(data.getStringExtra("position"));
                double price = Double.parseDouble(data.getStringExtra("price"));

                View chosenItem = listView.getChildAt(position);
                TextView listPrice = chosenItem.findViewById(R.id.price_in_list);

                listPrice.setText(String.format("$%.2f", price));

                ArrayAdapter<Item> adapter = (ArrayAdapter) listView.getAdapter();
                Item item = adapter.getItem(position);
                item.setCurrent_Price(price);
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

        AlertDialog.Builder addDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_item, null);
        addDialog.setView(dialogView);
        addDialog.setTitle("Add Item");
        addDialog.setMessage("Enter item information");
        final EditText itemName = (EditText) dialogView.findViewById(R.id.editName);
        final EditText itemURL = (EditText) dialogView.findViewById(R.id.editURL);
        addDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = itemName.getText().toString();
                String url = itemURL.getText().toString();


                if (url.contains("homedepot") || url.contains("academy") || url.contains("walmart")) {


                    ReadWebsiteTask readWebsiteTask = new ReadWebsiteTask();//.execute(url);
                    Log.d("tag", "This is websiteprice: " + websitePrice);
                    try {
                        websitePrice = Double.parseDouble(readWebsiteTask.execute(url).get().replace("$", ""));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException ee) {
                    }
                    websitePrice = readWebsiteTask.whatever;

                    Item newItem = new Item(name, websitePrice, url);
                    Log.d("tag", "This is item.getInitialPrice " + newItem.getInitial_Price());

                    db.open();
                    long id = db.insertItem(newItem.getName(), websitePrice,
                            websitePrice, newItem.getUrl(), newItem.getChange_Percentage());

                    Cursor c = db.getAllItems();
                    c.moveToLast();
                    int rowID = c.getInt(0);
                    db.close();
                    c.close();

                    DatabaseItem dbItem = new DatabaseItem(rowID, name, websitePrice, url);
                    itemManager.addItem(dbItem);
                    ArrayAdapter<Item> adapter = (ArrayAdapter) listView.getAdapter();
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getBaseContext(), "Malformed Url", Toast.LENGTH_LONG).show();
                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = addDialog.create();
        dialog.show();

    }

    public void showAddItemDialog(String url){

        AlertDialog.Builder addDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_item, null);
        addDialog.setView(dialogView);
        addDialog.setTitle("Add Item");
        addDialog.setMessage("Enter item information");
        final EditText itemName = (EditText) dialogView.findViewById(R.id.editName);
        final EditText itemURL = (EditText) dialogView.findViewById(R.id.editURL);
        itemURL.setText(url);
        addDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = itemName.getText().toString();
                String url = itemURL.getText().toString();


                ReadWebsiteTask readWebsiteTask = new ReadWebsiteTask();//.execute(url);
                Log.d("tag", "This is websiteprice: " + websitePrice);
                try{
                    websitePrice = Double.parseDouble(readWebsiteTask.execute(url).get().replace("$", ""));
                }catch (InterruptedException e){
                    e.printStackTrace();
                }catch (ExecutionException ee){}
                websitePrice = readWebsiteTask.whatever;

                Item newItem = new Item(name, websitePrice, url);
                Log.d("tag", "This is item.getInitialPrice "+ newItem.getInitial_Price());

                db.open();
                long id = db.insertItem(newItem.getName(), websitePrice,
                        websitePrice, newItem.getUrl(), newItem.getChange_Percentage());

                Cursor c = db.getAllItems();
                c.moveToLast();
                int rowID = c.getInt(0);
                db.close();
                c.close();

                DatabaseItem dbItem = new DatabaseItem(rowID, name, websitePrice, url);
                itemManager.addItem(dbItem);
                ArrayAdapter<Item> adapter = (ArrayAdapter) listView.getAdapter();
                adapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = addDialog.create();
        dialog.show();

    }


    public void showEditItemDialog(int itemIndex){

        List itemList = itemManager.getItemList();
        DatabaseItem item = (DatabaseItem) itemList.get(itemIndex);

        AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_item, null);
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

                ReadWebsiteTask readWebsiteTask = new ReadWebsiteTask();//.execute(url);
                Log.d("tag", "This is websiteprice: " + websitePrice);
                try{
                    websitePrice = Double.parseDouble(readWebsiteTask.execute(url).get().replace("$", ""));
                }catch (InterruptedException e){
                    e.printStackTrace();
                }catch (ExecutionException ee){}

                Item newItem = new Item(name, websitePrice, url);
                Log.d("tag", "This is item.getInitialPrice "+ newItem.getInitial_Price());

                itemManager.editItemName(item, name);
                itemManager.editItemURL(item, url);

                item.setCurrent_Price(websitePrice);

                ArrayAdapter<Item> adapter = (ArrayAdapter) listView.getAdapter();
                adapter.notifyDataSetChanged();

                db.open();
                boolean itemUpdated = db.updateItem(item.getRowID(), name, item.getInitial_Price(),
                        websitePrice, url, 0.0);
                if(itemUpdated){
                    Log.d("tag", "Item Updated");
                }
                else{
                    Log.d("tag", "Update failed");
                }
                db.close();
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

        DatabaseItem it = (DatabaseItem) itemList.get(index);
        itemManager.removeItem(it);
        ArrayAdapter<Item> adapter = ( ArrayAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();

        db.open();
        if(db.deleteItem(it.getRowID())){
            Log.d("tag", "Delete successful.");
        }
        else{
            Log.d("tag", "Delete failed.");
        }

        db.close();

    }

    private static String parseHomeDepot(BufferedReader website){
        Document doc;
        String line;
        try{
            while((line = website.readLine()) != null){
                doc = Jsoup.parse(line);
                Element e = doc.getElementById("ciItemPrice");
                if(e != null){
                    //Log.d("tag", "tag found!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    website.close();
                    Attributes attributes = e.attributes();
                    String price = attributes.get("value");
                    return price;
                }
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }

    private static String parseWalmart(BufferedReader html){
        Document doc;
        String line;
        try{
            while((line = html.readLine()) != null){
                doc = Jsoup.parse(line);
                Element item = doc.getElementsByClass("price-group").first();
                if(item != null){
                    html.close();
                    return item.attr("aria-label");
                }
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }

    private static String parseAcademy(BufferedReader html){
        Document doc;
        String line;
        try{
            while((line = html.readLine()) != null){
                doc = Jsoup.parse(line);
                Elements e = doc.getElementsByClass("css-1hyfx7x e1m4x7hc0");
                if(!e.isEmpty()){
                    Document innerDoc = Jsoup.parse(e.html());
                    Element ee = innerDoc.getElementsByTag("span").get(1);
                    String price = ee.html();
                    html.close();
                    return price;
                }
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }

    public class ReadWebsiteTask extends AsyncTask<String, Void, String> {
        public AsyncResponse delegate = null;
        double whatever = 1.0;

        protected String doInBackground(String... urls) {
            BufferedReader site = null;
            String answer;

            try {
                site = webReader.openUrl(urls[0], USER_AGENT);
            } catch (IOException e) {

                e.printStackTrace();
            }
            String price;
            if (urls[0].contains("homedepot")) {
                price = parseHomeDepot(site);
            } else if (urls[0].contains("academy")) {
                price = parseAcademy(site);
            } else if (urls[0].contains("walmart")) {
                price = parseWalmart(site);
            } else {
                PriceFinder priceFinder = new PriceFinder();
                price = Double.toString(priceFinder.getPrice(urls[0]));
            }
            Log.d("tag", "This is websiteprice: " + price);
            String temp = price.replace("$", "");
            whatever = Double.parseDouble(temp);
            return price;
        }



    }



}


