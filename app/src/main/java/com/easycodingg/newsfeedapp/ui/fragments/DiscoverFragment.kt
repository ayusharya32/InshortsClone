package com.easycodingg.newsfeedapp.ui.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.easycodingg.newsfeedapp.LoginActivity
import com.easycodingg.newsfeedapp.NewsTypeSelectionActivity
import com.easycodingg.newsfeedapp.R
import com.easycodingg.newsfeedapp.adapters.QueryAdapter
import com.easycodingg.newsfeedapp.databinding.FragmentDiscoverBinding
import com.easycodingg.newsfeedapp.models.User
import com.easycodingg.newsfeedapp.ui.viewmodels.DiscoverViewModel
import com.easycodingg.newsfeedapp.util.Constants.DATE_QUERY_TYPE
import com.easycodingg.newsfeedapp.util.Constants.EXTRA_USER
import com.easycodingg.newsfeedapp.util.Constants.SEARCH_QUERY_TYPE
import com.easycodingg.newsfeedapp.util.Constants.USERS_COLLECTION
import com.easycodingg.newsfeedapp.util.viewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DiscoverFragment: Fragment(R.layout.fragment_discover) {

    private val binding by viewBinding(FragmentDiscoverBinding::bind)
    private val viewModel: DiscoverViewModel by viewModels()

    private lateinit var queryAdapter: QueryAdapter
    private lateinit var newsTypesAdapter: QueryAdapter
    private lateinit var auth: FirebaseAuth
    private val usersRef = Firebase.firestore.collection(USERS_COLLECTION)

    private var user: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        setupRecyclerView()
        setDatePickerDialog()

        if(user != null) {
            user?.let { it ->
                val toolbarText = "Hello, ${it.firstName}"
                binding.tvToolbarTitle.text = toolbarText

                val userFeedQuery = it.newsPrefs.joinToString(separator = " OR ")
                setupNewsTypesAdapter(userFeedQuery)
            }
        } else {
            setUserNameOnToolbar()
        }

        binding.etSearchNews.apply {
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    val searchQuery = this.text.trim().toString()
                    val action = DiscoverFragmentDirections
                        .actionDiscoverFragmentToNewsFragment(SEARCH_QUERY_TYPE, searchQuery)

                    this.text.clear()
                    findNavController().navigate(action)

                    return@setOnKeyListener true
                }
                false
            }
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.miDayNightToggle -> {

                    when (requireContext().resources.configuration
                            .uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                        Configuration.UI_MODE_NIGHT_NO -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    }
                }
                R.id.miNewsPrefs -> {
                    user?.let {
                        Intent(requireContext(), NewsTypeSelectionActivity::class.java).apply {
                            putExtra(EXTRA_USER, it)
                        }.also {
                            startActivity(it)
                        }
                    }
                }
                R.id.miLogout -> {
                    auth.signOut()
                    requireActivity().finish()
                    Intent(requireContext(), LoginActivity::class.java).also { intent ->
                        startActivity(intent)
                    }
                }
            }
            false
        }
    }

    private fun setupRecyclerView() {
        queryAdapter = QueryAdapter(viewModel.queryList) { query ->

            Timber.d("Type - ${query.queryType}, Value - ${query.queryValue}")
            val action = DiscoverFragmentDirections
                    .actionDiscoverFragmentToNewsFragment(query.queryType, query.queryValue)
            findNavController().navigate(action)
        }

        binding.rvTypes.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = queryAdapter
        }
    }

    private fun setDatePickerDialog() {
        val calender = Calendar.getInstance()
        val datePickerListener =
            DatePickerDialog.OnDateSetListener() { _, year, monthOfYear, dayOfMonth ->
                calender.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, monthOfYear)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                updateDateEditText(calender)
                setBtnDateSearchListener(calender)
            }

        binding.etDate.setOnClickListener {
            val tempDate = Calendar.getInstance()
            tempDate.add(Calendar.DAY_OF_YEAR, -30)

            Timber.d("Min Date: ${tempDate.time}")
            val minDateInMillis = tempDate.timeInMillis

            DatePickerDialog(
                    requireContext(),
                    datePickerListener,
                    calender.get(Calendar.YEAR),
                    calender.get(Calendar.MONTH),
                    calender.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
                datePicker.minDate = minDateInMillis
                show()
            }
        }
    }

    private fun updateDateEditText(calendar: Calendar) {
        val simpleDateFormat = SimpleDateFormat("dd/MMMM/yyyy", Locale.UK)
        binding.etDate.setText(simpleDateFormat.format(calendar.time))
    }

    private fun setBtnDateSearchListener(calendar: Calendar) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        val formattedSelectedDate = sdf.format(calendar.time)

        calendar.add(Calendar.DATE, 1)
        val formattedNextDate = sdf.format(calendar.time)

        val dateString = formattedSelectedDate + "N" + formattedNextDate

        binding.btnDateSearch.setOnClickListener {
            Timber.d("Formatted Date: ${dateString.split("N")}")
            binding.etDate.text.clear()

            val action = DiscoverFragmentDirections
                    .actionDiscoverFragmentToNewsFragment(DATE_QUERY_TYPE, dateString)
            findNavController().navigate(action)
        }
    }

    private fun setUserNameOnToolbar() {
        val userEmail = auth.currentUser?.email

        usersRef.whereEqualTo("email", userEmail).get()
            .addOnSuccessListener {
                user = it.documents[0].toObject<User>()
                val toolbarText = "Hello, ${user?.firstName}"
                binding.tvToolbarTitle.text = toolbarText

                val userFeedQuery = user?.newsPrefs?.joinToString(separator = " OR ")
                setupNewsTypesAdapter(userFeedQuery!!)
            }
    }

    private fun setupNewsTypesAdapter(userFeedQuery: String) {
        newsTypesAdapter = QueryAdapter(viewModel.getNewsTypesList(userFeedQuery)) { query ->

            val action = DiscoverFragmentDirections
                    .actionDiscoverFragmentToNewsFragment(query.queryType, query.queryValue)
            findNavController().navigate(action)
        }

        binding.rvPrefs.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = newsTypesAdapter
        }
    }
}