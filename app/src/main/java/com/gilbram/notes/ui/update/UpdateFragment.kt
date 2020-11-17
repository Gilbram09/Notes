package com.gilbram.notes.ui.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gilbram.notes.R
import com.gilbram.notes.databinding.FragmentListBinding
import com.gilbram.notes.databinding.FragmentUpdateBinding
import com.gilbram.notes.model.ToDoData
import com.gilbram.notes.viewmodel.SharedViewModel
import com.gilbram.notes.viewmodel.ToDoViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_update.*

class UpdateFragment : Fragment() {

    private val args: UpdateFragmentArgs by navArgs<UpdateFragmentArgs>()
    private val mSharedViewLocation: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBinding.inflate(inflater,container,false)
        binding.args = args
        binding.spinnerPrioritiesUpdate.onItemSelectedListener = mSharedViewLocation.listener
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemove()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun updateItem() {
        val mTitle = edt_title_update.text.toString()
        val mPriority = spinner_priorities_update.selectedItem.toString()
        val mDescription = edt_description_update.text.toString()

        val validation = mSharedViewLocation.verifyDataFromUser(mTitle, mDescription)
        if (validation) {
            val newData = ToDoData(
                    args.currentitem.id,
                    mTitle,
                    mSharedViewLocation.parsePriority(mPriority),
                    mDescription
            )
            mToDoViewModel.updateData(newData)
            Toast.makeText(requireContext(), "data berhasil", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
    }
    private fun confirmItemRemove() {
        AlertDialog.Builder(requireContext())
                .setTitle("Delete ${args.currentitem.title}")
                .setMessage("Yakin Mau Hapus ${args.currentitem.title}")
                .setPositiveButton("Ya"){_,_->
                    mToDoViewModel.deleteData(args.currentitem)
                    Toast.makeText(requireContext(),"berhasil hapus", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                }
                .setNegativeButton("Tidak"){_,_->}
                .create()
                .show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}