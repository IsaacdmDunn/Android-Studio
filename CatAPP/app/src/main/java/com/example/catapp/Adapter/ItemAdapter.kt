package com.example.catapp.Adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.catapp.R
import com.example.catapp.model.Fact

class ItemAdapter(private val context: Context,
                  private val dataset: List<Fact>
                  ): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFact: TextView = view.findViewById(R.id.txtFact)
        val imgFact: ImageView = view.findViewById(R.id.imgFact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_view_fact,
            parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.txtFact.text =
            context.resources.getString(item.stringResourceId)
        holder.imgFact.setImageResource(item.imageResourceId)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

}