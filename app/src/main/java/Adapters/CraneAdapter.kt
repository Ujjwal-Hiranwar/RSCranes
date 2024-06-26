package Adapters

import Models.dataModel
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mypackage.rscranes.R
import com.mypackage.rscranes.SplashScreenActivity
import com.squareup.picasso.Picasso

class CraneAdapter(private val context: Context, private val craneList: ArrayList<dataModel>) :
    RecyclerView.Adapter<CraneAdapter.ViewHolder>() {

    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {

        fun onItemClick(position: Int)
        fun onItemClickDel(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val modelName: TextView = itemView.findViewById(R.id.mName)
        val img: ImageView = itemView.findViewById(R.id.crane_img)
        val checkBtn: Button = itemView.findViewById(R.id.checkNow)
        val description : TextView = itemView.findViewById(R.id.des)
     val  delBtn : ImageView = itemView.findViewById(R.id.delete)

        init {
           checkBtn.setOnClickListener(this)
            delBtn.setOnClickListener(this)
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
//        To make delete btn visible to only admin
        if (SplashScreenActivity.adminUser.isAdmin) {
            holder.delBtn.visibility = View.VISIBLE

        }else{
            holder.delBtn.visibility = View.INVISIBLE
            }
        holder.delBtn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)

            // set message of alert dialog
            dialogBuilder.setMessage("Do you want to Delete this Model ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Delete", DialogInterface.OnClickListener {
                        dialog, id ->  myListener.onItemClickDel(position)

                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Deleting model")
            // show alert dialog
            alert.show()
        }
        Picasso.get().load(craneList[position].image).into(holder.img)
        holder.description.text = currentItem.description
        holder.checkBtn.setOnClickListener {
            myListener.onItemClick(position)
        }

    }
}
