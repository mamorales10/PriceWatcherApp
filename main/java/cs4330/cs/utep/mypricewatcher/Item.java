package cs4330.cs.utep.mypricewatcher;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

    private String name;
    private double initial_Price;
    private double current_Price;
    private double change_percentage;
    private String url;

    public Item(String name, double initial_Price, String url){
        this.name = name;
        this.initial_Price = initial_Price;
        this.current_Price = initial_Price;
        this.change_percentage = 0.0;
        this.url = url;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){ this.name = name; }

    public double getInitial_Price(){
        return initial_Price;
    }

    public double getCurrent_Price(){
        return current_Price;
    }

    public void setCurrent_Price(double current_Price){
        this.current_Price = current_Price;
        setChange_Percentage(current_Price);
    }

    public double getChange_Percentage(){
        return change_percentage;
    }

    /* MainActivity will perform calculation */
    private void setChange_Percentage(double currentPrice){
        this.change_percentage = (currentPrice - getInitial_Price()) / getInitial_Price() * 100;;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){this.url = url;}

    public String toString(){
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.initial_Price);
        dest.writeDouble(this.current_Price);
        dest.writeDouble(this.change_percentage);
        dest.writeString(this.url);
    }

    private Item(Parcel in){
        this.name = in.readString();
        this.initial_Price = in.readDouble();
        this.current_Price = in.readDouble();
        this.change_percentage = in.readDouble();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>(){
        public Item createFromParcel(Parcel source){
            return new Item(source);
        }
        public Item[] newArray(int size){
            return new Item[size];
        }
    };
}
