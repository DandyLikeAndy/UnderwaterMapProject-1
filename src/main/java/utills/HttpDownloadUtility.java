package utills;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 15.05.2017.
 */
public class HttpDownloadUtility {
    private static final int BUFFER_SIZE = 4096;
    private static final String dir = "tiles";
    private static List<Callback> callbacks = new ArrayList<>();
    private static List<Callback> onFinishedDownload = new ArrayList<>();


    /**
     * Downloads a file from a URL
     *
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        System.out.println("load to " + saveDir);
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            String[] msg = {"Downloading file:= " + fileName} ;

            //callbacks.forEach(c->c.call(msg[0]));

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            callbacks.forEach(c->c.call("Downloading..."));

        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            callbacks.forEach(c->c.call("No file to download. Server replied HTTP code: " + responseCode));
        }
        httpConn.disconnect();
        System.out.println("done");
    }

    public static void loadTiles(List<String> urls) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Path vendorFolderPath = Paths.get(SettingsProperties.getInstance().getTileCash()+"/"+SettingsProperties.getInstance().getCurrentMapSource().getName());
                if (!Files.exists(vendorFolderPath)) {
                    try {
                        Files.createDirectory(vendorFolderPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                urls.forEach(url -> {
                    System.out.println("url 1 " + url);
                    String[] arr = url.split("/");
                    //String vendorFolder = arr[2];
                    //String[] vendorFolderArr = vendorFolder.split("\\.");
                    //vendorFolder = vendorFolderArr[vendorFolderArr.length-2]+"."+vendorFolderArr[vendorFolderArr.length-1];
                    String s = arr[2].split("\\.")[0];
                    String z = arr[3];
                    String x = arr[4];
                    String y = arr[5].split("\\.")[0];
                    url = prepareUrl(s, x, y, z);
                    System.out.println("url 2 " + url);
                    //System.out.println(z + " " + x + " " + y);

                    //Path vendorFolderPath = path.resolve(SettingsProperties.getInstance().getCurrentMapSource().getName());
                    if (!Files.exists(vendorFolderPath)) try {
                        Files.createDirectory(vendorFolderPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Path zPath = vendorFolderPath.resolve(z);
                    if (!Files.exists(zPath)) try {
                        Files.createDirectory(zPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Path xPath = zPath.resolve(x);
                    if (!Files.exists(xPath)) try {
                        Files.createDirectory(xPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Path yPath = xPath.resolve(y);

                    try {
                        if (!isFileExist(y, xPath.toAbsolutePath()))
                            downloadFile(url, xPath.toAbsolutePath().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                });

                onFinishedDownload.forEach(c->c.call("all files is downloaded"));
            }
        }).start();
    }

    public static void addCallbacks(Callback callback){
        callbacks.add(callback);
    }

    public static void setOnFinishedDownload(Callback callback){
        onFinishedDownload.add(callback);
    }


    private static String prepareUrl(String s, String x, String y, String z) {
        String url = SettingsProperties.getInstance().getCurrentMapSource().getDownloadUrl();
        String res;
        res = url.replaceFirst("\\{s", s);
        res = res.replaceFirst("\\{x", x);
        res = res.replaceFirst("\\{y", y);
        res = res.replaceFirst("\\{z", z);
        res = res.replaceAll("}", "");

        System.out.println("result url: "+res);
        return res;
    }

    private static boolean isFileExist(String fileName, Path path) {
        return Files.exists(path.resolve(fileName));
    }

    public interface Callback{
        void call(String msg);
    }

    public static void main(String[] args) {
        String url = "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png";
        url = url.replaceFirst("\\{s", "a");
        url = url.replaceFirst("\\{x", "a");
        url = url.replaceFirst("\\{y", "a");
        url = url.replaceFirst("\\{z", "a");
        url = url.replaceAll("}", "");
        System.out.println(url);
    }


}
