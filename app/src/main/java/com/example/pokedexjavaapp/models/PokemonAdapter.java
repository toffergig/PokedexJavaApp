package com.example.pokedexjavaapp.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexjavaapp.R;

import java.util.List;

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
        holder.pokemonNameTextView.setText(pokemon.getName());
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
        TextView pokemonNameTextView;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            pokemonNameTextView = itemView.findViewById(R.id.pokemon_name_text_view);
        }
    }
}