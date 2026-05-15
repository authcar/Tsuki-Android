package com.example.tsuki;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralized helper for all Firestore read/write operations.
 * Structure:
 *   users/{uid}/profile      → name, email, birthday
 *   users/{uid}/cycle        → cycleLength, periodLength, periodStartDay/Month/Year
 *   users/{uid}/logs/{date}  → flow, symptoms, moods
 */
public class FirestoreManager {

    private static final String COLLECTION_USERS = "users";
    private static final String DOC_PROFILE      = "profile";
    private static final String DOC_CYCLE        = "cycle";
    private static final String COLLECTION_LOGS  = "logs";

    private final FirebaseFirestore db;
    private final String uid;

    public interface OnSuccessListener { void onSuccess(); }
    public interface OnFailureListener { void onFailure(Exception e); }
    public interface OnDataListener    { void onData(Map<String, Object> data); }

    public FirestoreManager() {
        db  = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    private boolean isLoggedIn() { return uid != null; }

    // ─── Profile ──────────────────────────────────────────────────────────────

    public void saveProfile(String name, String email,
                            OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (!isLoggedIn()) return;
        Map<String, Object> data = new HashMap<>();
        data.put("name",  name);
        data.put("email", email);
        db.collection(COLLECTION_USERS).document(uid)
                .collection("data").document(DOC_PROFILE)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(v -> { if (onSuccess != null) onSuccess.onSuccess(); })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.onFailure(e); });
    }

    public void getProfile(OnDataListener onData, OnFailureListener onFailure) {
        if (!isLoggedIn()) return;
        db.collection(COLLECTION_USERS).document(uid)
                .collection("data").document(DOC_PROFILE)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && onData != null) onData.onData(doc.getData());
                })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.onFailure(e); });
    }

    // ─── Cycle settings ───────────────────────────────────────────────────────

    public void saveCycleData(int periodStartDay, int periodStartMonth, int periodStartYear,
                              int periodLength, int cycleLength,
                              OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (!isLoggedIn()) return;
        Map<String, Object> data = new HashMap<>();
        data.put("periodStartDay",   periodStartDay);
        data.put("periodStartMonth", periodStartMonth);
        data.put("periodStartYear",  periodStartYear);
        data.put("periodLength",     periodLength);
        data.put("cycleLength",      cycleLength);
        db.collection(COLLECTION_USERS).document(uid)
                .collection("data").document(DOC_CYCLE)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(v -> { if (onSuccess != null) onSuccess.onSuccess(); })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.onFailure(e); });
    }

    public void getCycleData(OnDataListener onData, OnFailureListener onFailure) {
        if (!isLoggedIn()) return;
        db.collection(COLLECTION_USERS).document(uid)
                .collection("data").document(DOC_CYCLE)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && onData != null) onData.onData(doc.getData());
                })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.onFailure(e); });
    }

    // ─── Daily log ────────────────────────────────────────────────────────────

    /**
     * @param dateKey format "YYYY-MM-DD", used as document ID
     */
    public void saveLog(String dateKey, String flow,
                        List<String> symptoms, List<String> moods,
                        OnSuccessListener onSuccess, OnFailureListener onFailure) {
        if (!isLoggedIn()) return;
        Map<String, Object> data = new HashMap<>();
        data.put("date",     dateKey);
        data.put("flow",     flow != null ? flow : "");
        data.put("symptoms", symptoms);
        data.put("moods",    moods);
        db.collection(COLLECTION_USERS).document(uid)
                .collection(COLLECTION_LOGS).document(dateKey)
                .set(data)
                .addOnSuccessListener(v -> { if (onSuccess != null) onSuccess.onSuccess(); })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.onFailure(e); });
    }

    public void getLog(String dateKey, OnDataListener onData, OnFailureListener onFailure) {
        if (!isLoggedIn()) return;
        db.collection(COLLECTION_USERS).document(uid)
                .collection(COLLECTION_LOGS).document(dateKey)
                .get()
                .addOnSuccessListener(doc -> {
                    if (onData != null) onData.onData(doc.exists() ? doc.getData() : null);
                })
                .addOnFailureListener(e -> { if (onFailure != null) onFailure.onFailure(e); });
    }
}
