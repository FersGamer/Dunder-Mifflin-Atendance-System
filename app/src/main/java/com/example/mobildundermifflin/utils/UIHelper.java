package com.example.mobildundermifflin.utils;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.example.mobildundermifflin.R;
import com.example.mobildundermifflin.SessionManager;
import com.google.android.material.imageview.ShapeableImageView;

public class UIHelper {
    public static void cargarFotoToolbar(Context ctx, ShapeableImageView ivToolbar) {
        String fotoUrl = SessionManager.getFotoUrl(ctx);
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(ctx)
                    .load(fotoUrl)
                    .placeholder(R.drawable.ejemplo)
                    .circleCrop()
                    .into(ivToolbar);
        }
    }
}
