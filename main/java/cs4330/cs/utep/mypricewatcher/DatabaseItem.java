package cs4330.cs.utep.mypricewatcher;

import android.provider.ContactsContract;

public class DatabaseItem extends Item {

    private int rowID;

    public DatabaseItem(int rowID, String name, double initial_Price, String url){
        super(name, initial_Price, url);
        this.rowID = rowID;
    }

    public int getRowID(){
        return rowID;
    }

}
