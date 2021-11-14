package com.smart.smartbalibackpaker.dashboard

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.facebook.CallbackManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.smart.smartbalibackpaker.R
import com.smart.smartbalibackpaker.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var user: FirebaseUser
    private lateinit var dbReference: DatabaseReference
    private var userId: String? = null
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding
    private var callbackManager = CallbackManager.Factory.create()

    private var myUid: String? = null

    private var hashMap: HashMap<String, Any> = HashMap()

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_allplace,
            R.string.tab_touristplace,
            R.string.tab_hotelvilla,
            R.string.tab_worshipplace
        )
        const val FACEBOOK_EMAIL = "facebook_email"
        const val FACEBOOK_NAME = "facebook_NAME"
        const val FACEBOOK_IMAGE = "facebook_image"
    }

    override fun onStart() {
        checkOnlineState("online")
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        checkOnlineState(System.currentTimeMillis().toString())
    }

    override fun onResume() {
        checkOnlineState("online")
        super.onResume()
    }

    override fun onDestroy() {
        checkOnlineState(System.currentTimeMillis().toString())
        super.onDestroy()
    }

    private fun checkOnlineState(onlineState: String) {
        myUid = FirebaseAuth.getInstance().currentUser?.uid
        dbReference = FirebaseDatabase.getInstance().getReference("users").child(myUid.toString())

        hashMap.put("onlineState", onlineState)
        dbReference.updateChildren(hashMap)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        dbReference = db.getReference("users")
        user = auth.currentUser!!
        userId = auth.currentUser?.uid

        val name = activity?.intent?.getStringExtra(FACEBOOK_NAME)
        binding?.tvWelcomeUsername?.text = "Welcome $name"

        val query = dbReference.orderByChild("email").equalTo(user.email)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val username = "" + ds.child("username").value
                    val image = ds.child("image").value

                    binding?.tvWelcomeUsername?.text = "Welcome $username"

                    if (image == "") {
                        context?.let {
                            Glide.with(it)
                                .load(R.drawable.account)
                                .apply(RequestOptions().override(55, 55))
                                .into(binding!!.ivWelcomePhoto)
                        }
                    } else {
                        context?.let {
                            Glide.with(it)
                                .load(image)
                                .apply(RequestOptions().override(55, 55))
                                .into(binding!!.ivWelcomePhoto)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        return binding?.root
//        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO -> REMOVE AFTER SIMULATING LAYOUT
        val dateFormat = SimpleDateFormat("dd/M/yyyy")
        val currentDate = dateFormat.format(Date())
        _binding?.tvWelcomeDate?.text = currentDate

        val dashboardPlaceAdapter = activity?.let { DashboardPlaceAdapter(it) }

        binding?.vpDashboard?.adapter = dashboardPlaceAdapter
        TabLayoutMediator(
            binding?.layoutTabLayout!!,
            binding?.vpDashboard!!
        ) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        val imageList = ArrayList<SlideModel>() // Create image list

        imageList.add(
            SlideModel(
                R.drawable.besakih,
                "Pura Besakih",
                ScaleTypes.CENTER_CROP
            )
        )
        imageList.add(
            SlideModel(
                R.drawable.sanur,
                "Pantai Sanur",
                ScaleTypes.CENTER_CROP
            )
        )
        imageList.add(
            SlideModel(
                R.drawable.finns,
                "Finns Beach Club",
                ScaleTypes.CENTER_CROP
            )
        )
        val imageSlider = binding?.imageSlider
        imageSlider?.setImageList(imageList)

    }


}