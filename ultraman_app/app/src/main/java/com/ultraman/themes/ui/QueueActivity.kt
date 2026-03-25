package com.ultraman.themes.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ultraman.themes.R
import com.ultraman.themes.databinding.ActivityQueueBinding
import com.ultraman.themes.model.UltramanSong
import com.ultraman.themes.service.MusicService

class QueueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQueueBinding
    private lateinit var adapter: QueueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Up Next"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapter = QueueAdapter(MusicService.queue.toMutableList()) { index ->
            MusicService.removeFromQueue(index)
            adapter.removeItem(index)
        }
        binding.queueRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.queueRecyclerView.adapter = adapter

        MusicService.onQueueChanged = {
            runOnUiThread {
                adapter.updateList(MusicService.queue.toMutableList())
            }
        }

        updateEmptyState()
    }

    private fun updateEmptyState() {
        binding.emptyQueueText.visibility =
            if (MusicService.queue.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicService.onQueueChanged = null
    }
}

class QueueAdapter(
    private val items: MutableList<UltramanSong>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<QueueAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView     = view.findViewById(R.id.queueSongTitle)
        val series: TextView    = view.findViewById(R.id.queueSongSeries)
        val strip: View         = view.findViewById(R.id.queueColorStrip)
        val remove: ImageButton = view.findViewById(R.id.removeFromQueueButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_queue, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val song = items[pos]
        h.title.text  = song.title
        h.series.text = "${song.series} • ${song.songType}"
        try { h.strip.setBackgroundColor(Color.parseColor(song.color)) } catch (_: Exception) {}
        h.remove.setOnClickListener { onRemove(pos) }
    }

    fun removeItem(index: Int) {
        if (index in items.indices) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateList(newList: MutableList<UltramanSong>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}