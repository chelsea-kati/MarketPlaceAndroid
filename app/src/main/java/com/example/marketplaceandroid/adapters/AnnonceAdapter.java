package com.example.marketplaceandroid.adapters;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marketplaceandroid.R;
import com.example.marketplaceandroid.models.Annonce;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class AnnonceAdapter extends BaseAdapter {
    private Context context;
    private List<Annonce> annonces;
    private LayoutInflater inflater;
    private OnAnnonceClickListener listener;

    public interface OnAnnonceClickListener {
        void onAnnonceClick(Annonce annonce);
        void onFavoriteClick(Annonce annonce, int position);
    }

    public AnnonceAdapter(Context context, List<Annonce> annonces, OnAnnonceClickListener listener) {
        this.context = context;
        this.annonces = annonces;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return annonces.size();
    }

    @Override
    public Object getItem(int position) {
        return annonces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return annonces.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_annonce, parent, false);
            holder = new ViewHolder();
            holder.ivAnnonceImage = convertView.findViewById(R.id.iv_annonce_image);
            holder.tvAnnonceTitre = convertView.findViewById(R.id.tv_annonce_titre);
            holder.tvAnnoncePrix = convertView.findViewById(R.id.tv_annonce_prix);
            holder.tvAnnonceDescription = convertView.findViewById(R.id.tv_annonce_description);
            holder.tvAnnonceCategorie = convertView.findViewById(R.id.tv_annonce_categorie);
            holder.tvAnnonceLocalisation = convertView.findViewById(R.id.tv_annonce_localisation);
            holder.tvAnnonceDate = convertView.findViewById(R.id.tv_annonce_date);
            holder.tvAnnonceVendeur = convertView.findViewById(R.id.tv_annonce_vendeur);
            holder.ivFavorite = convertView.findViewById(R.id.iv_favorite);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Annonce annonce = annonces.get(position);
        bindData(holder, annonce, position);

        return convertView;
    }

    private void bindData(ViewHolder holder, Annonce annonce, int position) {
        // Titre
        holder.tvAnnonceTitre.setText(annonce.getTitre());

        // Prix
        DecimalFormat df = new DecimalFormat("#,##0.00");
        holder.tvAnnoncePrix.setText(df.format(annonce.getPrix()) + " €");

        // Description
        holder.tvAnnonceDescription.setText(annonce.getDescription());

        // Catégorie
        holder.tvAnnonceCategorie.setText(annonce.getCategorie());

        // Localisation
        holder.tvAnnonceLocalisation.setText(annonce.getLocalisation());

        // Date
        if (annonce.getDateCreation() != null) {
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                    annonce.getDateCreation().getTime(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
            );
            holder.tvAnnonceDate.setText(relativeTime);
        }

        // Vendeur
        if (annonce.getVendeur() != null) {
            holder.tvAnnonceVendeur.setText("Par " + annonce.getVendeur().getNomComplet());
        }

        // Image
        if (annonce.getImages() != null && !annonce.getImages().isEmpty()) {
            Picasso.get()
                    .load(annonce.getImages().get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .fit()
                    .centerCrop()
                    .into(holder.ivAnnonceImage);
        } else {
            holder.ivAnnonceImage.setImageResource(R.drawable.placeholder_image);
        }

        // Bouton favori
        updateFavoriteIcon(holder.ivFavorite, annonce.isFavorite());
        holder.ivFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(annonce, position);
            }
        });

        // Click sur l'annonce
        holder.itemView = (View) holder.ivAnnonceImage.getParent().getParent();
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnnonceClick(annonce);
            }
        });
    }

    private void updateFavoriteIcon(ImageView ivFavorite, boolean isFavorite) {
        if (isFavorite) {
            ivFavorite.setImageResource(R.drawable.ic_favorite_filled);
            ivFavorite.setColorFilter(context.getResources().getColor(R.color.accent_color));
        } else {
            ivFavorite.setImageResource(R.drawable.ic_favorite_border);
            ivFavorite.setColorFilter(context.getResources().getColor(R.color.text_secondary));
        }
    }

    public void notifyItemChanged(int position) {
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView ivAnnonceImage;
        TextView tvAnnonceTitre;
        TextView tvAnnoncePrix;
        TextView tvAnnonceDescription;
        TextView tvAnnonceCategorie;
        TextView tvAnnonceLocalisation;
        TextView tvAnnonceDate;
        TextView tvAnnonceVendeur;
        ImageView ivFavorite;
        View itemView;
    }

}
