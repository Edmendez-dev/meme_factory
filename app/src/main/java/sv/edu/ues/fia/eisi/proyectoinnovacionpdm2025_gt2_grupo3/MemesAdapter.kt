package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MemesAdapter(
    private val memes: List<MainActivity.MemeItem>,
    private val activity: MainActivity
) : RecyclerView.Adapter<MemesAdapter.MemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meme, parent, false)
        return MemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val memeItem = memes[position]
        Glide.with(holder.itemView.context)
            .load(memeItem.uri)
            .into(holder.memeImage)

        holder.btnSave.setOnClickListener {
            activity.saveMemeToGallery(position)
        }

        holder.btnShare.setOnClickListener {
            activity.shareMeme(position)
        }

        holder.btnDelete.setOnClickListener {
            activity.deleteMeme(position)
        }
    }

    override fun getItemCount() = memes.size

    class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memeImage: ImageView = itemView.findViewById(R.id.meme_image)
        val btnSave: Button = itemView.findViewById(R.id.btn_save)
        val btnShare: Button = itemView.findViewById(R.id.btn_share)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete)
    }
}