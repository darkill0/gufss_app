package com.gufsspapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gufsspapp.R;
import com.gufsspapp.adapters.DocumentAdapter;
import com.gufsspapp.db.UserFileManager;
import com.gufsspapp.models.Document;

import java.util.List;
import java.util.stream.Collectors;

public class AdminDocumentsFragment extends Fragment {

    private DocumentAdapter adapter;
    private UserFileManager db;
    private List<Document> allDocs;
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_documents, container, false);

        db = UserFileManager.getInstance(requireContext());
        allDocs = db.getAllDocuments();

        RecyclerView rv = view.findViewById(R.id.rvDocuments);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new DocumentAdapter(allDocs, doc -> showDocOptions(doc));
        rv.setAdapter(adapter);

        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                filterDocs();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        view.findViewById(R.id.btnAdd).setOnClickListener(v -> showAddDocDialog());

        return view;
    }

    private void filterDocs() {
        List<Document> filtered = allDocs;
        if (!searchQuery.isEmpty()) {
            String q = searchQuery.toLowerCase();
            filtered = allDocs.stream()
                    .filter(d -> d.getTitle().toLowerCase().contains(q) ||
                            d.getAuthor().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
        adapter.updateData(filtered);
    }

    private void showDocOptions(Document doc) {
        new AlertDialog.Builder(requireContext())
                .setTitle(doc.getTitle())
                .setItems(new String[]{"Информация", "Удалить"}, (d, which) -> {
                    if (which == 0) {
                        Toast.makeText(requireContext(),
                                "Автор: " + doc.getAuthor() +
                                        "\nДата: " + doc.getCreatedDate() +
                                        "\nСтатус: " + doc.getStatus(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setTitle(getString(R.string.confirm_delete))
                                .setPositiveButton("Удалить", (dd, ww) -> {
                                    db.deleteDocument(doc.getId());
                                    allDocs = db.getAllDocuments();
                                    filterDocs();
                                    Toast.makeText(requireContext(),
                                            "Документ удалён", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Отмена", null)
                                .show();
                    }
                })
                .show();
    }

    private void showAddDocDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(20);
        layout.setPadding(pad, dpToPx(12), pad, dpToPx(8));

        EditText etTitle = createEditText("Название документа", "");
        EditText etDesc = createEditText("Описание", "");
        EditText etAuthor = createEditText("Автор", "");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(8), 0, 0);

        etDesc.setLayoutParams(params);
        etAuthor.setLayoutParams(params);

        Spinner spType = new Spinner(requireContext());
        String[] types = {"Отчёт", "Приказ", "Служебная записка", "Запрос", "Постановление"};
        ArrayAdapter<String> ta = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, types);
        ta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(ta);
        spType.setLayoutParams(params);

        Spinner spCat = new Spinner(requireContext());
        String[] cats = {"Входящие", "Исходящие", "Черновик"};
        ArrayAdapter<String> ca = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, cats);
        ca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCat.setAdapter(ca);
        spCat.setLayoutParams(params);

        layout.addView(etTitle);
        layout.addView(etDesc);
        layout.addView(etAuthor);
        layout.addView(spType);
        layout.addView(spCat);

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.new_document))
                .setView(layout)
                .setPositiveButton(getString(R.string.save), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(requireContext(),
                                getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] typeKeys = {"report", "decree", "memo", "request", "resolution"};
                    String[] catKeys = {"incoming", "outgoing", "draft"};

                    Document doc = new Document();
                    doc.setTitle(title);
                    doc.setDescription(etDesc.getText().toString().trim());
                    doc.setAuthor(etAuthor.getText().toString().trim().isEmpty() ?
                            "Иванов И.И." : etAuthor.getText().toString().trim());
                    doc.setType(typeKeys[spType.getSelectedItemPosition()]);
                    doc.setCategory(catKeys[spCat.getSelectedItemPosition()]);
                    doc.setCreatedDate("15.05.2024");
                    doc.setStatus("pending");
                    doc.setSize("PDF - 0.1 MB");

                    db.addDocument(doc);
                    allDocs = db.getAllDocuments();
                    filterDocs();
                    Toast.makeText(requireContext(),
                            getString(R.string.doc_added), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private EditText createEditText(String hint, String value) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        et.setText(value);
        et.setTextColor(0xFFE6EDF3);
        et.setHintTextColor(0xFF6E7681);
        et.setBackgroundResource(R.drawable.input_bg);
        et.setPadding(dpToPx(10), dpToPx(8), dpToPx(10), dpToPx(8));
        return et;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
