package com.mypackage.rscranes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import Adapters.CraneAdapter
import Models.dataModel
import android.content.ContentValues
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.mypackage.rscranes.databinding.ActivityAdminHomeBinding

class AdminHomeActivity : AppCompatActivity(), CraneAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val craneList = ArrayList<dataModel>()
    private var adapter : CraneAdapter? = null
    private lateinit var drawerLayout : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_home)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Model and Image")
        drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.cloase_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.addCranes.setOnClickListener {
            startActivity(Intent(this@AdminHomeActivity, AdminAddCrane::class.java))
        }
        binding.craneRecycleView.layoutManager = LinearLayoutManager(this)

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    craneList.clear() // Clear the list before adding new data

                    for (snapshot in snapshot.children) {
                        val crane = snapshot.getValue(dataModel::class.java)
                        crane?.let { craneList.add(it) }  // Add only non-null crane objects
                    }

                    adapter = CraneAdapter(this@AdminHomeActivity, craneList)
                    binding.craneRecycleView.adapter = adapter
                    adapter!!.setOnItemClickListener(this@AdminHomeActivity)

                } else {
                    Toast.makeText(this@AdminHomeActivity, "Doesn't exist.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    override fun onItemClick(position: Int) {
        val clickedItem = craneList[position]
        val intent = Intent(this,CraneInfoView::class.java)
        intent.putExtra("key",clickedItem.modelName)
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_logout -> {
                auth.signOut()
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
//            R.id.nav_status -> Toast.makeText(this, "Nav Status Clicked", Toast.LENGTH_SHORT).show()
//            R.id.nav_account -> Toast.makeText(this, "Nav Account Clicked", Toast.LENGTH_SHORT).show()
            R.id.nav_request -> Toast.makeText(this, "Nav Request Clicked", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


}

