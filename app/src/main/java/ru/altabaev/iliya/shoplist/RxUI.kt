package ru.altabaev.iliya.shoplist

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxUI {

    companion object {
        @JvmStatic
        fun getEditTextObservable(editText: EditText) =
            PublishSubject.create<String> {
                editText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        it.onNext(s.toString())
                    }
                })
            }
    }
}