package com.sebastianmurgu.slotmachine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_slot_machine.*

class SlotMachineActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private var sessionScore = 0

    private val slotImages: List<Int> = listOf(
        R.drawable.cherries,
        R.drawable.number7,
        R.drawable.dollarsign,
        R.drawable.grapes,
        R.drawable.lemon
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slot_machine)
        database = FirebaseDatabase.getInstance().reference

        play_button.setOnClickListener {
            playSlots()
        }

        leaderboard_button.setOnClickListener {
            saveScore(sessionScore)
            startActivity(Intent(this, MainActivity::class.java))
        }

        logout_button.setOnClickListener {
            saveScore(sessionScore)
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun playSlots() {
        val leftImage = slotImages.random()
        val middleImage = slotImages.random()
        val rightImage = slotImages.random()

        left_slot.setImageResource(leftImage)
        middle_slot.setImageResource(middleImage)
        right_slot.setImageResource(rightImage)

        val points: Int
        if (leftImage == middleImage && middleImage == rightImage) {
            // all matched
            points = 5
        } else if (leftImage == middleImage || leftImage == rightImage || middleImage == rightImage) {
            // 2 matched
            points = 2
        } else {
            // no match
            points = 0
        }

        sessionScore += points

        if (points != 0) {
            status.text = "+$points points! Score so far: $sessionScore"
        } else {
            status.text = "Try again! Score so far: $sessionScore"
        }
    }

    private fun saveScore(score: Int) {
        if (score == 0) return

        val user = FirebaseAuth.getInstance().currentUser
        database.child("scores").child(user!!.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // handle error
            }

            override fun onDataChange(p0: DataSnapshot) {
                val scoreEntry = p0.getValue(LeaderboardEntry::class.java)
                if (scoreEntry == null) {
                    val entry = LeaderboardEntry(user.email.toString(), sessionScore)
                    database.child("scores").child(user!!.uid).setValue(entry)
                } else {
                    val entry = LeaderboardEntry(user.email.toString(), scoreEntry.score + sessionScore)
                    database.child("scores").child(user!!.uid).setValue(entry)
                }
            }
        })
    }
}
