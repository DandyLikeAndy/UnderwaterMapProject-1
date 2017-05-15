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
import java.util.List;

/**
 * Created by User on 15.05.2017.
 */
public class HttpDownloadUtility {
    private static final int BUFFER_SIZE = 4096;
    private static final String dir = "tiles";

    public static void main(String[] args) throws IOException {
        List<String> list = new ArrayList<>();
        list.add("https://a.tile.opentopomap.org/10/517/377.png");
        list.add("https://a.tile.opentopomap.org/10/514/377.png");

        loadTiles(list);
    }

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        System.out.println("load to "+saveDir);
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

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

    public static void loadTiles(List<String> urls) throws IOException {
        Path path = Paths.get(dir);
        if (!Files.exists(path)){
            Files.createDirectory(path);
        }

        urls.forEach(url -> {
            url.replace("http://", "");
            String[] arr = url.split("/");
            String z = arr[2];
            String x = arr[3];
            String y = arr[4];

            Path zPath = path.resolve(z);
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
                downloadFile(url, xPath.toAbsolutePath().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
