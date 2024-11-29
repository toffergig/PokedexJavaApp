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

    private List<Pokemon> pokemonList;

    public PokemonAdapter(List<Pokemon> pokemonList) {
        this.pokemonList = pokemonList;
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
        Pokemon pokemon = pokemonList.get(position);
        //holder.pokemonNameTextView.setText(pokemon.getName());

        // Display ID, name, and sprite
        pokemon.extractIdAndSpriteURL(); // Extract ID and sprite URL
        String formattedId = String.format(Locale.getDefault(), "%03d", pokemon.getId()); // Format to 3 digits with locale
        holder.pokemonIdTextView.setText(formattedId);
        holder.pokemonNameTextView.setText(pokemon.getName());
        Picasso.get().load(pokemon.getSpriteURL()).into(holder.pokemonSpriteImageView);

        // Load the sprite image using Picasso
//        pokemon.extractSpriteURL();  // Extract the sprite URL
//        holder.pokemonIdTextView.setText(String.valueOf(pokemon.getId())); // Display ID
//        Picasso.get().load(pokemon.getSpriteURL()).into(holder.pokemonSpriteImageView);
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public void updatePokemonList(List<Pokemon> newPokemonList) {
        pokemonList.clear();
        pokemonList.addAll(newPokemonList);
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
            pokemonSpriteImageView = itemView.findViewById(R.id.pokemon_sprite_image_view); // Initialize ImageView
        }
    }
}