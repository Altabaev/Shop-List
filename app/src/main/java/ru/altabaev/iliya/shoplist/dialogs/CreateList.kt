package ru.altabaev.iliya.shoplist.dialogs

import android.app.ActionBar
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.DialogFragment
import android.view.*
import ru.altabaev.iliya.shoplist.R
import ru.altabaev.iliya.shoplist.databinding.CreateListBinding
import ru.altabaev.iliya.shoplist.db.DBWorker
import ru.altabaev.iliya.shoplist.db.models.CList

class CreateList : DialogFragment() {

    private var binding: CreateListBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.window.attributes.windowAnimations = R.style.CreateListDialog

        binding = DataBindingUtil.inflate(inflater, R.layout.create_list, container, false)

        if (arguments?.getInt(LIST_ID) != null) {
            val list = getDbWorker().getElementById(CList::class.java, arguments?.getInt(LIST_ID)!!)
            binding?.title?.setText(list?.title)
        }

        binding?.ok?.setOnClickListener {
            val text = binding?.title?.text.toString()
            if (text.isEmpty()) {
                binding?.inputLayout?.error = "Заполните это поле"
                return@setOnClickListener
            }
            if (arguments?.getInt(LIST_ID) == null) {
                val realm = getDbWorker().getRealm()
                realm.executeTransaction {
                    val list = getDbWorker().getRealm().createObject(CList::class.java, getDbWorker().getNextId(CList::class.java))
                    list.title = text
                }
                binding?.title?.text?.clear()
            } else {
                getDbWorker().getRealm().executeTransaction {
                    val list = it.where(CList::class.java).equalTo("id", arguments?.getInt(LIST_ID)).findFirst()
                    list?.title = binding?.title?.text?.toString()
                }

            }

            dismiss()
        }

        binding?.cancel?.setOnClickListener {
            dismiss()
        }

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setLayout(AppBarLayout.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
    }

    fun getDbWorker() = DBWorker.getInstance()

    companion object {
        @JvmStatic
        private val LIST_ID = "list_id"

        @JvmStatic
        fun newInstance(id: Int): CreateList {
            val createList = CreateList()
            val bundle = Bundle()
            bundle.putInt(LIST_ID, id)
            createList.arguments = bundle
            return createList
        }
    }
}