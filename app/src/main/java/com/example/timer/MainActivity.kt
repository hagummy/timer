package com.example.timer

import android.graphics.text.TextRunShaper
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val reTextView : TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }
    private val rereTextView : TextView by lazy {
       findViewById(R.id.remainSecondsTextView)
    }
    private val seekBar : SeekBar by lazy {
        findViewById(R.id.seekBar)
    }
    private var CountDownTimer : CountDownTimer ?=null
    private val soundPool= SoundPool.Builder().build()

    private var tickSoundId: Int?=null
    private var bellSound: Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initSound()
    }

    private fun initView(){
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2) {
                    updateTime(p1*60*1000L)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                stopCount()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0 ?: return

                if(p0.progress ==0){
                    stopCount()
                }
                else {
                    startCount()
                }
            }
        })
    }

    private fun initSound(){
        tickSoundId=soundPool.load(this,R.raw.timer_ticking,1)
        bellSound=soundPool.load(this,R.raw.timer_bell,1)

    }

    private fun updateTime(time : Long){
        val remain = time/1000
        reTextView.text="%02d".format(remain/60)
        rereTextView.text="%02d".format(remain%60)

    }

    private fun stopCount(){
        CountDownTimer?.cancel()
        CountDownTimer=null
        soundPool.autoPause()
    }

    private fun startCount(){
        CountDownTimer=createCountTimer(seekBar.progress*60*1000L)
        CountDownTimer?.start()

        tickSoundId?.let { soundId->
            soundPool.play(soundId,1F,1F,0,-1,1F)
        }
    }

    private fun createCountTimer(l:Long)=object : CountDownTimer(l,1000L) { //추상클라스
        override fun onTick(p0: Long) {
            updateTime(p0)
            updateSeekBar(p0)
        }

        override fun onFinish() {
            completeCountDown()
        }
    }

    private fun updateSeekBar(p0 : Long) {
        seekBar.progress=(p0/1000/60).toInt()
    }

    private fun completeCountDown(){
        updateTime(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSound?.let{soundId->
            soundPool.play(soundId,1F,1F,0,-1,1F)
        }
    }

    override fun onResume(){
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause(){
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy(){
        super.onDestroy()
        soundPool.release()
    }
}