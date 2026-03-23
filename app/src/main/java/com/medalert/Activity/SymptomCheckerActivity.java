package com.medalert.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.medalert.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SymptomCheckerActivity extends AppCompatActivity {

    private EditText symptomInput;
    private TextView resultsText, resultsLabel;
    private Button searchButton;
    private ImageButton btnBack;
    private ScrollView resultsContainer;

    private static final String AUTH_API_URL = "https://authservice.priaid.ch/login";
    private static final String SYMPTOM_API_URL = "https://healthservice.priaid.ch/symptoms";
    private static final String DIAGNOSIS_API_URL = "https://healthservice.priaid.ch/diagnosis";
    private static final String API_USERNAME = "Ly69Z_GMAIL_COM_AUT";
    private static final String API_PASSWORD = "k7T2WmSn64Xcr8PGi";
    private static final String API_LANGUAGE = "en-gb";

    private String apiToken;
    private boolean isAuthenticating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_checker);

        symptomInput = findViewById(R.id.symptomInput);
        resultsText = findViewById(R.id.resultsText);
        resultsLabel = findViewById(R.id.resultsLabel);
        searchButton = findViewById(R.id.searchButton);
        resultsContainer = findViewById(R.id.resultsContainer);
        btnBack = findViewById(R.id.btn_back);

        searchButton.setOnClickListener(v -> {
            String query = symptomInput.getText().toString().trim();
            if (!query.isEmpty()) {
                if (apiToken == null && !isAuthenticating) {
                    authenticateAndSearch(query);
                } else {
                    searchSymptoms(query);
                }
            } else {
                Toast.makeText(this, "Please enter a symptom", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SymptomCheckerActivity.this, NewHomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void authenticateAndSearch(String query) {
        isAuthenticating = true;
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                String uri = AUTH_API_URL;
                String hashedCredentials = generateHMACMD5(uri, API_PASSWORD);

                URL url = new URL(AUTH_API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + API_USERNAME + ":" + hashedCredentials);
                connection.setRequestProperty("Content-Type", "application/json");

                int responseCode = connection.getResponseCode();
                Log.d("AUTH_API_RESPONSE", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String response = readInputStream(connection);
                    Log.d("AUTH_API_DATA", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    apiToken = "Bearer " + jsonResponse.optString("Token", "");
                    runOnUiThread(() -> searchSymptoms(query));
                } else {
                    logErrorStream(connection);
                    handleAuthError(responseCode);
                }
            } catch (Exception e) {
                Log.e("AUTH_API_ERROR", "Authentication Failed", e);
                runOnUiThread(() -> Toast.makeText(this, "Authentication Error", Toast.LENGTH_SHORT).show());
            } finally {
                isAuthenticating = false;
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private String generateHMACMD5(String data, String key) throws Exception {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(hash);
            } else {
                // Fallback for older versions
                return android.util.Base64.encodeToString(hash, android.util.Base64.DEFAULT).trim();
            }
        } catch (Exception e) {
            Log.e("HMAC_ERROR", "Error generating HMACMD5", e);
            throw new SignatureException("Error generating HMACMD5: " + e.getMessage());
        }
    }



    private void handleAuthError(int responseCode) {
        runOnUiThread(() -> {
            String errorMessage;
            switch (responseCode) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    errorMessage = "Authentication failed. Please check your credentials.";
                    break;
                case HttpURLConnection.HTTP_FORBIDDEN:
                    errorMessage = "Access denied. Please verify your permissions.";
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    errorMessage = "Bad request. Please try again.";
                    break;
                default:
                    errorMessage = "An unknown error occurred. Please try again later.";
                    break;
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            Log.e("AUTH_ERROR", "Error Code: " + responseCode + " - " + errorMessage);
        });
    }
    private void handleApiError(int responseCode) {
        runOnUiThread(() -> {
            String errorMessage;
            switch (responseCode) {
                case HttpURLConnection.HTTP_NOT_FOUND:
                    errorMessage = "The requested resource was not found.";
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    errorMessage = "Server error. Please try again later.";
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    errorMessage = "Bad request. Please check your input.";
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    errorMessage = "Unauthorized access. Please verify your credentials.";
                    break;
                default:
                    errorMessage = "An unexpected error occurred (Code: " + responseCode + ").";
                    break;
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            Log.e("API_ERROR", "Error Code: " + responseCode + " - " + errorMessage);
        });
    }

    private void searchSymptoms(String query) {
        new Thread(() -> {
            if (apiToken == null || apiToken.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, "API token is missing. Please try again later.", Toast.LENGTH_SHORT).show());
                return;
            }

            HttpURLConnection connection = null;
            try {
                String urlString = SYMPTOM_API_URL + "?token=" + URLEncoder.encode(apiToken, "UTF-8") + "&language=" + API_LANGUAGE;
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", apiToken);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                Log.d("SYMPTOM_API_RESPONSE", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String response = readInputStream(connection);
                    Log.d("SYMPTOM_API_DATA", response);
                    runOnUiThread(() -> processSymptomResponse(response, query));
                } else {
                    logErrorStream(connection);
                    handleApiError(responseCode);
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to retrieve symptoms", Toast.LENGTH_SHORT).show());
                Log.e("SYMPTOM_API_ERROR", "Request Failed", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }


    private void processSymptomResponse(String response, String query) {
        try {
            JSONArray symptoms = new JSONArray(response);
            String[] inputSymptoms = query.split(","); // Split input by commas
            JSONArray matchedSymptomIds = new JSONArray();

            for (String input : inputSymptoms) {
                input = input.trim();
                for (int i = 0; i < symptoms.length(); i++) {
                    JSONObject symptom = symptoms.getJSONObject(i);
                    if (symptom.optString("Name", "").equalsIgnoreCase(input)) {
                        matchedSymptomIds.put(symptom.getInt("ID"));
                        break;
                    }
                }
            }

            if (matchedSymptomIds.length() > 0) {
                searchDiagnosis(matchedSymptomIds);
            } else {
                runOnUiThread(() -> resultsText.setText("No matching symptoms found."));
            }
        } catch (Exception e) {
            runOnUiThread(() -> resultsText.setText("Error processing symptoms."));
            Log.e("PROCESS_SYMPTOM_ERROR", "Parsing Failed", e);
        }
    }


    private void searchDiagnosis(JSONArray symptomIds) {
        new Thread(() -> {
            if (apiToken == null || apiToken.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, "API token is missing. Please try again later.", Toast.LENGTH_SHORT).show());
                return;
            }

            HttpURLConnection connection = null;
            try {
                String urlString = DIAGNOSIS_API_URL + "?token=" + URLEncoder.encode(apiToken, "UTF-8") +
                        "&language=" + API_LANGUAGE + "&symptoms=" + URLEncoder.encode(symptomIds.toString(), "UTF-8") +
                        "&gender=male&year_of_birth=1985";

                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", apiToken);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                Log.d("DIAGNOSIS_API_RESPONSE", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String response = readInputStream(connection);
                    Log.d("DIAGNOSIS_API_DATA", response);
                    runOnUiThread(() -> processDiagnosisResponse(response));
                } else {
                    logErrorStream(connection);
                    handleApiError(responseCode);
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to retrieve diagnosis", Toast.LENGTH_SHORT).show());
                Log.e("DIAGNOSIS_API_ERROR", "Request Failed", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }



    private void processDiagnosisResponse(String response) {
        try {
            JSONArray diagnoses = new JSONArray(response);

            StringBuilder resultsBuilder = new StringBuilder();

            for (int i = 0; i < diagnoses.length(); i++) {
                JSONObject diagnosis = diagnoses.getJSONObject(i);
                JSONObject issue = diagnosis.getJSONObject("Issue");
                String name = issue.optString("Name", "Unknown");
                String accuracy = issue.optString("Accuracy", "N/A");

                resultsBuilder.append("• ").append(name)
                        .append(" (Accuracy: ").append(accuracy).append("%)\n");
            }

            if (resultsBuilder.length() > 0) {
                runOnUiThread(() -> {
                    resultsText.setText(resultsBuilder.toString());
                    resultsLabel.setVisibility(View.VISIBLE);
                    resultsContainer.setVisibility(View.VISIBLE);
                });
            } else {
                runOnUiThread(() -> resultsText.setText("No diagnosis found."));
            }

        } catch (Exception e) {
            runOnUiThread(() -> resultsText.setText("Error processing diagnosis."));
            Log.e("PROCESS_DIAGNOSIS_ERROR", "Parsing Failed", e);
        }
    }

    private String readInputStream(HttpURLConnection connection) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8")
        );
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line.trim());
        }
        reader.close();
        return response.toString();
    }

    private void logErrorStream(HttpURLConnection connection) throws Exception {
        if (connection.getErrorStream() != null) {
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), "UTF-8")
            );
            StringBuilder errorResponse = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine.trim());
            }
            errorReader.close();
            Log.e("API_ERROR", "Error Response: " + errorResponse.toString());
        } else {
            Log.e("API_ERROR", "No error stream available.");
        }
    }
}
