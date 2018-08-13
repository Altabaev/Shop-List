package ru.altabaev.iliya.shoplist.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import io.realm.RealmList
import ru.altabaev.iliya.shoplist.R
import ru.altabaev.iliya.shoplist.db.DBWorker
import ru.altabaev.iliya.shoplist.db.models.Item
import ru.altabaev.iliya.shoplist.dialogs.ConfirmRemove
import ru.altabaev.iliya.shoplist.dialogs.ItemEditor
import ru.altabaev.iliya.shoplist.interfaces.DialogInterface
import ru.altabaev.iliya.shoplist.interfaces.ItemTouchHelperAdapter
import java.util.*


class ItemsRecyclerAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        ItemTouchHelperAdapter {

    private var isItemMoved = false

    private val confirm = ConfirmRemove()

    private var results: RealmList<Item>? = null

    fun setData(results: RealmList<Item>?) {
        if (isItemMoved) return
        this.results = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return ItemViewHolder.newInstance(view)
    }

    override fun getItemCount(): Int {
        return results?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = results!![position]
            holder.setData(
                    item?.complited,
                    item?.title,
                    item?.price,
                    item?.count
            )

            val checkbox = holder.itemView?.findViewById<AppCompatCheckBox>(R.id.complited)
            holder.itemView?.findViewById<LinearLayout>(R.id.check)?.setOnClickListener {
                checkbox?.isChecked = checkbox?.isChecked?.not() ?: false
                holder.itemView?.findViewById<LinearLayout>(R.id.ll)
                        ?.setBackgroundResource((if (checkbox?.isChecked!!) R.color.colorSalad else R.color.colorTextWhite))
            }

            checkbox?.setOnCheckedChangeListener { buttonView, isChecked ->
                DBWorker.getInstance().getRealm().executeTransaction {
                    item?.complited = isChecked
                }
            }

            holder.itemView?.findViewById<ImageButton>(R.id.option_menu)
                    ?.setOnClickListener {
                        val menu = PopupMenu(context, it)
                        menu.inflate(R.menu.item_option_menu)
                        menu.setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.action_edit -> {
                                    val editor = ItemEditor.newInstance(item?.id ?: 0)
                                    editor.show((context as AppCompatActivity).supportFragmentManager, "item editor")
                                }
                            }
                            true
                        }
                        menu.show()
                    }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        isItemMoved = true
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                DBWorker.getInstance().getRealm().executeTransaction {
                    Collections.swap(results, i, i + 1)
                }
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                DBWorker.getInstance().getRealm().executeTransaction {
                    Collections.swap(results, i, i - 1)
                }
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        isItemMoved = false
        true
    }

    override fun onItemDismiss(position: Int) {
        confirm.show((context as AppCompatActivity).supportFragmentManager, "confirm remove")
        confirm.dialogInterface = object : DialogInterface {
            override fun onClickOk() {
                DBWorker.getInstance().getRealm().executeTransaction {
                    results?.removeAt(position)
                }
                notifyItemRemoved(position)
                confirm.dismiss()
            }

            override fun onClickCancel() {
                notifyDataSetChanged()
                confirm.dismiss()
            }
        }
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    class ItemViewHolder(view: View, val ll: LinearLayout, val complited: AppCompatCheckBox, val title: TextView,
                         val count: TextView, val price: TextView) : RecyclerView.ViewHolder(view) {

        companion object {
            @JvmStatic
            fun newInstance(view: View) = ItemViewHolder(view,
                    view.findViewById(R.id.ll),
                    view.findViewById(R.id.complited),
                    view.findViewById(R.id.title),
                    view.findViewById(R.id.count),
                    view.findViewById(R.id.price))
        }

        fun setData(complited: Boolean?, title: String?, price: Float?, count: Int?) {
            this.ll.setBackgroundResource((if (complited == true) R.color.colorSalad else R.color.colorTextWhite))
            this.complited.isChecked = complited ?: false
            this.title.text = title
            this.price.text = price?.toString() ?: "0"
            this.count.text = count?.toString() ?: "1"
        }
    }
}