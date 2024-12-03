package com.example.pokedexjavaapp.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pokedexjavaapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private List<PokemonEntity> pokemonList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PokemonEntity pokemon);
    }

    public PokemonAdapter(List<PokemonEntity> pokemonList, OnItemClickListener listener) {
        this.pokemonList = pokemonList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pokemon, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        PokemonEntity pokemon = pokemonList.get(position);

        String formattedId = String.format(Locale.getDefault(), "%03d", pokemon.getId());
        holder.pokemonIdTextView.setText(formattedId);
        holder.pokemonNameTextView.setText(pokemon.getName());
        Picasso.get().load(pokemon.getSpriteURL()).into(holder.pokemonSpriteImageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(pokemon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public void updatePokemonList(List<PokemonEntity> newPokemonList) {
        int initialSize = pokemonList.size();
        pokemonList.addAll(newPokemonList);
        notifyItemRangeInserted(initialSize, newPokemonList.size());
    }

    public void setPokemonList(List<PokemonEntity> newList) {
        pokemonList.clear();
        pokemonList.addAll(newList);
        notifyDataSetChanged();
    }

    public void clearPokemonList() {
        pokemonList.clear();
        notifyDataSetChanged();
    }

    static class PokemonViewHolder extends RecyclerView.ViewHolder {
        TextView pokemonIdTextView;
        ImageView pokemonSpriteImageView;
        TextView pokemonNameTextView;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            pokemonIdTextView = itemView.findViewById(R.id.pokemon_id_text_view);
            pokemonNameTextView = itemView.findViewById(R.id.pokemon_name_text_view);
            pokemonSpriteImageView = itemView.findViewById(R.id.pokemon_sprite_image_view);
        }
    }
}
