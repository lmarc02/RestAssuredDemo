import java.util.List;
import java.util.Map;

public class AddPetModal {
    private Integer id;
    private Map<String, String> category;
    private String    name;
    private List<String>    photoUrls;
    private List<Map> tags;
    private String    status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<String, String> getCategory() {
        return category;
    }

    public void setCategory(Map<String, String> category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public List<Map> getTags() {
        return tags;
    }

    public void setTags(List<Map> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
