package cs4330.cs.utep.mypricewatcher;

public class Item {

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

    public double getInitial_Price(){
        return initial_Price;
    }

    public double getCurrent_Price(){
        return current_Price;
    }

    public void setCurrent_Price(double current_Price){
        this.current_Price = current_Price;
    }

    public double getChange_Percentage(){
        return change_percentage;
    }

    /* MainActivity will perform calculation */
    public void setChange_Percentage(double change_percentage){
        this.change_percentage = change_percentage;
    }

    public String getUrl(){
        return url;
    }

}
