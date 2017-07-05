package utills;

public class MapSource {
    String name;
    String url;
    String subdomains;
    String downloadUrl;

    public MapSource(String name, String url, String subdomains, String downloadUrl) {
        this.name = name;
        this.url = url;
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubdomains() {
        return subdomains;
    }

    public void setSubdomains(String subdomains) {
        this.subdomains = subdomains;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
