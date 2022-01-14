package com.example.memeshareapp

import android.R.attr
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.net.Inet4Address
import android.graphics.Bitmap

import android.R.attr.bitmap

import android.os.ParcelFileDescriptor

import android.provider.MediaStore

import android.content.ContentValues

import android.content.ContentResolver
import android.content.ContentValues.TAG

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.*
import android.R.attr.bitmap
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap


class MainActivity : AppCompatActivity() {

    var currentImageUrl:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMeme()
    }
    @SuppressLint("SetTextI18n")
    private fun loadMeme(){
        findViewById<ProgressBar>(R.id.pbar).visibility= View.VISIBLE
        if(findViewById<ProgressBar>(R.id.pbar).getVisibility() == View.VISIBLE) {
            findViewById<Button>(R.id.ShareButton).setEnabled(true)
        }else {
            findViewById<Button>(R.id.ShareButton).isEnabled=false
        }

        // Instantiate the cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap
        val network = BasicNetwork(HurlStack())

        val textView = findViewById<TextView>(R.id.text)
        val queue = Volley.newRequestQueue(this)
        //Api url
        val url = "https://meme-api.herokuapp.com/gimme"

        val JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response ->
                currentImageUrl=response.getString("url")
                Glide.with(this).load(currentImageUrl).thumbnail(0.05f).listener(object: RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        findViewById<ProgressBar>(R.id.pbar).visibility=View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        findViewById<ProgressBar>(R.id.pbar).visibility=View.GONE
                        return false
                    }
                }).into(findViewById<ImageView>(R.id.imageView))

            },
            {
                Toast.makeText(this,"OOPS! Something Went Wrong",Toast.LENGTH_LONG).show()
            })
        MySingleton.getInstance(this).addToRequestQueue(JsonObjectRequest)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun ShareMeme(view: android.view.View) {
        //works when we click on this 'share' button
        val sharebtn=findViewById<Button>(R.id.ShareButton)
        val intent = Intent(Intent.ACTION_SEND).setType("image/*")
        val image=findViewById<ImageView>(R.id.imageView)
        // Step 2: Get Bitmap from your imageView
        val bitmap = image.drawable.toBitmap() // your imageView here.

        // Step 3: Compress image
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        // Step 4: Save image & get path of it

        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "tempimage", null)

        // Step 5: Get URI of saved image
        val uri = Uri.parse(path)

        // Step 6: Put Uri as extra to share intent
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        // Step 7: Start/Launch Share intent
        startActivity(intent)
    }
    fun NextMeme(view: android.view.View) {
        //works when we click on this 'next' button
        loadMeme()
        Toast.makeText(this,"Loading",Toast.LENGTH_SHORT).show()
    }
}