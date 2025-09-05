package com.thinkmay.thinkmay_app_beta // <-- Make sure this matches your package name

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

class NativeActivity : AppCompatActivity() { // Renamed to match your error log

    private lateinit var drawerLayout: DrawerLayout
    private var flutterFragment: FlutterFragment? = null

    private val FLUTTER_ENGINE_ID = "my_flutter_drawer_engine"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Assuming your layout file is activity_native.xml or similar
        setContentView(R.layout.activity_native_video) 

        drawerLayout = findViewById(R.id.drawer_layout)
        findViewById<Button>(R.id.open_drawer_button).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // --- CONFIGURE AND CACHE THE FLUTTER ENGINE ---
        val flutterEngine = FlutterEngine(this).also {
            // THE FIX IS HERE: Set the initial route on the engine's NavigationChannel
            it.navigationChannel.setInitialRoute("/drawer") 
            
            // Now, execute Dart
            it.dartExecutor.executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )
        }
        FlutterEngineCache.getInstance().put(FLUTTER_ENGINE_ID, flutterEngine)
        // ------------------------------------------------

        addFlutterFragment()
    }

    private fun addFlutterFragment() {
        flutterFragment = supportFragmentManager
            .findFragmentByTag("flutter_fragment") as? FlutterFragment

        if (flutterFragment == null) {
            // Now, when we build the fragment, we don't need to specify the route
            // because the engine it's about to use already knows where to go.
            val newFlutterFragment = FlutterFragment.withCachedEngine(FLUTTER_ENGINE_ID)
                .build<FlutterFragment>() // <-- .initialRoute() is removed

            flutterFragment = newFlutterFragment
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.flutter_fragment_container,
                    newFlutterFragment,
                    "flutter_fragment"
                )
                .commit()
        }
    }
}