package cs4330.cs.utep.mypricewatcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class ItemsAdapter extends ArrayAdapter<Item> {
    public ItemsAdapter(Context context, List<Item> items){
        super(context, 0, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){



        Item item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_in_list, parent, false);
        }

        TextView itemName = (TextView) convertView.findViewById(R.id.name_in_list);
        TextView itemPercentage = (TextView) convertView.findViewById(R.id.price_in_list);

        itemName.setText(item.getName());
        itemPercentage.setText(String.format("$%.2f", item.getCurrent_Price()));

        return convertView;
    }


}
