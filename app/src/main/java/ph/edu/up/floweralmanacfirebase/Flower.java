package ph.edu.up.floweralmanacfirebase;

/**
 * Created by victo on 7/10/2017.
 */

public class Flower{

    private String userName;
    private String flowerName;
    private String ease;
    private String instructions;
    private String photoUrl;
    private String postKey;

    public Flower() { }

    public Flower(String userName, String flowerName, String ease, String instructions, String photoUrl, String postKey) {
        this.userName = userName;
        this.flowerName = flowerName;
        this.ease = ease;
        this.instructions = instructions;
        this.photoUrl = photoUrl;
        this.postKey = postKey;
    }

    public String getUserName () { return userName; }

    public String getFlowerName() { return flowerName; }

    public String getEase() { return ease; }

    public String getInstructions() { return instructions; }

    public String getPhotoUrl() { return photoUrl; }

    public String getPostKey() { return postKey; }

    public void setUserName(String userName) { this.userName = userName; }

    public void setFlowerName(String flowerName) { this.flowerName = flowerName; }

    public void setEase(String ease) { this.ease = ease; }

    public void setInstructions(String instructions) { this.instructions = instructions; }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public void setPostKey(String postKey) { this.postKey = postKey; }
}
