package test_chat_project.testchat.Item;


public class Room_List_Element {
    private String mNameRoom;
    private String mPasswordRoom;
    private String mCreator;

    public Room_List_Element(String mNameRoom, String mPasswordRoom, String mCreator) {
        this.mNameRoom = mNameRoom;
        this.mPasswordRoom = mPasswordRoom;
        this.mCreator = mCreator;

    }

    public String getmNameRoom() {
        return mNameRoom;
    }

    public void setmNameRoom(String mNameRoom) {
        this.mNameRoom = mNameRoom;
    }

    public String getmPasswordRoom() {
        return mPasswordRoom;
    }

    public void setmPasswordRoom(String mPasswordRoom) {
        this.mPasswordRoom = mPasswordRoom;
    }

    public String getmCreator() {
        return mCreator;
    }

    public void setmCreator(String mCreator) {
        this.mCreator = mCreator;
    }

}
