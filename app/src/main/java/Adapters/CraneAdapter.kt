package Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mypackage.rscranes.R
import Models.dataModel
import com.squareup.picasso.Picasso

class CraneAdapter(private val context: Context, private val craneList: ArrayList<dataModel>) :
    RecyclerView.Adapter<CraneAdapter.ViewHolder>() {

    private lateinit var myListener: OnItemClickListener

    interface OnItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val modelName: TextView = itemView.findViewById(R.id.mName)
        val img: ImageView = itemView.findViewById(R.id.crane_img)
        val checkBtn: Button = itemView.findViewById(R.id.checkNow)

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

    override fun getItemCount(): Int {
        return craneList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = craneList[position]
        holder.modelName.text = currentItem.modelName
        Picasso.get().load(craneList[position].image).into(holder.img)
        holder.checkBtn.setOnClickListener {
            myListener.onItemClick(position)
        }
    }
}
