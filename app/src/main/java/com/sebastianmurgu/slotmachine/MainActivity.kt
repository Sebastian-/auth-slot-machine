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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.leaderboard_entry.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var leaderboardEntries: MutableList<LeaderboardEntry> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = FirebaseDatabase.getInstance().reference

        leaderboard.layoutManager = LinearLayoutManager(this)
        val user = FirebaseAuth.getInstance().currentUser

        database.child("scores").orderByChild("score").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                leaderboardEntries = mutableListOf()
                p0.children.reversed().forEach {
                    val entry = it.getValue(LeaderboardEntry::class.java)
                    if (entry != null) {
                        leaderboardEntries.add(entry)
                    }
                }

                update()
            }

        })


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

    private fun update() {
        leaderboard.adapter = LeaderboardAdapter(leaderboardEntries, this)
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

            holder.itemView.player_name.text = entry.username
            holder.itemView.player_score.text = entry.score.toString()
        }
    }

    private class LeaderboardViewHolder(view: View): RecyclerView.ViewHolder(view)
}
