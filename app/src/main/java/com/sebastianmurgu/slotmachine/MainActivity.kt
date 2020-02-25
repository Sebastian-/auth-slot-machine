package com.sebastianmurgu.slotmachine

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.leaderboard_entry.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var leaderboardDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        leaderboardDB = FirebaseDatabase.getInstance().reference.child("leaderboard")

        leaderboard.layoutManager = LinearLayoutManager(this)

        val user = FirebaseAuth.getInstance().currentUser

        val entries = listOf(
            LeaderboardEntry("user@test.com", 12),
            LeaderboardEntry("Lucas", 25),
            LeaderboardEntry("Fausto", 52)
        )

        entries.forEach {
            val key = leaderboardDB.push().key
            key ?: return

            leaderboardDB.child(key).setValue(it)
        }

        leaderboard.adapter = LeaderboardAdapter(entries, this)

        play_button.setOnClickListener {
           if (user != null) {
               startActivity(Intent(this, SlotMachineActivity::class.java))
           } else {
               startActivity(Intent(this, AuthSelectionActivity::class.java))
           }
        }

//        var database = FirebaseDatabase.getInstance().reference
//        database.child("messages").setValue("Hello World")
    }

    private class LeaderboardAdapter(val entries: List<LeaderboardEntry>, val context: Context): RecyclerView.Adapter<LeaderboardViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
            return LeaderboardViewHolder(LayoutInflater.from(context).inflate(R.layout.leaderboard_entry, parent, false))
        }

        override fun getItemCount(): Int {
            return entries.count()
        }

        override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
            val entry = entries[position]

            holder.itemView.player_name.text = entry.name
            holder.itemView.player_score.text = entry.score.toString()
        }
    }

    private class LeaderboardViewHolder(view: View): RecyclerView.ViewHolder(view)
}
