package cs4330.cs.utep.mypricewatcher;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private PriceFinder priceFinder;
    private Item item;
    private TextView nameDisplay;
    private TextView initPriceDisplay;
    private TextView currentPriceDisplay;
    private TextView percentageDisplay;
    private Button refreshButton;
    private Button siteButton;
    private String position; // Item list position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        String name = i.getStringExtra("name");

        Double init_price = Double.parseDouble(i.getStringExtra("init"));

        String url = i.getStringExtra("url");

        position = i.getStringExtra("position");

        item = new Item(name, init_price, url);

        nameDisplay = findViewById(R.id.itemName);


        initPriceDisplay = findViewById(R.id.initPrice);

        currentPriceDisplay = findViewById(R.id.currentPrice);


        percentageDisplay = findViewById(R.id.change);

        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this::refreshClicked);

        siteButton = findViewById(R.id.siteButton);
        siteButton.setOnClickListener(this::searchWebsite);

        priceFinder = new PriceFinder();
        displayPrices();
    }

    public void searchWebsite(View view){

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl())));
    }

    public void onBackPressed(){
        Intent i = new Intent();
        i.putExtra("position", position);
        i.putExtra("price", Double.toString(item.getCurrent_Price()));
        setResult(RESULT_OK, i);
        finish();
        super.onBackPressed();
    }

    private void displayPrices(){
        nameDisplay.setText(String.format("%s", item.getName()));
        initPriceDisplay.setText(String.format("$%.2f", item.getInitial_Price()));

        findPrice(item);

        currentPriceDisplay.setText(String.format("$%.2f", item.getCurrent_Price()));
        percentageDisplay.setText(String.format("%.2f %%", item.getChange_Percentage()));

    }

    private void findPrice(Item item){

        double currentPrice = priceFinder.getPrice(item.getUrl());
        if(item.getCurrent_Price() != currentPrice) {
            item.setCurrent_Price(currentPrice);



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

}
