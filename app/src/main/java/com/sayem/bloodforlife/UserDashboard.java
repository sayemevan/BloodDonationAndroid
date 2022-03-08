package com.sayem.bloodforlife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sayem.bloodforlife.fragments.AchievementFragment;
import com.sayem.bloodforlife.fragments.AdminPanelFragment;
import com.sayem.bloodforlife.fragments.AmbulanceFragment;
import com.sayem.bloodforlife.fragments.EditProfileFragment;
import com.sayem.bloodforlife.fragments.FindDonorsFragment;
import com.sayem.bloodforlife.fragments.HomeFragment;
import com.sayem.bloodforlife.fragments.HospitalFragment;
import com.sayem.bloodforlife.fragments.PostRequestFragment;
import com.sayem.bloodforlife.fragments.ProfileFragment;
import com.sayem.bloodforlife.fragments.StatisticsFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.onProfileFragmentBtnSelected{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;

    private Toolbar toolbar;

    private View navHeaderView;
    private TextView fullname, ubloodgroup;
    private CircleImageView drawerImage;
    private String userId, userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        drawerLayout = findViewById(R.id.dashboard_drawer);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeaderView = navigationView.getHeaderView(0);

        toolbar = findViewById(R.id.myDashboardToolBar);
        setSupportActionBar(toolbar);

        fullname = navHeaderView.findViewById(R.id.showName);
        ubloodgroup = navHeaderView.findViewById(R.id.showBloodGroup);
        drawerImage = navHeaderView.findViewById(R.id.drawableImg);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = mAuth.getCurrentUser().getUid();

        //Drawer Header View
        DocumentReference documentReference2 = fStore.collection("userrole").document(userId);
        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fullname.setText(value.getString("userName"));
                ubloodgroup.setText("Blood Group "+value.getString("bloodGroup"));
            }
        });

        //show Drawer Image
        StorageReference showDrawableImageRef = storageReference.child(userId+".jpg");
        if(showDrawableImageRef != null) {
            showDrawableImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(drawerImage);
                }
            });
        }


        documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userRole = value.getString("rolename");

                Menu navMenu = navigationView.getMenu();
                if(userRole.equalsIgnoreCase("admin")){
                    navMenu.findItem(R.id.drawerAdmin).setVisible(true);
                } else {
                    navMenu.findItem(R.id.drawerAdmin).setVisible(false);
                }
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment, new HomeFragment());
        fragmentTransaction.commit();



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater minflater = getMenuInflater();
        minflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            case R.id.searchMenu:
//                Toast.makeText(this, "Search menu clicked", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.menuAdminInfo:
                Toast.makeText(this, "Admin menu clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuAppInfo:
                Toast.makeText(this, "App Info menu clicked", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.drawerHome){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new HomeFragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(item.getItemId() == R.id.drawerProfile){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new ProfileFragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(item.getItemId() == R.id.drawerMyRequest){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new PostRequestFragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(item.getItemId() == R.id.drawerMyAchievements){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new AchievementFragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(item.getItemId() == R.id.drawerSearchDonor){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new FindDonorsFragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(item.getItemId() == R.id.drawerHospital){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new HospitalFragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(item.getItemId() == R.id.drawerAmbulance){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new AmbulanceFragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(item.getItemId() == R.id.drawerAdmin){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new AdminPanelFragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(item.getItemId() == R.id.drawerStatistic){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new StatisticsFragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(item.getItemId() == R.id.drawerLogout){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Are you sure want to logout?")
                    .setCancelable(false)
                    .setTitle("Logout")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();
                            finish();
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

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onProfileFragmentEditProfileButtonSelected() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new EditProfileFragment()).addToBackStack(null);
        fragmentTransaction.commit();
    }
}