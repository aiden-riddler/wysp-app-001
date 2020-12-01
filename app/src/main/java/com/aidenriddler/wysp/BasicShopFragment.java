package com.aidenriddler.wysp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class BasicShopFragment extends Fragment {

    //declaration
    private TextView add_product;
    private CardView addProductCard;
    private TextView setup_location;
    private CardView setupLocationCard;
    private String ShopTypeName;

    public BasicShopFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialisation
        add_product = view.findViewById(R.id.firstTextView);
        setup_location = view.findViewById(R.id.second_TextView);
        addProductCard = view.findViewById(R.id.add_product_card);
        setupLocationCard = view.findViewById(R.id.setup_location_card);

        switch (ShopTypeName){
            case "GAS STATION":
                add_product.setText("ADD FUEL/SERVICE");
                break;
            case "HAIRDRESSER/BARBER SHOP":
                add_product.setText("ADD SERVICE");
                break;
            case "NEWSAGENT":
                add_product.setText("ADD SERVICE");
                break;
            case "MOVERS AND PARKERS":
                add_product.setText("ADD SERVICE");
                setup_location.setText("SETUP OFFICE LOCATION");
                break;
            case "TRAVEL AGENT/TRAVEL AGENCY":
                add_product.setText("ADD JOURNEY");
                setup_location.setText("SETUP AGENCY LOCATION");
                break;
            case "TILL/CASH REGISTER":
            case "WYSP AGENT":
                add_product.setText("SETUP LOCATION");
                setup_location.setText("SETUP DESCRIPTION");
                break;
            case "GARAGE":
                add_product.setText("ADD SERVICE");
                break;
            case "CAR WASH":
                add_product.setText("ADD SERVICE");
                break;
            case "DRY CLEANER":
                add_product.setText("ADD SERVICE");
                break;
//            case "HAIRDRESSER/BARBER SHOP":
//                firstTextView.setText("ADD SERVICE");
//                break;
//            case "HAIRDRESSER/BARBER SHOP":
//                firstTextView.setText("ADD SERVICE");
//                break;
//            case "HAIRDRESSER/BARBER SHOP":
//                firstTextView.setText("ADD SERVICE");
//                break;
//            case "HAIRDRESSER/BARBER SHOP":
//                firstTextView.setText("ADD SERVICE");
//                break;
      }
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (ShopTypeName){
                    case "GAS STATION":
                    case "HAIRDRESSER/BARBER SHOP":
                    case "NEWSAGENT":
                    case "MOVERS AND PARKERS":
                    case "TRAVEL AGENT/TRAVEL AGENCY":
                    case "TILL/CASH REGISTER":
                    case "WYSP AGENT":
                    case "GARAGE":
                    case "CAR WASH":
                    case "DRY CLEANER":
                        openProductView();
                        break;
                }
            }
        });

    }

    private void openProductView() {
        Intent intent = new Intent(getContext(),ProductView.class);
        intent.putExtra("ShopTypeName",ShopTypeName);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ShopTypeName = getArguments().getString("ShopTypeName");
        return inflater.inflate(R.layout.basic_shop_fragment,container,false);
    }
}
