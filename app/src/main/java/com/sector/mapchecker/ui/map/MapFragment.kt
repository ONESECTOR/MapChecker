package com.sector.mapchecker.ui.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.sector.mapchecker.databinding.FragmentMapBinding
import com.sector.mapchecker.databinding.IcPinLayoutBinding
import com.sector.mapchecker.extension.addMarks
import com.sector.mapchecker.extension.zoomIn
import com.sector.mapchecker.extension.zoomOut
import com.sector.mapchecker.model.Mark
import com.sector.mapchecker.ui.dialogs.MarkDialog
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.ui_view.ViewProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onGotLocationPermissionResult
    )

    private val dialog by lazy {
        MarkDialog()
    }

    private var mapKitObj: MapKit? = null

    private val markCoordinates = listOf(
        Mark(
            latitude = BAKHMUT_LATITUDE,
            longitude = BAKHMUT_LATITUDE
        ),
        // Рандомные координаты придумал
        Mark(
            latitude = 12.3,
            longitude = 14.5
        ),
        Mark(
            latitude = 22.1,
            longitude = 34.8
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapKitObj = MapKitFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMarksToMap()

        binding.apply {
            btnZoomIn.setOnClickListener {
                mapView.zoomIn()
            }
            btnZoomOut.setOnClickListener {
                mapView.zoomOut()
            }
            btnZoomToMyLocation.setOnClickListener {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onGotLocationPermissionResult(granted: Boolean) {
        when(granted) {
            true -> {
                onPermissionGranted()
            }
            false -> {
                val isBlockedForever = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                when {
                    isBlockedForever -> {
                        askUserToOpenAppSettings()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun askUserToOpenAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity?.packageName, null)
            )
        )
    }

    private fun onPermissionGranted() {
        // Нужно показывать пользовательскую метку и делать фокус на него, но мне лень)
    }

    private fun addMarksToMap() {
        val markView = ViewProvider(getMarkView())

        binding.mapView.addMarks(
            markCoordinates = markCoordinates,
            markView = markView,
            clickListener = markClickListener
        )
    }

    private fun getMarkView() = IcPinLayoutBinding.inflate(layoutInflater).root

    private val markClickListener = MapObjectTapListener { mapObject, point ->
        dialog.show(childFragmentManager, MarkDialog.TAG)
        true
    }

    companion object {
        private const val BAKHMUT_LATITUDE = 48.59441
        private const val BAKHMUT_LONGITUDE = 37.99983
    }
}