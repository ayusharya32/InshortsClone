package com.easycodingg.newsfeedapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.easycodingg.newsfeedapp.databinding.ActivityNewsTypeSelectionBinding
import com.easycodingg.newsfeedapp.models.User
import com.easycodingg.newsfeedapp.util.Constants.USERS_COLLECTION
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class NewsTypeSelectionActivity: AppCompatActivity() {

    private lateinit var binding: ActivityNewsTypeSelectionBinding

    private lateinit var auth: FirebaseAuth
    private val usersRef = Firebase.firestore.collection(USERS_COLLECTION)

    private var tags = listOf<String>()
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsTypeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        setupTagsList()
        getUserAndSetupChips()

        binding.btnDone.setOnClickListener {
            saveNewsPrefs()
        }

        binding.btnSkip.setOnClickListener {
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }.also {
                startActivity(it)
                finish()
            }
        }
    }

    private fun setupTagsList() {
        tags = listOf("Cryptocurrency", "Stock Market", "Trading", "Investing", "Hollywood",
                "Cricket", "Football", "Sports", "Health", "Environment", "Music", "Education",
                "Space", "Technology")
    }

    private fun setupChipGroup() {

        tags.forEach { chipText ->
            val chip = layoutInflater.inflate(R.layout.chip_item, binding.root, false) as Chip
            chip.text = chipText

            val newsPrefs = user?.newsPrefs
            newsPrefs?.let {
                if (it.contains(chipText)) {
                    chip.isChecked = true
                }
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun getUserAndSetupChips() {
        binding.pbNewsSelection.visibility = View.VISIBLE
        val userEmail = auth.currentUser?.email

        usersRef.whereEqualTo("email", userEmail).get()
                .addOnSuccessListener {
                    user = it.documents[0].toObject<User>()
                    setupChipGroup()
                }.addOnCompleteListener {
                    binding.pbNewsSelection.visibility = View.GONE
                }
    }

    private fun saveNewsPrefs() {
        binding.pbNewsSelection.visibility = View.VISIBLE

        val checkedIndexList = getCheckedChipsIndexList()
        val checkedTagsList = mutableListOf<String>()

        checkedIndexList.forEach { checkedIndex ->
            checkedTagsList.add(tags[checkedIndex])
        }

        user?.let {
            usersRef.document(it.email)
                    .update("newsPrefs", checkedTagsList)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Preferences Updated Successfully",
                                Toast.LENGTH_SHORT).show()

                        Intent(this, HomeActivity::class.java).also { intent ->
                            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            startActivity(intent)
                            finish()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
                        Timber.d(e)
                    }.addOnCompleteListener {
                        binding.pbNewsSelection.visibility = View.GONE
                    }
        }
    }

    private fun getCheckedChipsIndexList(): List<Int> {
        val checkedIndexList = mutableListOf<Int>()

        binding.apply {
            for(chipIndex in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(chipIndex) as Chip

                if(chip.isChecked) {
                    checkedIndexList.add(chipIndex)
                }
            }
        }
        return checkedIndexList
    }
}

