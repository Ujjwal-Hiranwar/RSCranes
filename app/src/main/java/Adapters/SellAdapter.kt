package Adapters

import Models.RentRequests
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mypackage.rscranes.R
import com.mypackage.rscranes.SellRequest

class SellAdapter(private val context: Context, private val  rentList: ArrayList<RentRequests>):
    RecyclerView.Adapter<SellAdapter.ViewHolder>() {

    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: SellRequest) {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sell_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = rentList[position]
        holder.rentalName.text = currentItem.name
        holder.status.text = "SELL"
        holder.checkBtn.setOnClickListener {
            myListener.onItemClick(position)
    }
    }

    override fun getItemCount(): Int {
        return rentList.size
    }


}