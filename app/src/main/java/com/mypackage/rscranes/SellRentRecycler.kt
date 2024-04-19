package com.mypackage.rscranes

import Adapters.SellAdapter
import Adapters.SellRentAdapter
import Models.RentRequests
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SellRentRecycler : AppCompatActivity(), SellRentAdapter.OnItemClickListener,
    SellAdapter.OnItemClickListener {
    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val List = ArrayList<RentRequests>()
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: SellRentAdapter
    private lateinit var rv: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_rent_recycler)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Rent Request")
        rv = findViewById(R.id.recyclerViewRequest)

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    List.clear()
                    var i = 0;
                    for (snapshot in snapshot.children) {
                        val data = snapshot.getValue(RentRequests::class.java)
                        data?.let { List.add(it) }  // Add only non-null crane objects
                    }
                    rv.layoutManager = LinearLayoutManager(this@SellRentRecycler)

                    adapter = SellRentAdapter(this@SellRentRecycler, List)
                    rv.adapter = adapter
                    adapter.setOnItemClickListener(this@SellRentRecycler)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    override fun onItemClick(position: Int) {
        val clickedItem = List[position]
        val intent = Intent(this, RequestInfoview::class.java)
        intent.putExtra("key",clickedItem.name)
        startActivity(intent)
    }

}