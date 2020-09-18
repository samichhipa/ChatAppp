package com.example.chatapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomdeleteSheet extends BottomSheetDialogFragment implements View.OnClickListener{

    LinearLayout delete_msg;
    private BottomdeleteSheet.ItemClickListener mListener;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_bottom_sheet_layout, container, false);


        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.delete_msg).setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof BottomdeleteSheet.ItemClickListener) {
            mListener = (BottomdeleteSheet.ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }

    }

    @Override
    public void onClick(View v) {

        mListener.onItemClick();

    }

    public interface ItemClickListener {
        void onItemClick();
    }
}
