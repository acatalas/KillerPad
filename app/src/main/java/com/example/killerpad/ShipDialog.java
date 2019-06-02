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

import com.example.killerpad.comunications.ConnectionResponse;
import com.example.killerpad.preferences_manager.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

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

        changeShipColor(getContext(), shipColor);

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
                        ConnectionResponse.ShipType.BATMOBILE.name());

                dismiss();
            }
        });

        octaneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.saveString(getContext(),
                        SharedPreferencesManager.SHIP_KEY,
                        ConnectionResponse.ShipType.OCTANE.name());

                dismiss();
            }
        });

        marauderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.saveString(getContext(),
                        SharedPreferencesManager.SHIP_KEY,
                        ConnectionResponse.ShipType.MARAUDER.name());
                dismiss();
            }
        });

    }

    public Bitmap replaceColor(Bitmap src,int fromColor, int targetColor) {
        List<Integer> pixelList = new ArrayList<>();

        if(src == null) {
            return null;
        }

        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();

        int[] pixels = new int[width * height];

        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < pixels.length; ++x) {
            if(pixels[x] == fromColor){
                pixels[x] = targetColor;
            }
        }

        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());

        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }

    public void changeShipColor(Context context, int shipColor){

        Bitmap bitmap = replaceColor(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.octane),
                Color.GREEN, shipColor);

        ((ImageView)findViewById(R.id.octane)).setImageBitmap(bitmap);

        bitmap = replaceColor(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.batmobile),
                Color.GREEN, shipColor);
        ((ImageView)findViewById(R.id.batmobile)).setImageBitmap(bitmap);

        bitmap = replaceColor(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.marauder),
                Color.GREEN, shipColor);

        ((ImageView)findViewById(R.id.marauder)).setImageBitmap(bitmap);

    }
}
