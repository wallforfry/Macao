package fr.esiee.bde.macao.Jobs;

/**
 * Created by Wallerand on 31/05/2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import fr.esiee.bde.macao.R;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.MyViewHolder> {

    private List<Jobs> jobsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public TextView title;
        public TextView content;
        public TextView name;
        public TextView email;
        public TextView telephone;
        public RelativeLayout layout_name, layout_email, layout_telephone, layout_color;

        public MyViewHolder(View view) {
            super(view);
            category = (TextView) view.findViewById(R.id.job_category);
            title = (TextView) view.findViewById(R.id.job_title);
            content = (TextView) view.findViewById(R.id.job_content);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            name = (TextView) view.findViewById(R.id.job_name);
            email = (TextView) view.findViewById(R.id.job_email);
            telephone = (TextView) view.findViewById(R.id.job_telephone);

            layout_color = (RelativeLayout) view.findViewById(R.id.job_layout_color);
            layout_name = (RelativeLayout) view.findViewById(R.id.job_layout_name);
            layout_email = (RelativeLayout) view.findViewById(R.id.job_layout_email);
            layout_telephone = (RelativeLayout) view.findViewById(R.id.job_layout_telephone);

        }
    }


    public JobsAdapter(List<Jobs> jobsList, Context context) {
        this.jobsList = jobsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jobs_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Jobs job = jobsList.get(position);

        holder.layout_color.setBackgroundColor(Color.parseColor(job.getColor()));

        holder.category.setText(job.getCategory());
        holder.title.setText(job.getTitle());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.content.setText(Html.fromHtml(String.valueOf(job.getContent()), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.content.setText(Html.fromHtml(String.valueOf(job.getContent())));
        }

        if (job.getName().equals("null")) {
            holder.layout_name.setVisibility(View.GONE);
        }
        if (job.getEmail().equals("null")) {
            holder.layout_email.setVisibility(View.GONE);
        }
        if (job.getTelephone().equals("null")) {
            holder.layout_telephone.setVisibility(View.GONE);
        }

        holder.name.setText(job.getName());

        SpannableString contentEmail = new SpannableString(job.getEmail());
        contentEmail.setSpan(new UnderlineSpan(), 0, job.getEmail().length(), 0);
        holder.email.setText(contentEmail);

        SpannableString contentTelephone = new SpannableString(job.getTelephone());
        contentTelephone.setSpan(new UnderlineSpan(), 0, job.getTelephone().length(), 0);
        holder.telephone.setText(contentTelephone);

        holder.layout_telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = job.getTelephone();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                }
                else {
                    context.startActivity(intent);
                }
            }
        });

        holder.layout_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",job.getEmail(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, job.getName());
                context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });




    }

    @Override
    public int getItemCount() {
        return jobsList.size();
    }
}