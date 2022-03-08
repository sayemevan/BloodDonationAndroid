package com.sayem.bloodforlife.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sayem.bloodforlife.adapter.AllBloodPostAdapter;
import com.sayem.bloodforlife.R;
import com.sayem.bloodforlife.model.requestposts;

public class HomeFragment extends Fragment {

    private RecyclerView allBloodRequestPost;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private CollectionReference bloodPostCollectionReference;
    private String userId;
    private AllBloodPostAdapter allBloodPostAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

//        MenuItem searchBar = view.findViewById(R.id.searchMenu);
//        searchBar.setVisible(false);

        getActivity().setTitle("Blood for Life");

        allBloodRequestPost = view.findViewById(R.id.homeRecycleForViewPost);
        allBloodRequestPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        allBloodRequestPost.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = mAuth.getCurrentUser().getUid();

        bloodPostCollectionReference = fStore.collection("requestposts");

        DisplayAllBloodRequestPost();

        allBloodPostAdapter.setOnBloodPostClickListener(new AllBloodPostAdapter.OnBloodPostClickListener() {
            @Override
            public void OnBloodPostClick(DocumentSnapshot documentSnapshot, int position) {
                requestposts bloodRequestPost = documentSnapshot.toObject(requestposts.class);
                String uniquePostId = documentSnapshot.getId();

                Bundle bundle = new Bundle();
                bundle.putString("pdetailUserNames", bloodRequestPost.getPostUserName());
                bundle.putString("pdetailUserIDs", bloodRequestPost.getPostUserID());

                bundle.putString("pdetailRequestFors", bloodRequestPost.getPostPatientBloodGroup());
                bundle.putString("pdetailPostTimes", bloodRequestPost.getPostTime());
                bundle.putString("pdetailPostDates", bloodRequestPost.getPostDate());
                bundle.putString("pdetailPatientNames", bloodRequestPost.getPostPatientName());
                bundle.putString("getPdetailPatientAges", bloodRequestPost.getPostPatientAge());
                bundle.putString("pdetailPatientBloodGroups", bloodRequestPost.getPostPatientBloodGroup());
                bundle.putString("pdetailPatientProblems", bloodRequestPost.getPostPatientProblem());
                bundle.putString("pdetailPatientBloodNeedBagss", bloodRequestPost.getPostBloodBagsNeeded());
                bundle.putString("pdetailPatientBloodNeedDates", bloodRequestPost.getPostPatientBloodNeedDate());
                bundle.putString("pdetailPatientHospitalNames", bloodRequestPost.getPostHospitalName());
                bundle.putString("pdetailPatientLocations", bloodRequestPost.getPostPatientLocation());
                bundle.putString("pdetailContactPersons", bloodRequestPost.getPostContactPersonName());
                bundle.putString("pdetailContactPersonPhones", bloodRequestPost.getPostContactPersonPhone());
                bundle.putString("pdetailStatuss", bloodRequestPost.getStatus());

                bundle.putString("uniqueBloodPostID", uniquePostId);


                EditDeleteBloodRequestPostFragment editDeleteBloodRequestPostFragment = new EditDeleteBloodRequestPostFragment();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                editDeleteBloodRequestPostFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container_fragment, editDeleteBloodRequestPostFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }

            @Override
            public void onCallButtonClick(DocumentSnapshot documentSnapshot) {
                requestposts bloodRequestPost = documentSnapshot.toObject(requestposts.class);
                final String ContactPersonPhone = bloodRequestPost.getPostContactPersonPhone();
                final String status = bloodRequestPost.getStatus();

                if(status.equalsIgnoreCase("Managed!")){
                    Toast.makeText(getActivity(), "This request is managed already! Thanks", Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage("Are you sure want to call?")
                            .setCancelable(false)
                            .setTitle("Call to the contact person")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String phoneNo = "tel:" + ContactPersonPhone;
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse(phoneNo));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });


        return view;
    }

    private void DisplayAllBloodRequestPost() {
        Query query = bloodPostCollectionReference.whereEqualTo("status", "Not Manage Yet!!");

        FirestoreRecyclerOptions<requestposts> options = new FirestoreRecyclerOptions.Builder<requestposts>()
                .setQuery(query, requestposts.class)
                .build();
        allBloodPostAdapter = new AllBloodPostAdapter(options);
        allBloodRequestPost.setAdapter(allBloodPostAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        allBloodPostAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        allBloodPostAdapter.stopListening();
    }
}
