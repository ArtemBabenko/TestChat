package test_chat_project.testchat.Item;


public class Room_Message_Image {

    private String mIdImage;
    private String mNameUserImage;
    private String mMessageUserImage;
    private String mMessageTimeImage;
    private String mURLMessageImage;

    public Room_Message_Image(String mIdImage, String mNameUserImage, String mMessageUserImage, String mMessageTimeImage, String mURLMessageImage) {
        this.mIdImage = mIdImage;
        this.mNameUserImage = mNameUserImage;
        this.mMessageUserImage = mMessageUserImage;
        this.mMessageTimeImage = mMessageTimeImage;
        this.mURLMessageImage = mURLMessageImage;
    }

    public String getmIdImage() {
        return mIdImage;
    }

    public void setmIdImage(String mIdImage) {
        this.mIdImage = mIdImage;
    }

    public String getmNameUserImage() {
        return mNameUserImage;
    }

    public void setmNameUserImage(String mNameUserImage) {
        this.mNameUserImage = mNameUserImage;
    }

    public String getmMessageUserImage() {
        return mMessageUserImage;
    }

    public void setmMessageUserImage(String mMessageUserImage) {
        this.mMessageUserImage = mMessageUserImage;
    }

    public String getmMessageTimeImage() {
        return mMessageTimeImage;
    }

    public void setmMessageTimeImage(String mMessageTimeImage) {
        this.mMessageTimeImage = mMessageTimeImage;
    }

    public String getmURLMessageImage() {
        return mURLMessageImage;
    }

    public void setmURLMessageImage(String mURLMessageImage) {
        this.mURLMessageImage = mURLMessageImage;
    }
}
