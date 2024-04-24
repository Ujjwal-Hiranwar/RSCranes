package com.mypackage.rscranes

import Adapters.SellAdapter
import Adapters.SellRentAdapter
import Models.RentRequests
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mypackage.rscranes.databinding.ActivitySellRequestBinding

class SellRequest : AppCompatActivity(), SellRentAdapter.OnItemClickListener,
    SellAdapter.OnItemClickListener {
    private lateinit var binding: ActivitySellRequestBinding

    private lateinit var db: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val List = ArrayList<RentRequests>()
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: SellAdapter
    private lateinit var rv: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sell_request)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        databaseReference = db.reference.child("Sell Request")
        rv = findViewById(R.id.recyclerViewRequestSell)

        binding.back.setOnClickListener {
            finish()
        }
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    List.clear()
                    var i = 0;
                    for (snapshot in snapshot.children) {
                        val data = snapshot.getValue(RentRequests::class.java)
                        data?.let { List.add(it) }  // Add only non-null crane objects
                    }
                    rv.layoutManager = LinearLayoutManager(this@SellRequest)

                    adapter = SellAdapter(this@SellRequest, List)
                    rv.adapter = adapter
                    adapter.setOnItemClickListener(this@SellRequest)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    override fun onItemClick(position: Int) {
        val clickedItem = List[position]
        val intent = Intent(this, SellInfoView::class.java)
        intent.putExtra("key",clickedItem.name)
        startActivity(intent)
    }

}