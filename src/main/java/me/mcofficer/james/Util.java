package me.mcofficer.james;

import me.mcofficer.esparser.Sources;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;

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
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "MarioB(r)owser4.2");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
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


    public static ArrayList<File> fetchGameData(String githubToken) throws IOException {
        Path temp = Files.createTempDirectory("james");
        File data = new File(temp.toAbsolutePath() + "/data/");
        data.mkdir();

        JSONArray json = new JSONArray(Util.getContentFromUrl("https://api.github.com/repos/endless-sky/endless-sky/contents/data?ref=master&access_token=" + githubToken));
        for (Object o : json) {
            JSONObject j = (JSONObject) o;
            Util.downloadFile(j.getString("download_url"), data.toPath());
        }
        ArrayList<File> sources = Sources.getSources(temp, null);
        Files.walk(temp)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        return sources;
    }

    public static ArrayList<String> get1xImagePaths(String githubToken) {
        return checkImageDir("https://api.github.com/repos/endless-sky/endless-sky/contents/images?ref=master", githubToken);
    }

    private static ArrayList<String> checkImageDir(String path, String githubToken) {
        JSONArray json = new JSONArray(Util.getContentFromUrl(path + "&access_token=" + githubToken));
        ArrayList<String> arrayList = new ArrayList<>();
        for (Object o : json) {
            JSONObject j = (JSONObject) o;
            if (j.getString("type").equals("dir"))
                arrayList.addAll(checkImageDir(j.getString("url"), githubToken));
            else
                arrayList.add(j.getString("download_url"));
        }
        return arrayList;
    }

    public static ArrayList<String> get2xImagePaths(ArrayList<String> imagePaths) {
        ArrayList<String> revisedPaths = new ArrayList<>();
        for (String path : imagePaths) {
            String revised = path.replace("endless-sky/master", "endless-sky-high-dpi/master")
                    .replace(".png", "@2x.png");
            if (getHttpStatus(path) == 200)
                revisedPaths.add(revised);
            else
                revisedPaths.add(path);
        }
        return revisedPaths;
    }
}
