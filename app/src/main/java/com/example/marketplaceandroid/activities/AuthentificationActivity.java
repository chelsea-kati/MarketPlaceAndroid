package com.example.marketplaceandroid.activities;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.example.marketplaceandroid.R;
import com.example.marketplaceandroid.models.User;
import com.example.marketplaceandroid.services.ApiService;

public class AuthentificationActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etAdresse, etNom, etTelephone;
    private Button btnAuth;
    private TextView tvSwitchMode, tvFormTitle;
    private LinearLayout llRegisterFields;
    private ProgressBar pbLoading;
    private ApiService apiService;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);
        // Vérifier si l'utilisateur est déjà connecté
        apiService = ApiService.getInstance(this);
        if (apiService.isLoggedIn()) {
            redirectToHome();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etAdresse = findViewById(R.id.et_adresse);
        etNom = findViewById(R.id.et_nom);
        etTelephone = findViewById(R.id.et_telephone);
        btnAuth = findViewById(R.id.btn_auth);
        tvSwitchMode = findViewById(R.id.tv_switch_mode);
        tvFormTitle = findViewById(R.id.tv_form_title);
        llRegisterFields = findViewById(R.id.ll_register_fields);
        pbLoading = findViewById(R.id.pb_loading);
    }

    private void setupClickListeners() {
        btnAuth.setOnClickListener(v -> {
            if (isLoginMode) {
                performLogin();
            } else {
                performRegister();
            }
        });

        tvSwitchMode.setOnClickListener(v -> toggleMode());
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;

        if (isLoginMode) {
            // Mode connexion
            tvFormTitle.setText("Connexion");
            btnAuth.setText("Se connecter");
            tvSwitchMode.setText("Pas encore de compte ? S'inscrire");
            llRegisterFields.setVisibility(View.GONE);
        } else {
            // Mode inscription
            tvFormTitle.setText("Inscription");
            btnAuth.setText("S'inscrire");
            tvSwitchMode.setText("Déjà un compte ? Se connecter");
            llRegisterFields.setVisibility(View.VISIBLE);
        }

        // Vider les champs
        clearFields();
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (!validateLoginInputs(email, password)) {
            return;
        }

        showLoading(true);

        apiService.login(email, password, new ApiService.ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AuthentificationActivity.this,
                            "Connexion réussie ! Bienvenue " + user.getAdresse(),
                            Toast.LENGTH_SHORT).show();
                    redirectToHome();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AuthentificationActivity.this,
                            "Erreur de connexion: " + error,
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void performRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String adresse = etAdresse.getText().toString().trim();
        String nom = etNom.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();

        // Validation
        if (!validateRegisterInputs(email, password, adresse, nom, telephone)) {
            return;
        }

        showLoading(true);

        User newUser = new User(nom, adresse, email, telephone, password);

        apiService.register(newUser, password, new ApiService.ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AuthentificationActivity.this,
                            "Inscription réussie ! Vous pouvez maintenant vous connecter",
                            Toast.LENGTH_LONG).show();

                    // Basculer vers le mode connexion
                    toggleMode();
                    etEmail.setText(email);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AuthentificationActivity.this,
                            "Erreur d'inscription: " + error,
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private boolean validateLoginInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email requis");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format d'email invalide");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Mot de passe requis");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Le mot de passe doit contenir au moins 6 caractères");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateRegisterInputs(String email, String password, String adresse, String nom, String telephone) {
        if (TextUtils.isEmpty(adresse)) {
            etAdresse.setError("Adresse requise");
            etAdresse.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(nom)) {
            etNom.setError("Nom requis");
            etNom.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(telephone)) {
            etTelephone.setError("Téléphone requis");
            etTelephone.requestFocus();
            return false;
        }

        if (telephone.length() < 10) {
            etTelephone.setError("Numéro de téléphone invalide");
            etTelephone.requestFocus();
            return false;
        }

        return validateLoginInputs(email, password);
    }

    private void showLoading(boolean show) {
        if (show) {
            pbLoading.setVisibility(View.VISIBLE);
            btnAuth.setEnabled(false);
            btnAuth.setAlpha(0.6f);
        } else {
            pbLoading.setVisibility(View.GONE);
            btnAuth.setEnabled(true);
            btnAuth.setAlpha(1.0f);
        }
    }

    private void clearFields() {
        etEmail.setText("");
        etPassword.setText("");
        etAdresse.setText("");
        etNom.setText("");
        etTelephone.setText("");

        // Supprimer les erreurs
        etEmail.setError(null);
        etPassword.setError(null);
        etAdresse.setError(null);
        etNom.setError(null);
        etTelephone.setError(null);
    }

    private void redirectToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Empêcher le retour en arrière depuis l'écran d'authentification
        super.onBackPressed(); // Ajouter cette ligne pour éviter l'erreur
        moveTaskToBack(true);
    }
}
