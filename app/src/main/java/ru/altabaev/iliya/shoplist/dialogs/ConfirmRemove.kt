package ru.altabaev.iliya.shoplist.dialogs

import android.app.ActionBar
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.RecyclerView
import android.view.*
import ru.altabaev.iliya.shoplist.R
import ru.altabaev.iliya.shoplist.databinding.ConfirmRemoveBinding
import ru.altabaev.iliya.shoplist.interfaces.DialogInterface

class ConfirmRemove : DialogFragment() {

    private var binding: ConfirmRemoveBinding? = null
    var dialogInterface: DialogInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        dialog.setCanceledOnTouchOutside(false)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setGravity(Gravity.CENTER)

        binding = DataBindingUtil.inflate(inflater, R.layout.confirm_remove, container, false)

        binding?.ok?.setOnClickListener {
            dialogInterface?.onClickOk()
        }

        binding?.cancel?.setOnClickListener {
            dialogInterface?.onClickCancel()
        }

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
    }
}