package com.example.pockemongo

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkPermission()
        LoadPokemon()
    }
    var AccesLocation=123
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),AccesLocation)
                return
            }
        }
        GetUserLocation()
    }
    fun GetUserLocation(){
        Toast.makeText(this,"User Location access on ",Toast.LENGTH_LONG).show()
        //TODO :will implement later
        var myLocation=MyLocationListener()
        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)
        var mythread=myThred()
        mythread.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            AccesLocation->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    GetUserLocation()
                }
                else{
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


    }
    var location:Location?=null
    //Get user Location
    inner class MyLocationListener:LocationListener{
        constructor(){
            location= Location("Start")
            location!!.longitude=0.0
            location!!.longitude=0.0
        }
        override fun onLocationChanged(p0: Location?) {
         location=p0
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            //TODO("Not yet implemented")
        }

        override fun onProviderEnabled(provider: String?) {
            //TODO("Not yet implemented")
        }

        override fun onProviderDisabled(provider: String?) {
            //TODO("Not yet implemented")
        }

    }
    var oldLocation:Location?=null
    inner class myThred: Thread{
     constructor():super(){
         oldLocation= Location("Start")
         oldLocation!!.longitude=0.0
         oldLocation!!.longitude=0.0

     }

        override fun run() {
            while (true){
                try {
                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }
                    oldLocation=location
                    runOnUiThread {
                        mMap!!.clear()

                       // SHOW ME
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet("here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
                        )

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))
                        //SHOW POCKEMONS
                        for(i in 0..listPokemon.size-1){
                            var newPokemon=listPokemon[i]
                            if(newPokemon.isCatch==false){
                                val pockemonLoc = LatLng(newPokemon.location!!.latitude,
                                    newPokemon.location!!.longitude)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(sydney)
                                        .title(newPokemon.name!!)
                                        .snippet(newPokemon.des!!+",+power:"+newPokemon!!.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!)))
                                if(location!!.distanceTo(newPokemon.location)<2)
                                {
                                   newPokemon.isCatch==true
                                    listPokemon[i]=newPokemon
                                    PlayerPower+=newPokemon.power!!
                                    Toast.makeText(applicationContext,
                                        "You catch a new Pockemon your new power is"+PlayerPower,
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    Thread.sleep(1000)
                }catch (ex:Exception){}
            }
        }
    }
    var PlayerPower=0.0
    var listPokemon=ArrayList<Pokemon>()
    fun LoadPokemon(){
        listPokemon.add(Pokemon(R.drawable.charmander,"Charmander",
            "Living in India Japan",55.0, 37.7789994893035, -122.401846647263))
        listPokemon.add(Pokemon(R.drawable.bulbasaur,"Bulbasaur",
            "Living in USA",90.5, 37.7949568502667, -122.410494089127))

        listPokemon.add(Pokemon(R.drawable.squirtle,"Squirtle",
            "Living in Thailand",33.5, 37.7816621152613, -122.41225361824))


    }

}