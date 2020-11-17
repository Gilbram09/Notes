package com.gilbram.notes.ui.list

import android.app.AlertDialog
import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gilbram.notes.R
import com.gilbram.notes.adapter.ListAdapter
import com.gilbram.notes.databinding.FragmentListBinding
import com.gilbram.notes.model.ToDoData
import com.gilbram.notes.ui.add.SwipeToDelete
import com.gilbram.notes.util.hideKeyboard
import com.gilbram.notes.viewmodel.SharedViewModel
import com.gilbram.notes.viewmodel.ToDoViewModel
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val toDoViewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val adapter: ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.sharedviewModel = sharedViewModel
        toDoViewModel.getAllData.observe(viewLifecycleOwner,{ data ->
            sharedViewModel.checkDatabaseempty(data)
            adapter.setData(data)
        })
        setupRecyclerView()
        hideKeyboard(requireActivity())
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu,menu)
        val search = menu.findItem(R.id.menu_search)
        val searchview = search.actionView as? androidx.appcompat.widget.SearchView
        searchview?.isSubmitButtonEnabled = true
        searchview?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_delete_all -> confirmAllRemove()
            R.id.menu_priority_high -> {
                toDoViewModel.sortByHighPriority.observe(this, Observer {
                    adapter.setData(it)
                })
            }
            R.id.menu_priority_low -> {
                toDoViewModel.sortByLowPriority.observe(this, Observer {
                    adapter.setData(it)
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmAllRemove() {
        AlertDialog.Builder(requireContext())
                .setTitle("hapus semua? ")
                .setMessage("Yakin Mau Hapus?")
                .setPositiveButton("Ya"){_,_->
                    toDoViewModel.daleteAllData()
                    Toast.makeText(requireContext(),"berhasil hapus", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_listFragment_to_listFragment)
                }
                .setNegativeButton("no",null)
                .create()
                .show()
    }


    private fun setupRecyclerView() {
        val rv = binding.rvTodo
        rv.adapter = adapter
        rv.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        rv.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }
        swipeToDelete(rv)
    }

    private fun swipeToDelete(rv: RecyclerView) {
        val swipeToDeleteCallBack = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val delete = adapter.dataList[viewHolder.adapterPosition]
                toDoViewModel.deleteData(delete)
                adapter.notifyItemRemoved()


                restroreDeleteData(viewHolder.itemView,delete)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(rv)
    }

    private fun restroreDeleteData(view: View, delete: ToDoData) {
        val snackbar = Snackbar.make(
                view,
                "delete : ${delete.title}",
                Snackbar.LENGTH_LONG
        )
        snackbar.setAction("undo"){
            toDoViewModel.insertData(delete)
        }
        snackbar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onQueryTextSubmit(p00: String?): Boolean {
        if (p00 != null){
            searchData(p00)
        }
        return true
    }
    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != null){
            searchData(p0)
        }
        return true
    }
    private fun searchData(query: String) {
        val searchQuery ="%$query"
        toDoViewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }

}