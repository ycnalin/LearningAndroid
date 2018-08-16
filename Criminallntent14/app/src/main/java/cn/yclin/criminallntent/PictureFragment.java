package cn.yclin.criminallntent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class PictureFragment extends DialogFragment {
        public static final String ARG_PICTURE = "picture";

        private ImageView mImageView;

    public static PictureFragment newInstance(String filePath){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PICTURE, filePath);
        PictureFragment dialog = new PictureFragment();
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_picture, null);
        mImageView = v.findViewById(R.id.my_image_view);

        String filePath = (String)getArguments().getSerializable(ARG_PICTURE);

        Bitmap bitmap = PictureUtils.getScaledBitmap(
                filePath, getActivity());
        mImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
}

