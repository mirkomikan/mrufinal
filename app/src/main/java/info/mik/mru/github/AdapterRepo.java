package info.mik.mru.github;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import info.mik.mru.R;

/**
 * Created by mik on 2018-05-16.
 */

public class AdapterRepo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ModelRepo> repositories;

    public AdapterRepo(List<ModelRepo> repositories) {
        this.repositories = repositories;
    }

    @Override
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_github, parent, false);

        return new RepositoryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final RepositoryViewHolder RepositoryVH = (RepositoryViewHolder) holder;

        // bind Repo name
        RepositoryVH.name.setText(repositories.get(position).getmName());

        // bind Repo desc
        RepositoryVH.description.setText(repositories.get(position).getmDescription());

        // bind Repo owner's login (username)
        RepositoryVH.login.setText("@" + repositories.get(position).getOwner().getLogin());

        // bind Repo size, first simplify it to Xk view
        int size = repositories.get(position).getmSize(); // return the size divided by 1000
        if (size > 1000) size /= 1000;
        repositories.get(position).setmSize(size);
        // get only the first digit after comma and then append the value with 'k'
        RepositoryVH.size.setText(new DecimalFormat("##.#").format(size) + "k");

        // if Repo has_wiki, set bgr to ultra light yellow
        Boolean hasWiki = repositories.get(position).getmHasWiki();
        if(hasWiki) {
            //holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE")); // ultra light gray
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFBB")); // ultra light yellow
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }

    public static class RepositoryViewHolder extends  RecyclerView.ViewHolder {
        TextView name, description, login, size;

        public RepositoryViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            login = itemView.findViewById(R.id.login);
            size = itemView.findViewById(R.id.size);
        }
    }

}
