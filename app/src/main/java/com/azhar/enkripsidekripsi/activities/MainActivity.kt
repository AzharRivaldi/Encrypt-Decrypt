package com.azhar.enkripsidekripsi.activities

import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.azhar.enkripsidekripsi.databinding.ActivityMainBinding
import com.azhar.enkripsidekripsi.networking.ApiEndpoint
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding {
        ActivityMainBinding.inflate(layoutInflater)
    }

    var strEncrypt: String = ""
    var strDecrypt: String = ""
    var strResultEncrypt: String = ""
    var strResultDecrypt: String = ""
    var strCopyEncrypt: String = ""
    var strCopyDecrypt: String = ""
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle("Mohon Tunggu...")
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Sedang menampilkan hasil")

        //hide item layout
        binding.linearEncrypt.visibility = View.GONE
        binding.linearDecrypt.visibility = View.GONE
        binding.imageClearEncrypt.visibility = View.GONE
        binding.imageClearDecrypt.visibility = View.GONE

        //clear data encrypt with image delete
        binding.imageClearEncrypt.setOnClickListener {
            binding.teksEncrypt.text.clear()
            binding.linearEncrypt.visibility = View.GONE
            binding.imageClearEncrypt.visibility = View.GONE
        }

        //clear data decrypt with image delete
        binding.imageClearDecrypt.setOnClickListener {
            binding.teksDecrypt.text.clear()
            binding.linearDecrypt.visibility = View.GONE
            binding.imageClearDecrypt.visibility = View.GONE
        }

        //btn set encrypt
        binding.btnEncrypt.setOnClickListener {
            strEncrypt = binding.teksEncrypt.text.toString()
            if (strEncrypt.isEmpty()) {
                Toast.makeText(this@MainActivity, "Form tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            } else {
                getEncrypt(strEncrypt)
                val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        //btn set decrypt
        binding.btnDecrypt.setOnClickListener {
            strDecrypt = binding.teksDecrypt.text.toString()
            if (strDecrypt.isEmpty()) {
                Toast.makeText(this@MainActivity, "Form tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            } else {
                getDecrypt(strDecrypt)
                val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    private fun getEncrypt(strEncrypt: String) {
        progressDialog?.show()
        AndroidNetworking.get(ApiEndpoint.BASEURL_ENCRYPT)
                .addPathParameter("encrypt", strEncrypt)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        progressDialog?.dismiss()
                        try {
                            val jsonObject = response.getJSONObject("result")
                            strResultEncrypt = jsonObject.getString("encrypt")
                            binding.tvHasilEncrypt.text = strResultEncrypt
                            binding.tvHasilEncrypt.setOnClickListener { view ->
                                strCopyEncrypt = binding.tvHasilEncrypt.text.toString()
                                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipData = ClipData.newPlainText("strCopyEncrypt", strCopyEncrypt)
                                clipboardManager.setPrimaryClip(clipData)
                                Snackbar.make(view, "$strCopyEncrypt Added to Copy", Snackbar.LENGTH_SHORT).show()
                            }
                            binding.imageClearEncrypt.visibility = View.VISIBLE
                            binding.linearEncrypt.visibility = View.VISIBLE
                        } catch (e: JSONException) {
                            Toast.makeText(this@MainActivity,
                                    "Oops, gagal menampilkan enkripsi.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        progressDialog?.dismiss()
                        Toast.makeText(this@MainActivity,
                                "Oops! Sepertinya ada masalah dengan koneksi internet kamu.", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun getDecrypt(strDecrypt: String) {
        progressDialog?.show()
        AndroidNetworking.get(ApiEndpoint.BASEURL_DECRYPT)
                .addPathParameter("decrypt", strDecrypt)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        progressDialog?.dismiss()
                        try {
                            val jsonObject = response.getJSONObject("result")
                            strResultDecrypt = jsonObject.getString("decrypt")
                            binding.tvHasilDecrypt.text = strResultDecrypt
                            binding.tvHasilDecrypt.setOnClickListener { view ->
                                strCopyDecrypt = binding.tvHasilDecrypt.text.toString()
                                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipData = ClipData.newPlainText("strCopyDecrypt", strCopyDecrypt)
                                clipboardManager.setPrimaryClip(clipData)
                                Snackbar.make(view, "$strCopyDecrypt Added to Copy", Snackbar.LENGTH_SHORT).show()
                            }
                            binding.imageClearDecrypt.visibility = View.VISIBLE
                            binding.linearDecrypt.visibility = View.VISIBLE
                        } catch (e: JSONException) {
                            Toast.makeText(this@MainActivity,
                                    "Oops, gagal menampilkan dekripsi.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        progressDialog?.dismiss()
                        Toast.makeText(this@MainActivity,
                                "Oops! Sepertinya ada masalah dengan koneksi internet kamu.", Toast.LENGTH_SHORT).show()
                    }
                })
    }

}