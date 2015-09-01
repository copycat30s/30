package com.yahoo.apps.thirty.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yahoo.apps.thirty.R;

/**
 * Created by hsuanlee on 8/11/15.
 */
public class StatusDialog extends DialogFragment {
    private EditText etStatus;
    private TextView tvCharsLeft;
    private String targetId;

    public StatusDialog() {
    }

    public static StatusDialog newInstance() {
        StatusDialog frag = new StatusDialog();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_post, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        targetId = getArguments().getString("targetId");

        etStatus = (EditText) view.findViewById(R.id.etStatus);
        tvCharsLeft = (TextView) view.findViewById(R.id.tvCharsLeft);
        Button btnPost = (Button) view.findViewById(R.id.btnPost);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusDialogListener listener = (StatusDialogListener) getActivity();

                listener.onFinishStatusDialog(targetId, etStatus.getText().toString());
                dismiss();
            }
        });

        etStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                tvCharsLeft.setText("" + (140 - s.length()) + " left");
            }
        });


        return view;
    }

    public interface StatusDialogListener {
        void onFinishStatusDialog(String targetId,String status);
    }
}
