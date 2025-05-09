package com.example.beatsfit.view

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.beatsfit.util.LocationData
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun LiveLocationMap(contact: Contact, navController: NavHostController) {
    var liveLocation by remember { mutableStateOf(LocationData(0.0,0.0)) }
    val context = LocalContext.current
    var currentMarker: Marker? by remember { mutableStateOf(null) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(20.0)
        }
    }

    LaunchedEffect(contact) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("mobile_number", contact.phoneNumber)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents
                if (doc.isNotEmpty()) {
                    val contactId = doc[0].id

                    FirebaseFirestore.getInstance().collection("userLiveLocation")
                        .document(contactId)
                        .addSnapshotListener { document, _->
                            val lat = document?.getDouble("latitude") ?: 0.0
                            val lon = document?.getDouble("longitude") ?: 0.0
                            liveLocation = LocationData(lat, lon)

                            val geoPoint = GeoPoint(lat, lon)
                            mapView.controller.setZoom(18.5) // or keep 20.0
                            mapView.controller.setCenter(geoPoint) // ✅ center the map

                            currentMarker?.let { mapView.overlays.remove(it) }

                            val marker = Marker(mapView).apply {
                                position = geoPoint
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Selected Location"
                            }
                            mapView.overlays.add(marker)
                            currentMarker = marker

                            mapView.invalidate()
                        }
                }
            }
    }



    Column(modifier = Modifier.fillMaxSize()){
        AndroidView(factory = { mapView }, modifier = Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }
    }
}
