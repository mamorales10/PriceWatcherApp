package cs4330.cs.utep.mypricewatcher;
import java.util.ArrayList;
import java.util.List;
//package cs4330.cs.utep.mypricewatcher;

public class ItemManager {
    private ArrayList itemList = new ArrayList();

    public ItemManager(){
    }

    public List getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList itemList) {
        this.itemList = itemList;
    }

    public boolean addItem(Item item){
        this.itemList.add(item);
        if(this.itemList.indexOf(item) == -1){
            return false;
        }
        return true;
    }
    public boolean removeItem(Item item){
        this.itemList.remove(item);
        if(this.itemList.indexOf(item) != -1){
            return false;
        }
        return true;
    }
    public void editItemURL(Item item, String url){
        item.setUrl(url);
    }
    public void editItemName(Item item, String name){
        item.setName(name);
    }
}
