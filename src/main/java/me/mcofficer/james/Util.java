package me.mcofficer.james;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class Util {

    /**
     * Checks a URL for the HTTP status code.
     * <p>
     * Returns 0 if an IOException occurs.
     * Returns 1 if a MalformedURLException occurs.
     * Returns -1 if the Response is invalid.
     * @param url The url to check.
     * @return The HTTP Status Code.
     */
    public static int getHttpStatus(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("User-Agent", "MarioB(r)owser4.2");
            connection.connect();
            return connection.getResponseCode();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return 1;
        }
        catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getContentFromUrl(String url) {
        try {
            InputStream is = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1)
                sb.append((char) cp);
            return sb.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void downloadFile(String url, Path targetDir) throws IOException {
        String filename = url.substring(url.lastIndexOf('/'));
        ReadableByteChannel channel = Channels.newChannel(new URL(url).openStream());
        FileOutputStream outputStream = new FileOutputStream(targetDir + filename);
        outputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
    }
}
