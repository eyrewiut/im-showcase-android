package uk.co.imshowcase.imshowcase

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uk.co.eyrewiut.imshowcase.databinding.ProjectListCardBinding
import uk.co.imshowcase.imshowcase.models.ProjectContentType
import uk.co.imshowcase.imshowcase.models.Story

/*
* Copied with heavy modifications from a guide on using recyclerViews.
* Accessed: Thursday 3 Nov 2022
* Available at: https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin
* Author: Google Developers
*/
class ProjectListAdapter(private val onClick: (Story<ProjectContentType>) -> Unit) : ListAdapter<Story<ProjectContentType>, ProjectListAdapter.ViewHolder>(
    ProjectDiffCallback
) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(binding: ProjectListCardBinding, val onClick: (Story<ProjectContentType>) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        private val cardView: CardView
        private val titleView: TextView
        private val authorView: TextView
        private val thumbnailView: ImageView

        private var currentProject: Story<ProjectContentType>? = null

        init {
            // Define click listener for the ViewHolder's View.
            cardView = binding.projectListCard
            titleView = binding.projectTitle
            authorView = binding.projectAuthor
            thumbnailView = binding.projectThumb

            /*
            * The following block was copied with modifications from a code example on using recyclerViews
            * Available at: https://github.com/android/views-widgets-samples/blob/main/RecyclerViewKotlin/app/src/main/java/com/example/recyclersample/flowerList/FlowersAdapter.kt
            * Author: MagicalMeghan
            * Accessed: Thursday 3rd Nov 2022
            */
            binding.projectListCard.setOnClickListener {
                currentProject?.let {
                    onClick(it)
                }
            }
        }

        /*
        * The following function and use of it was inspired by a code example on using recyclerViews. The code inside is my own.
        * Available at: https://github.com/android/views-widgets-samples/blob/main/RecyclerViewKotlin/app/src/main/java/com/example/recyclersample/flowerList/FlowersAdapter.kt
        * Author: MagicalMeghan
        * Accessed: Thursday 3rd Nov 2022
        */
        fun bind(project: Story<ProjectContentType>) {
            currentProject = project

            titleView.text = project.content.title
            authorView.text = project.content.author

            if (project.content.thumbnail != null && !project.content.thumbnail!!.filename.isNullOrBlank()) {
                try {
                    Picasso.get()
                        .load(project.content.thumbnail!!.filename)
                        .resize(200, 200)
                        .centerCrop()
                        .into(thumbnailView)
                } catch (err: Exception) {
                    // TODO: display placeholder image?
                    Log.e("ProjectListAdapter", "Error loading project thumbnail", err)
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val binding = ProjectListCardBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding, onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }
}

/*
* The following block was copied with modifications from a code example on using recyclerViews
* Available at: https://github.com/android/views-widgets-samples/blob/main/RecyclerViewKotlin/app/src/main/java/com/example/recyclersample/flowerList/FlowersAdapter.kt
* Author: MagicalMeghan
* Accessed: Thursday 3rd Nov 2022
*/
object ProjectDiffCallback : DiffUtil.ItemCallback<Story<ProjectContentType>>() {
    override fun areItemsTheSame(
        oldItem: Story<ProjectContentType>,
        newItem: Story<ProjectContentType>
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Story<ProjectContentType>,
        newItem: Story<ProjectContentType>
    ): Boolean {
        return oldItem.id == newItem.id
    }
}

