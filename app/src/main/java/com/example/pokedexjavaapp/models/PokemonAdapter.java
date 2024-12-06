package com.example.pokedexjavaapp.models;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pokedexjavaapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import androidx.core.content.ContextCompat;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private final List<PokemonEntity> pokemonList;
    private final List<PokemonEntity> selectedPokemons = new ArrayList<>();

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(PokemonEntity pokemon);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(PokemonEntity pokemon);
    }

    public PokemonAdapter(List<PokemonEntity> pokemonList) {
        this.pokemonList = pokemonList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    // Update selected Pokémon list and refresh UI
    public void setSelectedPokemons(List<PokemonEntity> selected) {
        selectedPokemons.clear();
        selectedPokemons.addAll(selected);
        notifyDataSetChanged();
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

        // Highlight if selected
//        if (selectedPokemons.contains(pokemon)) {
//            holder.itemView.setBackgroundColor(Color.parseColor("#ebf5fb"));
//        } else {
//            holder.itemView.setBackgroundColor(Color.WHITE);
//        }

        int backgroundColor = selectedPokemons.contains(pokemon)
                ? ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_card_color)
                : Color.WHITE;
        ((CardView) holder.itemView).setCardBackgroundColor(backgroundColor);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(pokemon);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(pokemon);
                return true;
            }
            return false;
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
