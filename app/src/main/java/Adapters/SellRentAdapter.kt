package Adapters

import Models.RentRequests
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mypackage.rscranes.R
import com.mypackage.rscranes.RentRequest

class SellRentAdapter(private val context: Context, private val  rentList: ArrayList<RentRequests>):
    RecyclerView.Adapter<SellRentAdapter.ViewHolder>() {

    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: RentRequest) {
        myListener = listener
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val checkBtn: TextView = itemView.findViewById(R.id.checkBtnRequest)
        val status: TextView = itemView.findViewById(R.id.model_status)
        val rentalName: TextView = itemView.findViewById(R.id.rental_name)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                myListener.onItemClick(adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellRentAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SellRentAdapter.ViewHolder, position: Int) {
        val currentItem = rentList[position]
        holder.rentalName.text = currentItem.name
        holder.status.text = "RENT"
        holder.checkBtn.setOnClickListener {
            myListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return rentList.size
    }
}