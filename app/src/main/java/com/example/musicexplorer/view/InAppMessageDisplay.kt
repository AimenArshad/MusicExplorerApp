import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import com.example.musicexplorer.databinding.InAppMessageCardBinding
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplay
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplayCallbacks
import com.google.firebase.inappmessaging.model.InAppMessage

class InAppMessageDisplay(private val activity: Activity) : FirebaseInAppMessagingDisplay {

    override fun displayMessage(
        inAppMessage: InAppMessage,
        callbacks: FirebaseInAppMessagingDisplayCallbacks
    ) {
        val binding = InAppMessageCardBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity)
        dialog.setContentView(binding.root)

        Log.d("FIAM", "Custom InAppMessageDisplay invoked with message: ${inAppMessage.body?.text}")

        binding.messageCardTitle.text = inAppMessage.title?.text ?: "Default title"
        binding.messageCardBody.text = inAppMessage.body?.text ?: "Default message"
        binding.msgSheetBtn.text = "OK"

        val discount = inAppMessage.data?.get("discount")
        val price = inAppMessage.data?.get("price")

        Log.d("FIAM", "Discount: $discount, Price: $price")

        binding.msgSheetBtn.setOnClickListener {
            val url = inAppMessage.action?.actionUrl
            if (!url.isNullOrEmpty()) {

            }
            dialog.dismiss()
        }
        dialog.show()
    }
}
