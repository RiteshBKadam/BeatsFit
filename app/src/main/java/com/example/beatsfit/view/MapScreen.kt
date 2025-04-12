package com.example.beatsfit.view

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.beatsfit.util.LocationUtils
import com.example.beatsfit.viewmodel.LocationViewModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
@Composable
fun MapsScreen(
    context: Context,
    friendsId: String
) {
    val firestore = FirebaseFirestore.getInstance()
    var friendsLatitude by remember { mutableStateOf(0.0) }
    var friendsLongitude by remember { mutableStateOf(0.0) }
    var locationFetched by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        firestore.collection("healthData")
            .document(friendsId)
            .collection("latitude")
            .orderBy(FieldPath.documentId(), com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    val doc = docs.documents[0]
                    val _lat = doc.getDouble("value")
                    val _lon = doc.getDouble("value") // assuming you also store longitude

                    if (_lat != null && _lon != null) {
                        friendsLatitude = _lat
                        friendsLongitude = _lon
                        locationFetched = true
                    }
                }
            }
            .addOnFailureListener{
                Log.e("Firesdsafcdsafwdsf",it.toString())
            }
        firestore.collection("healthData")
            .document(friendsId)
            .collection("longitude")
            .orderBy(FieldPath.documentId(), com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    val doc = docs.documents[0]
                    val _lon = doc.getDouble("value") // assuming you also store longitude

                    if (_lon != null) {
                        friendsLongitude = _lon
                        locationFetched = true
                    }
                }
            }
            .addOnFailureListener{
                Log.e("Firesdsafcdsafwdsf",it.toString())
            }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
            val mapView = remember {
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(20.0)
                }
            }
            Toast.makeText(context,friendsLongitude.toString(),Toast.LENGTH_LONG).show()
            LaunchedEffect(friendsLatitude, friendsLongitude) {
                val geoPoint = GeoPoint(friendsLatitude.toDouble(), friendsLongitude.toDouble())
                mapView.controller.setCenter(geoPoint)

                val marker = Marker(mapView).apply {
                    position = geoPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Friend's Location"
                }
                mapView.overlays.clear()
                mapView.overlays.add(marker)
                mapView.invalidate()
            }

            AndroidView(factory = { mapView }, modifier = Modifier.weight(1f))

    }
}

