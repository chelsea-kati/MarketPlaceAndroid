package com.example.marketplaceandroid.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.marketplaceandroid.R;
import com.example.marketplaceandroid.adapters.AnnonceAdapter;
import com.example.marketplaceandroid.adapters.CategoryAdapter;
import com.example.marketplaceandroid.models.Annonce;
import com.example.marketplaceandroid.models.User;
import com.example.marketplaceandroid.services.ApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ListView lvAnnonces;
    private RecyclerView rvCategories;
    private LinearLayout llSearchBar, llEmptyState;
    private TextView tvVoirTout;
    private FloatingActionButton fabAddAnnonce;
    private ProgressBar pbLoading;

    private ApiService apiService;
    private AnnonceAdapter annonceAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Annonce> annoncesList;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupToolbar();
        setupAdapters();
        setupClickListeners();

        apiService = ApiService.getInstance(this);
        currentUser = apiService.getCurrentUser();

        loadAnnonces();
    }
    private void initViews() {
        lvAnnonces = findViewById(R.id.lv_annonces);
        rvCategories = findViewById(R.id.rv_categories);
        llSearchBar = findViewById(R.id.ll_search_bar);
        llEmptyState = findViewById(R.id.ll_empty_state);
        tvVoirTout = findViewById(R.id.tv_voir_tout);
        fabAddAnnonce = findViewById(R.id.fab_add_annonce);
        pbLoading = findViewById(R.id.pb_loading);
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Marketplace");
        }
    }

    private void setupAdapters() {
        // Adapter pour les annonces
        annoncesList = new ArrayList<>();
        annonceAdapter = new AnnonceAdapter(this, annoncesList, new AnnonceAdapter.OnAnnonceClickListener() {
            @Override
            public void onAnnonceClick(Annonce annonce) {
                openAnnonceDetail(annonce);
            }
            @Override
            public void onFavoriteClick(Annonce annonce, int position) {
                toggleFavorite(annonce, position);
            }
        });
        lvAnnonces.setAdapter(annonceAdapter);

        // Adapter pour les catégories
        List<String> categories = Arrays.asList(
                "Électronique", "Vêtements", "Maison", "Auto",
                "Sports", "Livres", "Jeux", "Autres"
        );
        categoryAdapter = new CategoryAdapter(categories, category -> {
            // Filtrer par catégorie
            filterByCategory(category);
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
    }
    private void setupClickListeners() {
        llSearchBar.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        tvVoirTout.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("show_all", true);
            startActivity(intent);
        });
        fabAddAnnonce.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAnnonceActivity.class);
            startActivity(intent);
        });
    }

    private void loadAnnonces() {
        showLoading(true);

        apiService.getAnnonces(new ApiService.ApiCallback<List<Annonce>>() {
            @Override
            public void onSuccess(List<Annonce> annonces) {
                runOnUiThread(() -> {
                    showLoading(false);
                    annoncesList.clear();
                    annoncesList.addAll(annonces);
                    annonceAdapter.notifyDataSetChanged();

                    if (annonces.isEmpty()) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                    }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showEmptyState(true);
                    Toast.makeText(HomeActivity.this,
                            "Erreur lors du chargement: " + error,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private void toggleFavorite(Annonce annonce, int position) {
        if (annonce.isFavorite()) {
            // TODO: Implémenter la suppression des favoris
            Toast.makeText(this, "Retirer des favoris", Toast.LENGTH_SHORT).show();
        } else {
            apiService.addToFavoris(annonce.getId(), new ApiService.ApiCallback<com.marketplace.app.models.Favori>() {
                @Override
                public void onSuccess(com.marketplace.app.models.Favori favori) {
                    runOnUiThread(() -> {
                        annonce.setFavorite(true);
                        annonceAdapter.notifyItemChanged(position);
                        Toast.makeText(HomeActivity.this,
                                "❤️ Ajouté aux favoris",
                                Toast.LENGTH_SHORT).show();
                    });
                }
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(HomeActivity.this,
                                "Erreur: " + error,
                                Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }

    private void filterByCategory(String category) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("category_filter", category);
        startActivity(intent);
    }
    private void openAnnonceDetail(Annonce annonce) {
        Intent intent = new Intent(this, AnnonceDetailActivity.class);
        intent.putExtra("annonce_id", annonce.getId());
        startActivity(intent);
    }

    private void showLoading(boolean show) {
        if (show) {
            pbLoading.setVisibility(View.VISIBLE);
            lvAnnonces.setVisibility(View.GONE);
        } else {
            pbLoading.setVisibility(View.GONE);
            lvAnnonces.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState(boolean show) {
        if (show) {
            llEmptyState.setVisibility(View.VISIBLE);
            lvAnnonces.setVisibility(View.GONE);
        } else {
            llEmptyState.setVisibility(View.GONE);
            lvAnnonces.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_favoris) {
            Intent intent = new Intent(this, FavorisActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_messages) {
            Intent intent = new Intent(this, MessagesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_profil) {
            Intent intent = new Intent(this, ProfilActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        apiService.logout();
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les annonces quand on revient sur l'activité
        loadAnnonces();
    }

    }




