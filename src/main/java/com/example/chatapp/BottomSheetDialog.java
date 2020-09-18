package com.example.chatapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {


    ImageView imgBtn, docBtn;
    private ItemClickListener mListener;
    String select = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);


        return view;


    }

    public interface ItemClickListener {
        void onItemClick(String item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.img_btn).setOnClickListener(this);
        view.findViewById(R.id.doc_btn).setOnClickListener(this);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }


    }

    @Override
    public void onClick(View v) {


        int id = v.getId();

        if (id == R.id.img_btn) {
            select = "image";
            mListener.onItemClick(select);
            dismiss();
        } else if (id == R.id.doc_btn) {
            select = "doc";
            mListener.onItemClick(select);
            dismiss();


        }

    }
}
