package Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mypackage.rscranes.R
import Models.dataModel
import com.squareup.picasso.Picasso

class CraneAdapter(private val context: Context, private val craneList: ArrayList<dataModel>) :
    RecyclerView.Adapter<CraneAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modelName: TextView = itemView.findViewById(R.id.mName)
        val img: ImageView = itemView.findViewById(R.id.crane_img)
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
        Picasso.get().load(craneList.get(position).image).into(holder.img)

    }
}