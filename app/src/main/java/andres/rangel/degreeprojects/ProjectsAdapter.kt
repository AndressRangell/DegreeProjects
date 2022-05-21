package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.ItemProjectBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ProjectsAdapter : RecyclerView.Adapter<ProjectsAdapter.ProjectsViewHolder>() {

    inner class ProjectsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Project, newItem: Project) =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProjectsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_project,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ProjectsViewHolder, position: Int) {
        val binding = ItemProjectBinding.bind(holder.itemView)

        val project = differ.currentList[position]
        holder.itemView.apply {
            binding.apply {
                tvName.text = project.name
                tvDescription.text = project.description
                tvStatus.text = when (project.status) {
                    StatusProject.UNAPPROVED -> "Estado: sin aprobar"
                    StatusProject.FREE -> "Estado: libre"
                    StatusProject.ASSIGNED -> "Estado: asignado"
                    StatusProject.DEVELOPING -> "Estado: en desarrollo"
                    StatusProject.FINISHED -> "Estado: Finalizado"
                }
                if (position % 2 == 0)
                    linearItem.setBackgroundResource(R.drawable.background_card_one)
                else
                    linearItem.setBackgroundResource(R.drawable.background_card_two)
            }

            setOnClickListener {
                onItemClickListener?.let { it(project) }
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

    private var onItemClickListener: ((Project) -> Unit)? = null

    fun setOnItemClickListener(listener: (Project) -> Unit) {
        onItemClickListener = listener
    }

}