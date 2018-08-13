package ru.altabaev.iliya.shoplist.adapters

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import io.realm.RealmResults
import ru.altabaev.iliya.shoplist.R
import ru.altabaev.iliya.shoplist.db.DBWorker
import ru.altabaev.iliya.shoplist.db.models.CList
import ru.altabaev.iliya.shoplist.dialogs.ConfirmRemove
import ru.altabaev.iliya.shoplist.interfaces.DialogInterface
import ru.altabaev.iliya.shoplist.interfaces.OnRecyclerViewItemClickListener

class ListsRecyclerAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val NORMAL = 0
    private val CREATE_NEW = 1

    private val confirm = ConfirmRemove()

    var onItemClickListener: OnRecyclerViewItemClickListener? = null

    private var results: RealmResults<CList>? = null

    fun setData(results: RealmResults<CList>?) {
        this.results = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            NORMAL -> {
                val view = LayoutInflater.from(context).inflate(R.layout.list, parent, false)
                return ListViewHolder.newInstance(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_create_list, parent, false)
                return CreateListViewHolder.newInstance(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return results?.size?.plus(1) ?: 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == results?.size) CREATE_NEW else NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListViewHolder) {
            val list = results!![position]
            holder.setData(
                    list?.title,
                    list?.items?.size
            )
            holder.itemView?.setOnClickListener {
                onItemClickListener?.onClick(list)
            }
            holder.itemView?.findViewById<ImageButton>(R.id.delete)
                    ?.setOnClickListener {
                        if (!confirm.isResumed) {
                            confirm.show((context as AppCompatActivity).supportFragmentManager, "confirm remove")
                            confirm.dialogInterface = object : DialogInterface {

                                override fun onClickOk() {
                                    DBWorker.getInstance().deleteElementById(CList::class.java, results!![position]?.id!!)
                                    confirm.dismiss()
                                }

                                override fun onClickCancel() {
                                    confirm.dismiss()
                                }

                            }
                        }
                    }
            holder.itemView?.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.setBackgroundResource(R.color.colorAccent)
                    }
                    MotionEvent.ACTION_UP -> {
                        v.setBackgroundResource(R.color.colorTextWhite)
                    }
                }
                false
            }
        } else {
            holder.itemView?.setOnClickListener {
                onItemClickListener?.onClick()
            }
        }
    }

    class ListViewHolder(view: View, val title: TextView, val size: TextView) : RecyclerView.ViewHolder(view) {

        companion object {
            @JvmStatic
            fun newInstance(view: View) = ListViewHolder(view,
                    view.findViewById(R.id.title),
                    view.findViewById(R.id.size))
        }

        fun setData(title: String?, size: Int?) {
            this.title.text = title
            this.size.text = size.toString()
        }
    }

    class CreateListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            @JvmStatic
            fun newInstance(view: View) = CreateListViewHolder(view)
        }
    }
}