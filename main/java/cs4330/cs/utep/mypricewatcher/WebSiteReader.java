package cs4330.cs.utep.mypricewatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;


public class WebSiteReader {
    // required by websites: BestBuy, HomeDepot, Kohls, Amazon, Ebay, Kohls
    protected static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36";

    protected BufferedReader openUrl(String urlString, String userAgent) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        //con.setConnectTimeout(timeout);
        con.setRequestMethod("GET");
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setRequestProperty("Cookie", "zip=79932");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        if (userAgent != null) {
            con.setRequestProperty("User-Agent", userAgent);
        }
        // required by BestBuy website.
        String encoding = con.getContentEncoding();
        if (encoding == null) {
            encoding = "utf-8";
        }
        // Amazon sends gzipped documents even if not requested
        InputStreamReader reader = null;
        if ("gzip".equals(encoding)) {
            reader = new InputStreamReader(new GZIPInputStream(con.getInputStream()));
        } else {
            reader = new InputStreamReader(con.getInputStream(), encoding);
        }
        return new BufferedReader(reader);
    }
}
