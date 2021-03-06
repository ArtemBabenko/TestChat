package test_chat_project.testchat.Item;

public class Room_Message {

    private String mId;
    private String mIdImage;
    private String mNameUser;
    private String mIconUser;
    private String mUserKeyProfile;
    private String mMessageUser;
    private String mMessageTime;
    private String mMessageUri;

    public Room_Message(String mId, String mIdImage, String mMessageUri, String mNameUser, String mIconUser, String mUserKeyProfile, String mMessageUser, String mMessageTime) {
        this.mId = mId;
        this.mIdImage = mIdImage;
        this.mNameUser = mNameUser;
        this.mIconUser = mIconUser;
        this.mUserKeyProfile = mUserKeyProfile;


        this.mMessageUser = mMessageUser;
        this.mMessageTime = mMessageTime;
        this.mMessageUri = mMessageUri;

    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmIdImage() {
        return mIdImage;
    }

    public void setmIdImage(String mIdImage) {
        this.mIdImage = mIdImage;
    }

    public String getmMessageTime() {
        return mMessageTime;
    }

    public void setmMessageTime(String mMessageTime) {
        this.mMessageTime = mMessageTime;
    }

    public String getmNameUser() {
        return mNameUser;
    }

    public void setmNameUser(String mNameUser) {
        this.mNameUser = mNameUser;
    }

    public String getmIconUser() {
        return mIconUser;
    }

    public void setmIconUser(String mIconUser) {
        this.mIconUser = mIconUser;
    }

    public String getmUserKeyProfile() {
        return mUserKeyProfile;
    }

    public void setmUserKeyProfile(String mUserKeyProfile) {
        this.mUserKeyProfile = mUserKeyProfile;
    }

    public String getmMessageUser() {
        return mMessageUser;
    }

    public void setmMessageUser(String mMessageUser) {
        this.mMessageUser = mMessageUser;
    }

    public String getmMessageUri() {
        return mMessageUri;
    }

    public void setmMessageUri(String mMessageUri) {
        this.mMessageUri = mMessageUri;
    }
}
