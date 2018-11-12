package fr.esiee.bde.macao.Annales;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.esiee.bde.macao.R;

/**
 * Created by Wallerand on 02/06/2017.
 */

public class AnnaleAdapter extends RecyclerView.Adapter<AnnaleAdapter.MyViewHolder> {
    private List<Annale> annalesList;
    private OnItemClickListener mListener;

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public TextView subject, teacher, unit, year;
        private Annale annale;

        public MyViewHolder(View view) {
            super(view);
            subject = view.findViewById(R.id.annale_subject);
            teacher = view.findViewById(R.id.annale_teacher);
            unit = view.findViewById(R.id.annale_unit);
            year = view.findViewById(R.id.annale_year);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.fetchAnnale(annale.getId());
        }

        public void setAnnale(Annale annale) {
            this.annale = annale;
        }
    }

    public interface OnItemClickListener {
        void fetchAnnale(int id);
    }

    public AnnaleAdapter(List<Annale> annalesList, OnItemClickListener listener) {
        this.annalesList = annalesList;
        this.mListener = listener;
    }

    @Override
    public AnnaleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annale_list_row, parent, false);
        return new AnnaleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AnnaleAdapter.MyViewHolder holder, int position) {
        Annale annale = annalesList.get(position);
        holder.subject.setText(annale.getSubject());
        holder.teacher.setText(annale.getTeacher());
        holder.unit.setText(annale.getUnit());
        holder.year.setText(annale.getYear());
        holder.setAnnale(annale);
    }

    @Override
    public int getItemCount() {
        return annalesList.size();
    }

}
