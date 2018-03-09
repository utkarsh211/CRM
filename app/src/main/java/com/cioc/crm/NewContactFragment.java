package com.cioc.crm;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewContactFragment extends Fragment {

    EditText newFullName, newEmail, newMobNo, newCompany, newEmailDuplicate, newMobNoDuplicate, newDesignation, newNotes, newLinkedin, newFb;
    TextView newDp, newDpAttach;
    Button saveNewContact;
    Switch genderSwitch;
    ImageView switchProfile;


    public NewContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_contact, container, false);

        newFullName = v.findViewById(R.id.contacts_full_name);
        newEmail = v.findViewById(R.id.contacts_email);
        newMobNo = v.findViewById(R.id.contacts_mobile);
        newCompany = v.findViewById(R.id.contacts_company);
        newEmailDuplicate = v.findViewById(R.id.contacts_email_secondary);
        newMobNoDuplicate = v.findViewById(R.id.contacts_mobile_secondary);
        newDesignation = v.findViewById(R.id.contacts_designation);
        newNotes = v.findViewById(R.id.contacts_notes);
        newLinkedin = v.findViewById(R.id.contacts_linkedin);
        newFb = v.findViewById(R.id.contacts_facebook);
        newDp = v.findViewById(R.id.contact_profile_photo);
        newDpAttach = v.findViewById(R.id.dp_attached);
        saveNewContact = v.findViewById(R.id.save_newContacts);
        genderSwitch = v.findViewById(R.id.gender_sw);
        switchProfile = v.findViewById(R.id.switch_profile);

        genderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    switchProfile.setImageResource(R.drawable.male);
                }
                else switchProfile.setImageResource(R.drawable.woman);
            }
        });


        newDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 111);
            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111){
            if (resultCode == getActivity().RESULT_OK){
                Uri uri = data.getData();
                try {
                    Bitmap bit = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                    newDpAttach.setVisibility(View.VISIBLE);
                    newDpAttach.setText("Attached");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
