package utills;

public class MapSource {
    String name;
    String url;
    String subdomains;

    public MapSource(String name, String url, String subdomains) {
        this.name = name;
        this.url = url;
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
}
