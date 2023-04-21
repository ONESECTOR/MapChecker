package com.sector.mapchecker.ui.map

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
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
import com.sector.mapchecker.R
import com.sector.mapchecker.databinding.FragmentMapBinding
import com.sector.mapchecker.extension.zoomIn
import com.sector.mapchecker.extension.zoomOut
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
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

    private var mapKitObj: MapKit? = null

    private var userLocationLayer: UserLocationLayer? = null

    private var userLocationObjectListener = object : UserLocationObjectListener {

        override fun onObjectAdded(userLocationView: UserLocationView) {
            userLocationLayer?.setAnchor(
                PointF((binding.mapView.width * 0.5).toFloat(), (binding.mapView.height * 0.5).toFloat()),
                PointF((binding.mapView.width * 0.5).toFloat(), (binding.mapView.height * 0.83).toFloat())
            )
            userLocationView.arrow.setIcon(
                ImageProvider.fromResource(
                    requireContext(),
                    R.drawable.ic_my_location
                )
            )
            val pinIcon = userLocationView.pin.useCompositeIcon()
            pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(requireContext(), R.drawable.ic_plus),
                IconStyle()
                    .setAnchor(PointF(0F, 0F))
                    .setZIndex(0F)
                    .setScale(1F)
            )
            pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(requireContext(), R.drawable.ic_minus),
                IconStyle()
                    .setAnchor(PointF(0.5F, 0.5F))
                    .setRotationType(RotationType.ROTATE)
                    .setZIndex(0F)
                    .setScale(1F)
            )
            userLocationView.accuracyCircle.fillColor = Color.BLUE
        }

        override fun onObjectRemoved(p0: UserLocationView) {

        }

        override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

        }
    }

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
        setCameraToUser()
    }

    private fun setCameraToUser() {
        mapKitObj?.resetLocationManagerToDefault()
        userLocationLayer = mapKitObj?.createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer?.isVisible = true
        userLocationLayer?.isHeadingEnabled = true
        userLocationLayer?.setObjectListener(userLocationObjectListener)
    }
}