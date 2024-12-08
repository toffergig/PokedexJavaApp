//// File: PokemonDisplayHelper.java
//// Package: com.example.pokedexjavaapp.helpers
//
//package com.example.pokedexjavaapp.helpers;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.graphics.drawable.GradientDrawable;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.core.content.ContextCompat;
//
//import com.example.pokedexjavaapp.R;
//import com.example.pokedexjavaapp.models.PokemonDetails;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//import java.util.Locale;
//
///**
// * Helper class to manage displaying Pokémon details in the UI.
// */
//public class PokemonDisplayHelper {
//
//    private static final String TAG = "PokemonDisplayHelper";
//
//    private final Context context;
//    private final TextView nameTextView;
//    private final TextView idTextView;
//    private final ImageView imageView;
//    private final LinearLayout typesLayout;
//    private final LinearLayout abilitiesLayout;
//
//    /**
//     * Constructor to initialize the helper with required UI components.
//     *
//     * @param context          The context from the activity.
//     * @param nameTextView     TextView to display Pokémon name.
//     * @param idTextView       TextView to display Pokémon ID.
//     * @param imageView        ImageView to display Pokémon image.
//     * @param typesLayout      LinearLayout to display Pokémon types.
//     * @param abilitiesLayout  LinearLayout to display Pokémon abilities.
//     */
//    public PokemonDisplayHelper(Context context, TextView nameTextView, TextView idTextView, ImageView imageView,
//                                LinearLayout typesLayout, LinearLayout abilitiesLayout) {
//        this.context = context;
//        this.nameTextView = nameTextView;
//        this.idTextView = idTextView;
//        this.imageView = imageView;
//        this.typesLayout = typesLayout;
//        this.abilitiesLayout = abilitiesLayout;
//    }
//
//    /**
//     * Displays Pokémon details on the UI components.
//     *
//     * @param details The PokémonDetails object containing data.
//     */
//    public void displayPokemonDetails(PokemonDetails details) {
//        displayId(details.getId());
//        displayName(details.getName());
//        displayImage(details.getSprites().getFrontDefault());
//        displayTypes(details.getTypes());
//        displayAbilities(details.getAbilities());
//    }
//
//    /**
//     * Displays the Pokémon ID.
//     *
//     * @param id The Pokémon's ID.
//     */
//    private void displayId(int id) {
//        String formattedId = String.format(Locale.getDefault(), "#%03d", id);
//        idTextView.setText(formattedId);
//    }
//
//    /**
//     * Displays the Pokémon name.
//     *
//     * @param name The Pokémon's name.
//     */
//    private void displayName(String name) {
//        nameTextView.setText(capitalize(name));
//    }
//
//    /**
//     * Displays the Pokémon image using Picasso.
//     *
//     * @param imageUrl The URL of the Pokémon's image.
//     */
//    private void displayImage(String imageUrl) {
//        if (imageUrl != null && !imageUrl.isEmpty()) {
//            Picasso.get()
//                    .load(imageUrl)
//                    .placeholder(R.drawable.placeholder_image) // Ensure you have a placeholder image
//                    .error(R.drawable.error_image)             // Ensure you have an error image
//                    .into(imageView);
//        } else {
//            imageView.setImageResource(R.drawable.error_image); // Set to error image if URL is invalid
//        }
//    }
//
//    /**
//     * Displays the Pokémon types.
//     *
//     * @param types List of TypeInfo objects representing Pokémon types.
//     */
//    public void displayTypes(List<PokemonDetails.TypeInfo> types) {
//        // Remove existing type views but keep the label
//        if (typesLayout.getChildCount() > 1) {
//            typesLayout.removeViews(1, typesLayout.getChildCount() - 1);
//        }
//
//        for (PokemonDetails.TypeInfo typeInfo : types) {
//            String typeName = typeInfo.getType().getName();
//
//            // Create TextView for the type
//            TextView typeTextView = new TextView(context);
//            typeTextView.setText(typeName.toUpperCase(Locale.ROOT));
//            typeTextView.setTextColor(Color.WHITE);
//            typeTextView.setTypeface(null, Typeface.BOLD);
//            typeTextView.setPadding(16, 8, 16, 8);
//
//            // Set background with rounded corners and specific color
//            GradientDrawable bgDrawable = new GradientDrawable();
//            bgDrawable.setShape(GradientDrawable.RECTANGLE);
//            bgDrawable.setCornerRadius(16f);
//            bgDrawable.setColor(getTypeColor(typeName));
//            typeTextView.setBackground(bgDrawable);
//
//            // Set margins for horizontal spacing
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            params.setMargins(10, 0, 10, 0); // Left, Top, Right, Bottom
//            typeTextView.setLayoutParams(params);
//
//            // Add the TextView to the layout
//            typesLayout.addView(typeTextView);
//        }
//    }
//
//    /**
//     * Displays the Pokémon abilities.
//     *
//     * @param abilities List of AbilityInfo objects representing Pokémon abilities.
//     */
//    public void displayAbilities(List<PokemonDetails.AbilityInfo> abilities) {
//        // Remove existing ability views but keep the label
//        if (abilitiesLayout.getChildCount() > 1) {
//            abilitiesLayout.removeViews(1, abilitiesLayout.getChildCount() - 1);
//        }
//
//        // Limit to 4 abilities
//        int abilitiesToShow = Math.min(abilities.size(), 4);
//
//        for (int i = 0; i < abilitiesToShow; i++) {
//            PokemonDetails.AbilityInfo abilityInfo = abilities.get(i);
//            String abilityName = abilityInfo.getAbility().getName();
//
//            // Create TextView for the ability
//            TextView abilityTextView = new TextView(context);
//            abilityTextView.setText(capitalize(abilityName));
//            abilityTextView.setTextColor(Color.BLACK);
//            abilityTextView.setPadding(8, 4, 8, 4);
//
//            // Optionally, indicate if the ability is hidden
//            if (abilityInfo.isHidden()) {
//                abilityTextView.setTypeface(null, Typeface.ITALIC);
//                abilityTextView.setText(abilityTextView.getText() + " (Hidden)");
//            }
//
//            // Set layout parameters with margin
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            params.setMargins(0, 4, 0, 4);
//            abilityTextView.setLayoutParams(params);
//
//            // Add the TextView to the layout
//            abilitiesLayout.addView(abilityTextView);
//        }
//    }
//
//    /**
//     * Sets the background color for a Pokémon type.
//     *
//     * @param typeName The name of the Pokémon type.
//     * @return The color associated with the type.
//     */
//    private int getTypeColor(String typeName) {
//        // Assume that you have defined colors in colors.xml with names matching type names
//        int colorResourceId = context.getResources().getIdentifier(typeName, "color", context.getPackageName());
//        if (colorResourceId != 0) {
//            return ContextCompat.getColor(context, colorResourceId);
//        } else {
//            // Default color if type not found
//            return ContextCompat.getColor(context, R.color.default_type_color);
//        }
//    }
//
//    /**
//     * Capitalizes the first letter of a given string.
//     *
//     * @param text The input string.
//     * @return The capitalized string.
//     */
//    private String capitalize(String text) {
//        if (text == null || text.isEmpty()) return "";
//        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
//    }
//}
