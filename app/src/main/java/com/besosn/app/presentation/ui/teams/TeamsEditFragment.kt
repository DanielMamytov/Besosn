package com.besosn.app.presentation.ui.teams

import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.databinding.FragmentTeamsEditBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeamsEditFragment : Fragment(R.layout.fragment_teams_edit) {

    private var _binding: FragmentTeamsEditBinding? = null
    private val binding get() = _binding!!
    private var editingTeam: TeamModel? = null
    private var selectedIconUri: String? = null
    private val players = mutableListOf<PlayerModel>()
    private lateinit var playersAdapter: PlayersAdapter

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedIconUri = it.toString()
            binding.imageView2.setImageURI(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTeamsEditBinding.bind(view)

        editingTeam = arguments?.getSerializable("team") as? TeamModel

        val lettersOnly = InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[a-zA-Z ]*"))) source else ""
        }
        binding.etTeamName.filters = arrayOf(lettersOnly)
        binding.etCity.filters = arrayOf(lettersOnly)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener { saveTeam() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }

        playersAdapter = PlayersAdapter(players)
        binding.rvPlayers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlayers.adapter = playersAdapter
        binding.btnAddPlayer.setOnClickListener { showAddPlayerDialog() }

        binding.imageView2.setOnClickListener {
            pickImage.launch("image/*")
        }

        editingTeam?.let { team ->
            binding.etTeamName.setText(team.name)
            binding.etCity.setText(team.city)
            binding.etFoundedYear.setText(team.foundedYear.toString())
            binding.etNotes.setText(team.notes)
            selectedIconUri = team.iconUri
            if (team.iconUri != null) {
                binding.imageView2.setImageURI(Uri.parse(team.iconUri))
            } else if (team.iconRes != 0) {
                binding.imageView2.setImageResource(team.iconRes)
            }
            players.addAll(team.players)
            playersAdapter.notifyDataSetChanged()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun saveTeam() {
        val name = binding.etTeamName.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val foundedStr = binding.etFoundedYear.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        val founded = foundedStr.toIntOrNull()

        if (name.isEmpty() || city.isEmpty() || foundedStr.isEmpty() ||
            notes.isEmpty() || players.isEmpty() || founded == null) {
            Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val iconUri = selectedIconUri ?: editingTeam?.iconUri
        val currentPlayers = players.toList()

        val existing = editingTeam
        if (existing != null) {
            val updatedTeam = existing.copy(
                name = name,
                city = city,
                foundedYear = founded,
                notes = notes,
                players = currentPlayers,
                iconUri = iconUri,
                iconRes = if (iconUri != null) 0 else existing.iconRes
            )
            viewLifecycleOwner.lifecycleScope.launch {
                val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "app_db")
                    .fallbackToDestructiveMigration()
                    .build()
                withContext(Dispatchers.IO) {
                    db.teamDao().updateTeam(updatedTeam.toEntity())
                    db.playerDao().deletePlayersByTeam(updatedTeam.id)
                    val playerEntities = currentPlayers.map { it.toEntity(updatedTeam.id) }
                    if (playerEntities.isNotEmpty()) {
                        db.playerDao().insertPlayers(playerEntities)
                    }
                }
                setFragmentResult("team_updated", bundleOf("team" to updatedTeam))
                setFragmentResult("add_team_result", Bundle())
                findNavController().popBackStack()
            }
        } else {
            val team = TeamModel(
                name = name,
                city = city,
                foundedYear = founded,
                notes = notes,
                players = currentPlayers,
                iconUri = iconUri,
                iconRes = if (iconUri != null) 0 else R.drawable.ic_users
            )

            viewLifecycleOwner.lifecycleScope.launch {
                val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "app_db")
                    .fallbackToDestructiveMigration()
                    .build()
                val teamId = withContext(Dispatchers.IO) { db.teamDao().insertTeam(team.toEntity()).toInt() }
                val playerEntities = currentPlayers.map { it.toEntity(teamId) }
                if (playerEntities.isNotEmpty()) {
                    withContext(Dispatchers.IO) { db.playerDao().insertPlayers(playerEntities) }
                }
                setFragmentResult("add_team_result", Bundle())
                findNavController().popBackStack()
            }
        }
    }

    private fun showAddPlayerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_player, null)
        val etName = dialogView.findViewById<EditText>(R.id.etPlayerName)
        val etNumber = dialogView.findViewById<EditText>(R.id.etNumber)
        val spPosition = dialogView.findViewById<Spinner>(R.id.spPosition)
        val btnAdd = dialogView.findViewById<AppCompatButton>(R.id.btnAddPlayer)
        val btnCancel = dialogView.findViewById<AppCompatButton>(R.id.btnCancel)

        val positions = resources.getStringArray(R.array.player_positions)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, positions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPosition.adapter = spinnerAdapter

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val numberStr = etNumber.text.toString().trim()
            val position = spPosition.selectedItem.toString()

            val number = numberStr.toIntOrNull()
            if (name.isEmpty() || numberStr.isEmpty() || number == null) {
                Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            players.add(PlayerModel(name, position, number))
            playersAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
