package com.raywenderlich.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.raywenderlich.timefighter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private var score = 0

    private var gameStarted = false
    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long =60000
    private var countDownInterval: Long =1000
    private var timeLeft = 60


    private lateinit var binding: com.raywenderlich.timefighter.databinding.ActivityMainBinding

    companion object{
        private const val SCORE_KEY = "SCORE_KEY"
        private const val  TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Log.d(TAG, "onCreate called. Score is: $score")

        binding.tapMeButton.setOnClickListener { v ->
            val bounceAnimation = AnimationUtils.loadAnimation(this,R.anim.bounce)
            v.startAnimation(bounceAnimation)
            incrementScore() }

        savedInstanceState?.let{
            score = it.getInt(SCORE_KEY)
            timeLeft = it.getInt(TIME_LEFT_KEY)
            restoreGame()
        }?: resetGame()

        //old style with if{} else {}
//        if(savedInstanceState!=null){
//            score = savedInstanceState.getInt(SCORE_KEY)
//            timeLeft= savedInstanceState.getInt(TIME_LEFT_KEY)
//            restoreGame()
//        } else{
//            resetGame()
//        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY,score)
        outState.putInt(TIME_LEFT_KEY,timeLeft)
        countDownTimer.cancel()

        Log.d(TAG,"onSaveInstanceState: Saving Score: $score & " +
                "Time Left: $timeLeft")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG,"onDestroy called.")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.about_item){
            showInfo()
        }
        return true
    }

    // increment score logic
    private fun incrementScore(){
        if(!gameStarted){
            startGame()
        }
        score++

        binding.gameScoreTextView.text = getString(R.string.your_score,score)
    }

    private fun startGame(){
        countDownTimer.start()
        gameStarted = true
    }

    //reset game logic
    private fun resetGame(){
        score =0

        binding.gameScoreTextView.text = getString(R.string.your_score,score)
        binding.timeLeftTextView.text = getString(R.string.time_left,60)

        countDownTimer = object : CountDownTimer(initialCountDown,countDownInterval){

            override fun onTick(millisUntilFinished: Long){
                timeLeft = millisUntilFinished.toInt()/1000

                binding.timeLeftTextView.text = getString(R.string.time_left, timeLeft)
            }

            override fun onFinish(){
                endGame()
            }
        }

        gameStarted = false
    }

    private fun restoreGame(){

        binding.gameScoreTextView.text = getString(R.string.your_score,score)
        binding.timeLeftTextView.text = getString(R.string.time_left,timeLeft)

        countDownTimer = object: CountDownTimer((timeLeft*1000).toLong(),countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt()/1000
                binding.timeLeftTextView.text = getString(R.string.time_left,timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        countDownTimer.start()
        gameStarted = true
    }

    //end game logic
    private fun endGame(){
        Toast.makeText(this,
                getString(R.string.game_over_message,score),
                Toast.LENGTH_LONG).show()

        resetGame()
    }

    //show AlertDialog about a creator
    private fun showInfo(){
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)

        //Alert dialog
        AlertDialog.Builder(this).apply {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
        }.create().show()

        //Toast
        Toast.makeText(this,dialogMessage,Toast.LENGTH_SHORT).show()
    }
}