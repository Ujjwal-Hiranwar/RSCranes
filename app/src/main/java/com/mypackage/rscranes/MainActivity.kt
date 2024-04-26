package com.mypackage.rscranes

import Adapters.CraneAdapter
import Adapters.RentAdapter
import Models.CraneDetails
import Models.dataModel
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mypackage.rscranes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CraneAdapter.OnItemClickListener , NavigationView.OnNavigationItemSelectedListener,
    RentAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val craneList = ArrayList<dataModel>() // Declare list at class level
    private val rentList = ArrayList<CraneDetails>() // Declare list at class level
    private val sellList = ArrayList<CraneDetails>() // Declare list at class level
    private var adapter1 : CraneAdapter? = null
    private var adapter2 : RentAdapter? = null
    private lateinit var drawerLayout : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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


        binding.craneRecycleView.layoutManager = LinearLayoutManager(this)

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    craneList.clear() // Clear the list before adding new data

                    for (snapshot in snapshot.children) {
                        val crane = snapshot.getValue(dataModel::class.java)
                        crane?.let { craneList.add(it) }  // Add only non-null crane objects
                    }

                 adapter1 = CraneAdapter(this@MainActivity, craneList)
                    binding.craneRecycleView.adapter = adapter1
                    adapter1!!.setOnItemClickListener(this@MainActivity)

                } else {
                    Toast.makeText(this@MainActivity, "Doesn't exist.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
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
            R.id.rent_request -> Toast.makeText(this, "Nav Request Clicked", Toast.LENGTH_SHORT).show()
            R.id.nav_hire -> {
                databaseReference = db.reference.child("Crane details")

                databaseReference.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                        rentList.clear() // Clear the list before adding new data

                        for (snapshot in snapshot.children) {
                            val crane = snapshot.getValue(CraneDetails::class.java)
                            crane?.let {
                                if (it.type.lowercase() == "rent"){
                                    rentList.add(it)
                                    Log.d("rentList",it.toString())

                                }
                            }
                        }

                        adapter2 = RentAdapter(this@MainActivity, rentList)
                        binding.craneRecycleView.adapter = adapter2
                        adapter2!!.setOnItemClickListener(this@MainActivity)

                    } else {
                        Toast.makeText(this@MainActivity, "Doesn't exist.", Toast.LENGTH_SHORT).show()
                    }
                }

                        override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }

                })
            }
            R.id.nav_purchase ->{}
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
