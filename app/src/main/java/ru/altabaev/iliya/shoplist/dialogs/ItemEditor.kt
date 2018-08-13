package ru.altabaev.iliya.shoplist.dialogs

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import ru.altabaev.iliya.shoplist.R
import ru.altabaev.iliya.shoplist.databinding.ItemEditorBinding
import ru.altabaev.iliya.shoplist.db.DBWorker
import ru.altabaev.iliya.shoplist.db.models.Item

class ItemEditor : DialogFragment() {

    private var binding: ItemEditorBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.attributes.windowAnimations = R.style.CreateListDialog

        binding = DataBindingUtil.inflate(inflater, R.layout.item_editor, container, false)

        getDbWorker().getAll(Item::class.java, arguments?.getInt(ITEM_ID)!!)
                .filter {
                    it.size > 0
                }
                .subscribe({
                    binding?.name?.setText(it[0]?.title)
                    binding?.price?.setText(it[0]?.price?.toString() ?: "0.0")
                    binding?.count?.setText(it[0]?.count?.toString() ?: "0")
                })


        binding?.ok?.setOnClickListener {
            getRealm().executeTransaction {
                val item = it.where(Item::class.java).equalTo("id", arguments?.getInt(ITEM_ID)).findFirst()
                item?.title = binding?.name?.text.toString()
                item?.price = binding?.price?.text.toString().toFloat()
                item?.count = binding?.count?.text.toString().toInt()
            }
            dismiss()
        }

        binding?.cancel?.setOnClickListener {
            dismiss()
        }

        return binding?.root
    }

    fun getRealm() = DBWorker.getInstance().getRealm()

    fun getDbWorker() = DBWorker.getInstance()

    companion object {
        @JvmStatic
        private val ITEM_ID = "item_id"

        @JvmStatic
        fun newInstance(id: Int): ItemEditor {
            val editor = ItemEditor()
            val bundle = Bundle()
            bundle.putInt(ITEM_ID, id)
            editor.arguments = bundle
            return editor
        }
    }
}