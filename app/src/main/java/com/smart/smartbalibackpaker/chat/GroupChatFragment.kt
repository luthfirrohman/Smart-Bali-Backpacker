package com.smart.smartbalibackpaker.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.smart.smartbalibackpaker.R
import com.smart.smartbalibackpaker.databinding.FragmentGroupChatBinding
import com.smart.smartbalibackpaker.databinding.FragmentPersonalChatBinding
import com.smart.smartbalibackpaker.model.GroupChatAdapter
import com.smart.smartbalibackpaker.model.ModelGroupChat

class GroupChatFragment : Fragment() {
    private var _binding: FragmentGroupChatBinding? = null
    private val binding get() = _binding
    private lateinit var auth : FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: FirebaseDatabase
    private lateinit var dbReference: DatabaseReference
    private var myUid: String? = null
    private var hashMap: HashMap<String, Any> = HashMap()


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
        // Inflate the layout for this fragment
        _binding = FragmentGroupChatBinding.inflate(layoutInflater, container, false)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = FirebaseDatabase.getInstance()

        loadGroupList()
    }

    private fun loadGroupList() {
        val ref = db.getReference("groups")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupList = ArrayList<ModelGroupChat>()
                groupList.clear()
                for (ds in snapshot.children){
                    if(ds.child("member").child(auth.uid!!).exists()){
                        val model = ds.getValue(ModelGroupChat::class.java)
                        if (model != null) {
                            groupList.add(model)
                        }
                    }
                }
                groupList.reverse()
                val adapter = context?.let { GroupChatAdapter(it, groupList) }
                _binding?.rvChatGroup?.layoutManager = LinearLayoutManager(context)
                _binding?.rvChatGroup?.setHasFixedSize(true)
                _binding?.rvChatGroup?.adapter = adapter
                Log.d("hohohohoho", adapter.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}