package com.kutovenko.kitstasher.ui.fragment

import com.kutovenko.kitstasher.util.EMPTY
import kotlinx.android.synthetic.main.fragment_tabbed_scanning.*

package com.kutovenko.kitstasher.ui.fragment

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager

import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.kutovenko.kitstasher.R
import com.kutovenko.kitstasher.databinding.FragmentTabbedScanningBinding
import com.kutovenko.kitstasher.db.DbConnector
import com.kutovenko.kitstasher.model.Item
import com.kutovenko.kitstasher.model.StashItem
import com.kutovenko.kitstasher.network.AsyncApp42ServiceApi
import com.kutovenko.kitstasher.ui.adapter.UiAlertDialogAdapter
import com.kutovenko.kitstasher.ui.listener.OnFragmentInteractionListener
import com.kutovenko.kitstasher.util.Helper
import com.kutovenko.kitstasher.util.MyConstants
import com.shephertz.app42.paas.sdk.android.App42Exception
import com.shephertz.app42.paas.sdk.android.storage.Query
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder
import com.shephertz.app42.paas.sdk.android.storage.Storage

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import com.kutovenko.kitstasher.ui.MainActivity.MY_PERMISSIONS_REQUEST_CAMERA
import com.kutovenko.kitstasher.ui.MainActivity.asyncService

/**
 * Created by Алексей on 21.04.2017. Adding new items by Scanning
 */

class ScanFragment : Fragment(), AsyncApp42ServiceApi.App42StorageServiceListener {

    var barcode: String? = null
        private set
    private var ownerId: String? = null
    private var dbConnector: DbConnector? = null
    private var mListener: OnFragmentInteractionListener? = null
    var workMode: String? = null
        private set
    private var context: Context? = null
    private var binding: FragmentTabbedScanningBinding? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onAttachToParentFragment(parentFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentTabbedScanningBinding.inflate(inflater, container, false)

        checkCameraPermissions()



        barcode = MyConstants.EMPTY

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        scanTag = this.tag

        return binding!!.root
    }


    private fun openManualAdd() {

        mListener!!.onFragmentInteraction(barcode, workMode)
        val viewPager = activity!!.findViewById(com.kutovenko.kitstasher.R.id.viewpagerAdd)
        viewPager.setCurrentItem(1)
        initiateScanner(callback)
    }

    private var barcode = EMPTY

    private fun initiateScanner(callback: BarcodeCallback) {
        IntentIntegrator(activity).apply {
            setBeepEnabled(true)
            setOrientationLocked(false)
        }
        barcodeview.decodeContinuous(callback)
    }

    private val callback: BarcodeCallback
        get() = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if (result.text != null && result.text != barcode) {
                    barcode = result.text
                } else {
                    initiateScanner(callback)
                }
            }
            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        }

    override fun onPause() {
        super.onPause()
        barcodeview.pauseAndWait()
    }

    override fun onResume() {
        super.onResume()
        barcodeview.resume()
        initiateScanner(callback)
        barcode = EMPTY
    }

    private fun checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.CAMERA)) {
                Toast.makeText(activity, com.kutovenko.kitstasher.R.string.we_cant_read_barcodes,
                        Toast.LENGTH_LONG).show()
                val fragment = NoPermissionFragment.newInstance(Manifest.permission.CAMERA, MyConstants.TYPE_SUPPLY)
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment)
                fragmentTransaction.commitAllowingStateLoss()
            } else {
                ActivityCompat.requestPermissions(activity!!,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA)
            }
        }
    }

    companion object {
        var scanTag: String = "ScanFragment"
    }
}
