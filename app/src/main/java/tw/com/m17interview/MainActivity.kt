package tw.com.m17interview

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import tw.com.m17interview.databinding.ActivityMainBinding
import tw.com.m17interview.model.network.NetworkState
import tw.com.m17interview.model.network.Status
import tw.com.m17interview.ui.UserListAdapter
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        val adapter = UserListAdapter()
        recyclerView.adapter = adapter
        viewModel.pagedList.observe(this, Observer {
            if (it.size == 0) showToast(getString(R.string.empty_result))
            adapter.submitList(it)
        })

        button.setOnClickListener {

            closeSoftKeyboard()
            input.clearFocus()

            val userInput: String = input.text.toString().trim()

            if (userInput.isNotEmpty()) {

                viewModel.searchUser(userInput)
                recyclerView.scrollToPosition(0)
                adapter.submitList(null)

            } else {
                showToast(getString(R.string.edi_text_hint))
            }
        }

        // show the spinner when the initial state is loading
        viewModel.initLoadState.observe(this, Observer {
            state ->
            spinner.visibility = if (state == NetworkState.LOADING) View.VISIBLE else View.GONE
            recyclerView.visibility = if (state == NetworkState.LOADING) View.GONE else View.VISIBLE
        })

        // show alert dialog if network error
        viewModel.networkState.observe(this, Observer {
            state -> if (state.status == Status.FAILED) showAlertDialog(state.msg)
        })
    }

    private fun closeSoftKeyboard() {
        val inputMethodMgr: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodMgr.hideSoftInputFromWindow(input.windowToken, 0)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun showAlertDialog(message: String?) {
        AlertDialog.Builder(this)
            .setTitle(R.string.retry_dialog_hint)
            .setMessage(message)
            .setPositiveButton(R.string.retry_dialog_pos_button, object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, p1: Int) {
                    dialog?.dismiss()
                    viewModel.retry()
                }

            })
            .setNegativeButton(R.string.retry_dialog_neg_button, object:DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, p1: Int) {
                    dialog?.dismiss()
                }
            })
            .setCancelable(false)
            .show()
    }
}
