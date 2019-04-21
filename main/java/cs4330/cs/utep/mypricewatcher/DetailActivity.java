package cs4330.cs.utep.mypricewatcher;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

    private double websitePrice;
    WebSiteReader webReader = new WebSiteReader();
    protected static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        String name = i.getStringExtra("name");

        double init_price = Double.parseDouble(i.getStringExtra("init"));

        double current_price = Double.parseDouble(i.getStringExtra("current"));

        String url = i.getStringExtra("url");

        position = i.getStringExtra("position");

        item = new Item(name, init_price, url);
        item.setCurrent_Price(current_price);

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


        ReadWebsiteTask readWebsiteTask = new ReadWebsiteTask();
        try{
            websitePrice = Double.parseDouble(readWebsiteTask.execute(item.getUrl(), USER_AGENT).get().replace("$", ""));
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (ExecutionException ee){}

        if(item.getCurrent_Price() != websitePrice){
            item.setCurrent_Price(websitePrice);
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


//        protected void onPostExecute(String result) {
//            String temp = result.replace("$", "");
//            this.whatever = Double.parseDouble(temp);
//              https://www.academy.com/shop/pdp/magellan-outdoors-mens-hill-zone-short-sleeve-t-shirt#repChildCatid=5310197
//        }


    }

}
