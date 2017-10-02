package fr.esiee.bde.macao.Clubs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.esiee.bde.macao.Clubs.Club;
import fr.esiee.bde.macao.R;

/**
 * Created by Wallerand on 01/06/2017.
 */

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.MyViewHolder> {

    private List<Club> eventsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, content;
        private Button link, email;
        private ImageView image;

        private String url;
        private String mail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.club_title);
            content = (TextView) view.findViewById(R.id.club_content);
            image = (ImageView) view.findViewById(R.id.club_image);
            link = (Button) view.findViewById(R.id.club_link);
            email = (Button) view.findViewById(R.id.club_email_button);

            link.setOnClickListener(this);
            email.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.club_link:
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(this.url));
                    context.startActivity(i);
                    break;
                case R.id.club_email_button:
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", this.mail, null));
                    //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug de l'application Macao");
                    //emailIntent.putExtra(Intent.EXTRA_TEXT, "Salut,\n\nJ'ai remarqué un bug dans l'application :\n\n");
                    context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        }

        public void setUrl(String url){
            this.url = url;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }
    }


    public ClubAdapter(List<Club> eventsList, Context context) {
        this.eventsList = eventsList;
        this.context = context;
    }

    @Override
    public ClubAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_list_row, parent, false);

        return new ClubAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClubAdapter.MyViewHolder holder, int position) {
        Club club = eventsList.get(position);
        holder.title.setText(club.getTitle());
        holder.content.setText(club.getContent());
        holder.setMail(club.getEmail());
        holder.setUrl("https://bde.esiee.fr/clubs/view/"+club.getId()+"-"+club.getShortcode());
        Picasso.with(context).load(club.getImage()).resize(350, 350).centerCrop().into(holder.image);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
