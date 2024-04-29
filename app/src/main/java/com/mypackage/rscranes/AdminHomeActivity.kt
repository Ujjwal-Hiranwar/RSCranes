package com.mypackage.rscranes

import Adapters.CraneAdapter
import Adapters.PurchaseAdapter
import Adapters.RentAdapter
import Models.CraneDetails
import Models.dataModel
import android.content.ContentValues
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
import com.google.firebase.storage.FirebaseStorage
import com.mypackage.rscranes.databinding.ActivityAdminHomeBinding

class AdminHomeActivity : AppCompatActivity(), CraneAdapter.OnItemClickListener,
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val craneList = ArrayList<dataModel>()
    private val rentList = ArrayList<CraneDetails>()
    private val sellList = ArrayList<CraneDetails>()
    private var adapter1: CraneAdapter? = null
    private var adapter2: RentAdapter? = null
    private var adapter3: PurchaseAdapter? = null
    private lateinit var drawerLayout: DrawerLayout

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

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.cloase_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.addCranes.setOnClickListener {
            startActivity(Intent(this@AdminHomeActivity, AdminAddCrane::class.java))
        }
        binding.craneRecycleView.layoutManager = LinearLayoutManager(this)
        generalView()


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                auth.signOut()
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.rent_request -> {
                val intent = Intent(this, RentRequest::class.java)
                startActivity(intent)
            }

            R.id.sell_request -> {
                val intent = Intent(this, SellRequest::class.java)
                startActivity(intent)
            }
            R.id.nav_home ->generalView()
            R.id.nav_hire ->rentView()
            R.id.nav_purchase ->{purchaseView()}
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun generalView() {
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    craneList.clear() // Clear the list before adding new data

                    for (snapshot in snapshot.children) {
                        val crane = snapshot.getValue(dataModel::class.java)
                        crane?.let { craneList.add(it) }  // Add only non-null crane objects
                    }

                    adapter1 = CraneAdapter(this@AdminHomeActivity, craneList)
                    binding.craneRecycleView.adapter = adapter1
                    adapter1!!.setOnItemClickListener(this@AdminHomeActivity)

                } else {
                    Toast.makeText(this@AdminHomeActivity, "Doesn't exist.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    override fun onItemClick(position: Int) {
        val clickedItem = craneList[position]
        val intent = Intent(this, CraneInfoView::class.java)
        intent.putExtra("key", clickedItem.modelName)
        startActivity(intent)
    }

    override fun onItemClickDel(position: Int) {
        val clickedItem = craneList[position]
     val   storageRef = db.reference.child("Model and Image").child(clickedItem.modelName)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Crane details").child(clickedItem.modelName)
        databaseReference.removeValue()
        storageRef.removeValue()
        val intent = Intent(this, AdminHomeActivity::class.java)
        startActivity(intent)
    }
    fun purchaseView(){
        databaseReference = db.reference.child("Crane details")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    sellList.clear() // Clear the list before adding new data

                    for (snapshot in snapshot.children) {
                        val crane = snapshot.getValue(CraneDetails::class.java)
                        crane?.let {
                            if (it.type.lowercase() == "sell") {
                                sellList.add(it)
                            }
                        }
                    }

                    adapter3 = PurchaseAdapter(this@AdminHomeActivity, sellList)
                    binding.craneRecycleView.adapter = adapter3
                    adapter3!!.setOnItemClickListener(object :
                        PurchaseAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            val clickedItem = sellList[position]
                            val intent = Intent(this@AdminHomeActivity, CraneInfoView::class.java)
                            intent.putExtra("key", clickedItem.model)
                            startActivity(intent)
                        }
                    })

                } else {
                    Toast.makeText(this@AdminHomeActivity, "Doesn't exist.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })
    }
    fun rentView(){
        databaseReference = db.reference.child("Crane details")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    rentList.clear() // Clear the list before adding new data

                    for (snapshot in snapshot.children) {
                        val crane = snapshot.getValue(CraneDetails::class.java)
                        crane?.let {
                            if (it.type.lowercase() == "rent") {
                                rentList.add(it)
                            }
                        }
                    }

                    adapter2 = RentAdapter(this@AdminHomeActivity, rentList)
                    binding.craneRecycleView.adapter = adapter2
                    adapter2!!.setOnItemClickListener(object :
                        RentAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            val clickedItem = rentList[position]
                            val intent = Intent(this@AdminHomeActivity, CraneInfoView::class.java)
                            intent.putExtra("key", clickedItem.model)
                            startActivity(intent)
                        }
                    })

                } else {
                    Toast.makeText(this@AdminHomeActivity, "Doesn't exist.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })
    }
    
}
