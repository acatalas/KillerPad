package com.example.killerpad;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.example.killerpad.comunications.ShipType;
import com.example.killerpad.preferences_manager.SharedPreferencesManager;

public class ShipDialog extends Dialog {
    private ImageView batmobileButton;
    private ImageView octaneButton;
    private ImageView marauderButton;
    private int shipColor;

    public ShipDialog(@NonNull Context context) {
        super(context);
        shipColor = Color.parseColor(SharedPreferencesManager.getString(context,
                SharedPreferencesManager.COLOR_KEY,
                "#FF0000"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ship_picker);
        addShipListeners();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        changeShipColor(BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.octane, options), shipColor, R.id.octane);

        changeShipColor(BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.batmobile, options), shipColor, R.id.batmobile);

        changeShipColor(BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.marauder, options), shipColor, R.id.marauder);

    }


    private void addShipListeners(){
        batmobileButton = findViewById(R.id.batmobile);
        octaneButton = findViewById(R.id.octane);
        marauderButton = findViewById(R.id.marauder);

        batmobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.saveString(getContext(),
                        SharedPreferencesManager.SHIP_KEY,
                        ShipType.BATMOBILE.name());

                dismiss();
            }
        });

        octaneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.saveString(getContext(),
                        SharedPreferencesManager.SHIP_KEY,
                        ShipType.OCTANE.name());

                dismiss();
            }
        });

        marauderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.saveString(getContext(),
                        SharedPreferencesManager.SHIP_KEY,
                        ShipType.MARAUDER.name());
                dismiss();
            }
        });

    }


    public static Bitmap replaceGreenColor(Bitmap bitmap, int color) {
        // Source image size
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < pixels.length; ++x) {
            if (Color.green(pixels[x]) == Color.green(Color.GREEN)) {
                pixels[x] = color;
            }
        }

        Bitmap result = Bitmap.createBitmap(width, height, bitmap.getConfig());

        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }

    public void changeShipColor(Bitmap bitmap, int shipColor, int imageView){

        bitmap = replaceGreenColor(bitmap, shipColor);

        ((ImageView)findViewById(imageView)).setImageBitmap(bitmap);

    }
}
