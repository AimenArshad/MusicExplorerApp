package com.example.musicexplorer.view

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.example.musicexplorer.R
import com.example.musicexplorer.databinding.LayoutBottomSheetBlackFridayDiscountBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplay
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplayCallbacks
import com.google.firebase.inappmessaging.model.CardMessage
import com.google.firebase.inappmessaging.model.InAppMessage

//class BlackFridayBottomSheet(private val activity: MainActivity) : FirebaseInAppMessagingDisplay {
//    val binding = LayoutBottomSheetBlackFridayDiscountBinding.inflate(LayoutInflater.from(activity))
//    val dialog = BottomSheetDialog(activity)
//
//
//    override fun displayMessage(
//        inAppMessage: InAppMessage,
//        callbacks: FirebaseInAppMessagingDisplayCallbacks
//    ) {
//
//        dialog.setContentView(binding.root)
//
//        if(inAppMessage is CardMessage){
//            binding.offer.text = inAppMessage.title.text ?: "Default title"
//            binding.discountDesc.text = inAppMessage.body?.text ?: "Default message"
//            val imageUrl = inAppMessage.portraitImageData?.imageUrl
//
//            if (imageUrl != null) {
//                Glide.with(activity).load(imageUrl).into(binding.blackFridayBG)
//            }else {
//                binding.blackFridayBG.setImageResource(R.drawable.bg_black_friday)
//            }
//
//
//
//            binding.btnSubscribe.text = inAppMessage.primaryAction.button?.text?.text ?: "Default button"
//            binding.DismissText.text = inAppMessage.secondaryAction?.button?.text?.text ?: "Default button2"
//            val sku_id = inAppMessage.data?.get("sku_id")
//            Log.d("FIAM", "SKU Id: $sku_id")
//
//        }
//        binding.btnSubscribe.setOnClickListener {
////                val url = inAppMessage.primaryAction.actionUrl
////                if (!url.isNullOrEmpty()) {
////
////                }
//            dialog.dismiss()
//        }
//        if(activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
//            dialog.show()
//        }
//    }
//}
class BlackFridayBottomSheet(private val activity: MainActivity) : FirebaseInAppMessagingDisplay {

    private var pendingMessage: InAppMessage? = null
    private val dialog by lazy { BottomSheetDialog(activity) }
    private val binding by lazy { LayoutBottomSheetBlackFridayDiscountBinding.inflate(LayoutInflater.from(activity)) }

    override fun displayMessage(
        inAppMessage: InAppMessage,
        callbacks: FirebaseInAppMessagingDisplayCallbacks
    ) {
        pendingMessage = inAppMessage
    }

    fun showLastMessageIfAny() {
        Log.d("fiam", "showLastMessageIfAny")
        pendingMessage?.let {
            showMessage(it)
            pendingMessage = null
        }
    }

    private fun showMessage(inAppMessage: InAppMessage) {
        dialog.setContentView(binding.root)

        if (inAppMessage is CardMessage) {
            binding.offer.text = inAppMessage.title.text ?: "Default title"
            binding.discountDesc.text = inAppMessage.body?.text ?: "Default message"

            val imageUrl = inAppMessage.portraitImageData?.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(activity).load(imageUrl).into(binding.blackFridayBG)
            } else {
                binding.blackFridayBG.setImageResource(R.drawable.bg_black_friday)
            }

            binding.btnSubscribe.text = inAppMessage.primaryAction.button?.text?.text ?: "Default button"
            binding.DismissText.text = inAppMessage.secondaryAction?.button?.text?.text ?: "Default button2"

            val sku_id = inAppMessage.data?.get("sku_id")
            Log.d("FIAM", "SKU Id: $sku_id")
        }

        binding.btnSubscribe.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        pendingMessage = null
    }
}
