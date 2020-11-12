package edu.iit.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private List<Official> officialList;
    private MainActivity mainAct;

    public OfficialAdapter(List<Official> officialList, MainActivity mainAct) {
        this.officialList = officialList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("passed here!!!!");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent, false);
        view.setOnClickListener(mainAct);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Official official = officialList.get(position);
        if (official.getParty() == null) holder.row_name.setText(official.getName());
        else holder.row_name.setText(official.getName()+'('+official.getParty()+')');
        holder.row_office.setText(official.getOfficeName());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }

}
