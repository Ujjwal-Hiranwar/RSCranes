package Adapters

import Models.CraneDetails
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mypackage.rscranes.MainActivity
import com.mypackage.rscranes.R
import com.squareup.picasso.Picasso

class RentAdapter(private val context: Context, private val craneList: ArrayList<CraneDetails>) :
    RecyclerView.Adapter<RentAdapter.ViewHolder>() {

    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: MainActivity) {
        myListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val modelName: TextView = itemView.findViewById(R.id.mName)
        val img: ImageView = itemView.findViewById(R.id.crane_img)
        val checkBtn: Button = itemView.findViewById(R.id.checkNow)
        val description : TextView = itemView.findViewById(R.id.des)


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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = craneList[position]
        holder.modelName.text = currentItem.model
        Picasso.get().load(craneList[position].image).into(holder.img)
        holder.description.text = currentItem.description
        holder.checkBtn.setOnClickListener {
            myListener.onItemClick(position)
        }
    }

        override fun getItemCount(): Int {
            return craneList.size
        }
}
