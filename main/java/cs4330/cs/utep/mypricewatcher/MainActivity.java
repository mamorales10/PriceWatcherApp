package cs4330.cs.utep.mypricewatcher;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Item firstItem;
    private ListView listView;
    //private String[] items = {"whatever", "f", "m", "l", "I'm supreme overlord!", "Can", "whatever", "f", "m", "l", "I'm supreme overlord!", "Can"};
    private Item[] items = {new Item("what", 0.00, "http://www.cs.utep.edu/cheon/cs4330/homework/hw2.txt"),
            new Item("am", 120.00, "http://www.cs.utep.edu/cheon/cs4330/homework/hw2.txt"),
            new Item("I", 10.00, "http://www.cs.utep.edu/cheon/cs4330/homework/hw2.txt"),
            new Item("doing?", 330.00, "http://www.cs.utep.edu/cheon/cs4330/homework/hw2.txt")};
    private PriceFinder priceFinder;
    private TextView nameDisplay;
    private TextView initPriceDisplay;
    private TextView currentPriceDisplay;
    private TextView percentageDisplay;
    private Button refreshButton;
    private Button siteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameDisplay = findViewById(R.id.itemName);
        initPriceDisplay = findViewById(R.id.initPrice);
        currentPriceDisplay = findViewById(R.id.currentPrice);
        percentageDisplay = findViewById(R.id.change);

        siteButton = findViewById(R.id.siteButton);
        siteButton.setOnClickListener(this::searchWebsite);

        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, R.layout.activity_listview, R.id.theTextView, items);

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

        priceFinder = new PriceFinder();
        firstItem = new Item("Roku Streaming Stick", 49.99, "https://www.roku.com/products/streaming-stick");

        //displayPrices();

    }
    public void onListItemClick(ListView parent, View v, int position, long id){
        Toast.makeText(this, "You have selected " + (CharSequence) items[position],Toast.LENGTH_SHORT).show();
    }







    public void searchWebsite(View view){

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(firstItem.getUrl())));
    }

}
