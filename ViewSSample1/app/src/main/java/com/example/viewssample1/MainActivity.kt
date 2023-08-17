package com.example.viewssample1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val EXTERNAL_STORAGE_PERMISSION_CODE = 23
    private var mpm: PackageManager? = null
    private var mypkname: String = ""

    //private var textView1: TextView? = null

    private var adapter: ArrayAdapter<String>?= null



    private var insource: File?= null
    private var selectName: String?= ""
    private var copyStr: String?= ""
    private var copyToPath: String?= "/storage/emulated/0/Download"

    private var allFn: MutableList<String> = mutableListOf<String>()
    private var allFp: MutableList<String> = mutableListOf<String>()


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),EXTERNAL_STORAGE_PERMISSION_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getAllPkgs(){
        val appInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mpm?.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0L))
        } else {
            mpm?.getInstalledApplications(0)
        }
        Log.d("zzzz","appInfos:" + appInfos)
        if (appInfos != null) {
            for (pkgn in appInfos){
                val pkgna = pkgn.packageName
                Log.d("zzzz", pkgna)
            }
        }

        //val ai: ApplicationInfo = this.getPackageManager().getApplicationInfo("com.sprd.engineermode",PackageManager.ApplicationInfoFlags.of(0))
        /*
        val ai = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getPackageManager().getApplicationInfo("com.sprd.engineermode",PackageManager.ApplicationInfoFlags.of(0))
        } else {
            this.getPackageManager().getApplicationInfo("com.sprd.engineermode",0)
        }
        Log.d("zzzz","ai:" + ai.isProfileable)

         */
    }

    private fun copyFile(sourceLocation: File?, targetLocation: File?) {
        //Files.copy(sourceLocation,targetLocation, StandardCopyOption.REPLACE_EXISTING)
        var inTT: InputStream = FileInputStream(sourceLocation)
        Log.d("zzzz",inTT.toString())
        val out: OutputStream = FileOutputStream(targetLocation)

        // Copy the bits from instream to outstream
        val buf = ByteArray(1024)
        var len: Int
        while (inTT.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        inTT.close()
        out.close()
    }


    private  fun requestForExternalStoragePermission1(context: Context) {
        Toast.makeText(context,"zzzz", Toast.LENGTH_SHORT).show()
        ActivityCompat.requestPermissions(context as Activity,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),EXTERNAL_STORAGE_PERMISSION_CODE)
        Log.d("zzzz","requestForExternalStoragePermission1")
    }

    private fun test(){
        Log.d("zzz t:", "ggggg")
        val path: String = "/storage/emulated/0/DCIM/XXXX"
        val fileL = File(path)
        val fileLxx = fileL.listFiles()
        Log.d("zzz t:", fileLxx[0].toString())
        for (file in fileLxx) {
            val name: String = file.getName()
            Log.d("zzz t:", name + ", isF:")
        }
    }

    public fun showAllFolder3(pathh: String ){
        Log.d("zzz s:",pathh)
        val fileL = File(pathh)
        val fileLxx = fileL.listFiles()
        Log.d("zzz s:",pathh + ", listFiles:" + fileLxx)
        if (fileLxx != null) {
            for (file in fileL.listFiles()) {
                val name: String = file.getName()
                val isFolder: Boolean = file.isDirectory()
                Log.d("zzz s:", name + ", isF:" + isFolder)
                if (isFolder) {
                    val path = file.getPath()
                    showAllFolder3(path)
                } else {
                    Log.d("zzz s", name)
                    val ePath = file.getPath()
                    Log.d("zzz s",name + ", Path:" + ePath)
                    allFn.add(name)
                    allFp.add(ePath)
                }

            }
        }
    }

    private  fun showAllFolder() {
        val root = Environment.getExternalStorageDirectory()
        Log.d("zzz s1",root.toString())
        val files: Array<File> = root.listFiles()
        for (file in root.listFiles()) {
            val name: String = file.getName()
            val isFolder: Boolean = file.isDirectory()
            Log.d("zzz s1",name.toString() + ", isFolder:" + isFolder)
            if (isFolder) {
                Log.d("zzz s1",name.toString() + ", isFolder:" + isFolder)
                val path = file.getPath()
                showAllFolder3(path)
            } else {
                val ePath = file.getPath()
                Log.d("zzz s1",name + ", Path:" + ePath)
                allFn.add(name)
                allFp.add(ePath)
            }
        }
        Log.d("zzz s1",allFn.toString())

        //Log.d("zzz",files.toString())
    }

    private fun getBucketId(path: String): String {
        return path.lowercase(Locale.getDefault()).hashCode().toString()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("zzzz",requestCode.toString())
        when (requestCode) {
            23 -> {
                var allGranted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false
                    }
                }
                if (allGranted) {
                    mpm = this.packageManager
                    mypkname = getPackageName()

                    showAllFolder()
                    Log.d("zzzz","allFiless:" + allFn.toString())

                    val textView : TextView = findViewById(R.id.textView)
                    textView.setGravity(Gravity.CENTER)

                    val spinner: Spinner = findViewById(R.id.spinner)

                    val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allFn)
                    spinner.adapter = arrayAdapter

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            textView.setText("")
                            insource =  File(allFp?.get(position))
                            selectName = allFn?.get(position)
                            Log.d("zzzz", "select: " + allFn?.get(position))
                            textView.setText("select: " + allFn?.get(position) + "\n" +
                                    "Path:" + insource)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Code to perform some action when nothing is selected
                        }
                    }

                    val copyBtn = findViewById<Button>(R.id.button)
                    copyBtn.setOnClickListener {
                        if (insource != null) {
                            val outsource: File = File(copyToPath + "/" + selectName)
                            copyStr =
                                textView.getText().toString() + "\n" + "copy to" + "\n" + outsource.toString()
                            textView.setText(copyStr)
                            copyFile(insource, outsource)

                            copyStr = copyStr + "\n" + "Copy Finish!"
                            textView.setText(copyStr)
                            if (selectName != null){
                                allFn.add(selectName!!)
                                allFp.add(outsource.toString())
                            }

                            adapter?.notifyDataSetChanged()
                            //MediaScannerConnection.scanFile(this, arrayOf(outsource.toString()),null,null)
                        }
                    }

                    val disBtn = findViewById<Button>(R.id.button2)
                    disBtn.setOnClickListener {
                        //權限問題, 目前設計能disable自己, 除非在源碼編譯此應用, 或修改pm內邏輯, 讓此應用可以通過檢查限制才可disable其他應用, 可參考aosp packages/app/settings內設計
                        //透過adb shell pm enable com.example.viewssample1重新enable
                        //或是透過settings應用手動enable
                        if (mypkname != "") {
                            mpm?.setApplicationEnabledSetting(
                                mypkname,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0
                            )
                        }
                        Log.d("zzzz","disable myself! pkgname:" + mypkname)
                    }
                } else {
                    Toast.makeText(this, "您拒绝了某项权限，无法进行拍照", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


