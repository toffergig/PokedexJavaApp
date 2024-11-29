package com.example.pokedexjavaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class PokemonDetailsActivity extends AppCompatActivity {

    private TextView pokemonNameTextView;
    private ImageView pokemonImageView;
    private TextView pokemonIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_details_activity);

        // Initialize views
        pokemonNameTextView = findViewById(R.id.pokemon_detail_name);
        pokemonImageView = findViewById(R.id.pokemon_detail_image);
        pokemonIdTextView = findViewById(R.id.pokemon_detail_id);

        // Get data from Intent
        Intent intent = getIntent();
        int pokemonId = intent.getIntExtra("pokemon_id", -1);
        String pokemonName = intent.getStringExtra("pokemon_name");
        String pokemonSpriteUrl = intent.getStringExtra("pokemon_sprite_url");

        // Set data to views
        String formattedId = String.format(Locale.getDefault(), "%03d", pokemonId);
        pokemonIdTextView.setText(formattedId);
        pokemonNameTextView.setText(pokemonName);
        Picasso.get().load(pokemonSpriteUrl).into(pokemonImageView);

        // Optionally, fetch more details about the Pokémon here
    }
}
