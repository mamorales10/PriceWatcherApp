package cs4330.cs.utep.mypricewatcher;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Item firstItem;
    private ListView listView;
    private String[] items = {"whatever", "f", "m", "l", "I'm supreme overlord!", "Can", "whatever", "f", "m", "l", "I'm supreme overlord!", "Can"};
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
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this::refreshClicked);
        siteButton = findViewById(R.id.siteButton);
        siteButton.setOnClickListener(this::searchWebsite);

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_listview, items);

        listView = findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setNestedScrollingEnabled(true);
        listView.setTextFilterEnabled(true);

        priceFinder = new PriceFinder();
        firstItem = new Item("Roku Streaming Stick", 49.99, "https://www.roku.com/products/streaming-stick");

        displayPrices();

    }
    public void onListItemClick(ListView parent, View v, int position, long id){
        Toast.makeText(this, "You have selected " + items[position],Toast.LENGTH_SHORT).show();
    }

    private void displayPrices(){
        nameDisplay.setText(String.format("%s", firstItem.getName()));
        initPriceDisplay.setText(String.format("$%.2f", firstItem.getInitial_Price()));

        findPrice(firstItem);

        currentPriceDisplay.setText(String.format("$%.2f", firstItem.getCurrent_Price()));
        percentageDisplay.setText(String.format("%% %.2f", firstItem.getChange_Percentage()));
    }

    private void findPrice(Item item){
        double currentPrice = priceFinder.getPrice(item.getUrl());
        if(item.getCurrent_Price() != currentPrice) {
            item.setCurrent_Price(currentPrice);

            double change = (currentPrice - item.getInitial_Price()) / item.getInitial_Price() * 100;
            item.setChange_Percentage(change);

        }
    }

    public void refreshClicked(View view){
        new Thread(() -> {
            this.runOnUiThread(this::displayPrices);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {}

        }).start();
    }

    public void searchWebsite(View view){

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(firstItem.getUrl())));
    }

}
