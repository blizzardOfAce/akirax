package com.example.akirax

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cashfree.pg.api.CFPaymentGatewayService
import com.cashfree.pg.core.api.CFSession
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.exception.CFException
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutPayment
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutTheme
import com.example.akirax.data.api.CashfreeApiService
import com.example.akirax.data.sources.RetrofitInstance
import com.example.akirax.domain.model.BookingDetails
import com.example.akirax.domain.model.PaymentStatusModel
import com.example.akirax.domain.model.Ticket
import com.example.akirax.presentation.viewmodel.PaymentViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentVerificationActivity : AppCompatActivity(), CFCheckoutResponseCallback {
    private val paymentViewModel: PaymentViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orderID = intent.getStringExtra("ORDER_ID")
        val paymentSessionID = intent.getStringExtra("PAYMENT_SESSION_ID")

        try {
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this)
        } catch (e: CFException) {
            e.printStackTrace()
            finish()
        }

        if (!orderID.isNullOrEmpty() && !paymentSessionID.isNullOrEmpty()) {
            initiatePayment(orderID, paymentSessionID)
        } else {
            Log.e("PaymentVerification", "Missing orderID or paymentSessionID")
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun initiatePayment(orderID: String, paymentSessionID: String) {
        try {
            val cfSession = CFSession.CFSessionBuilder()
                .setEnvironment(CFSession.Environment.SANDBOX)
                .setPaymentSessionID(paymentSessionID)
                .setOrderId(orderID)
                .build()

            val cfTheme = CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
                .setNavigationBarBackgroundColor("#6A3FD3")
                .setNavigationBarTextColor("#ffffff")
                .build()

            val cfWebCheckoutPayment = CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
                .setSession(cfSession)
                .setCFWebCheckoutUITheme(cfTheme)
                .build()

            CFPaymentGatewayService.getInstance().doPayment(this, cfWebCheckoutPayment)
        } catch (e: CFException) {
            Log.e("PaymentVerification", "Error during payment initiation: ${e.message}")
            e.printStackTrace()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onPaymentVerify(orderID: String?) {
        orderID?.let {
            Log.d("PaymentVerification", "Payment verified for OrderID: $it")
            paymentViewModel.verifyPayment(it)
            finish()
        } ?: run {
            Log.e("PaymentVerification", "OrderID is null during verification")
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED) // Inform the payment screen of cancellation
        super.onBackPressed() // Default back navigation behavior
    }

    override fun onPaymentFailure(errorResponse: CFErrorResponse?, message: String?) {
        Log.e("PaymentVerification", "Payment failed: ${errorResponse?.message ?: message}")
        Toast.makeText(this, "Payment Error: ${errorResponse?.message ?: message}", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}

//class PaymentVerificationActivity : AppCompatActivity(), CFCheckoutResponseCallback {
//    private var orderId: String? = null
//    private var paymentSessionId: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // No need to set content view - we'll directly handle payment flow
//
//        // Retrieve intent extras
//        orderId = intent.getStringExtra("ORDER_ID")
//        paymentSessionId = intent.getStringExtra("PAYMENT_SESSION_ID")
//
//        Log.d("PaymentVerification", "Received ORDER_ID: $orderId")
//        Log.d("PaymentVerification", "Received PAYMENT_SESSION_ID: $paymentSessionId")
//
//        if (orderId.isNullOrEmpty() || paymentSessionId.isNullOrEmpty()) {
//            setResultAndFinish("FAILED", "Payment details missing")
//            return
//        }
//
//        try {
//            CFPaymentGatewayService.getInstance().setCheckoutCallback(this)
//
//            // Launch payment gateway immediately
//            initiatePayment(orderId!!, paymentSessionId!!)
//        } catch (e: CFException) {
//            Log.e("PaymentVerification", "CFException: ${e.message}")
//            setResultAndFinish("FAILED", "Payment service error")
//        }
//    }
//
//    // Launch payment gateway using Cashfree's SDK
//    private fun initiatePayment(orderId: String, paymentSessionId: String) {
//        try {
//            val cfSession = CFSession.CFSessionBuilder()
//                .setEnvironment(CFSession.Environment.SANDBOX)
//                .setPaymentSessionID(paymentSessionId)
//                .setOrderId(orderId)
//                .build()
//
//            val cfTheme = CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
//                .setNavigationBarBackgroundColor("#6A3FD3")
//                .setNavigationBarTextColor("#ffffff")
//                .build()
//
//            val cfWebCheckoutPayment = CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
//                .setSession(cfSession)
//                .setCFWebCheckoutUITheme(cfTheme)
//                .build()
//
//            CFPaymentGatewayService.getInstance().doPayment(this, cfWebCheckoutPayment)
//        } catch (e: CFException) {
//            Log.e("PaymentVerification", "Error during payment initiation: ${e.message}")
//            setResultAndFinish("FAILED", "Error initiating payment")
//        }
//    }
//
//    override fun onPaymentFailure(errorResponse: CFErrorResponse?, message: String?) {
//        Log.e("PaymentVerification", "Payment failed: ${errorResponse?.message ?: message}")
//        setResultAndFinish("FAILED", errorResponse?.message ?: message ?: "Payment Failed")
//    }
//
//    override fun onPaymentVerify(orderID: String?) {
//        Log.d("PaymentVerification", "onPaymentVerify triggered with orderID: $orderID")
//        orderID?.let {
//            // Payment was successful
//            setResultAndFinish("VERIFIED", "Payment Successful", it)
//        } ?: run {
//            setResultAndFinish("FAILED", "Verification Failed")
//        }
//    }
//
//    override fun onBackPressed() {
//        setResultAndFinish("CANCELLED", "Payment Cancelled")
//        super.onBackPressed()
//    }
//
//    private fun setResultAndFinish(status: String, message: String, orderId: String? = null) {
//        val intent = Intent().apply {
//            putExtra("PAYMENT_STATUS", status)
//            putExtra("ERROR_MESSAGE", message)
//            orderId?.let { putExtra("ORDER_ID", it) }
//        }
//        setResult(when (status) {
//            "VERIFIED" -> Activity.RESULT_OK
//            "CANCELLED" -> Activity.RESULT_CANCELED
//            else -> Activity.RESULT_CANCELED
//        }, intent)
//        finish()
//    }
//}


//WorkHareder
//class PaymentVerificationActivity : AppCompatActivity(), CFCheckoutResponseCallback {
//    private val paymentViewModel: PaymentViewModel by inject()
//
//    companion object {
//        const val PAYMENT_CANCELLED = 101
//        const val PAYMENT_FAILED = 102
//        const val PAYMENT_REQUEST_CODE = 1001
//
//    }
//
//    //Worrked
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////
////        val orderID = intent.getStringExtra("ORDER_ID")
////        val paymentSessionID = intent.getStringExtra("PAYMENT_SESSION_ID")
////
////        // Debugging Logs: Check if intent extras exist
////        Log.d("PaymentVerification", "Received ORDER_ID: $orderID")
////        Log.d("PaymentVerification", "Received PAYMENT_SESSION_ID: $paymentSessionID")
////
////
////        try {
////            CFPaymentGatewayService.getInstance().setCheckoutCallback(this)
////        } catch (e: CFException) {
////            Log.e("PaymentVerification", "CFException: ${e.message}")
////            return // Prevents premature cancellation
////        }
////
////        if (orderID.isNullOrEmpty() || paymentSessionID.isNullOrEmpty()) {
////            Log.e("PaymentVerification", "Missing orderID or paymentSessionID")
////            Toast.makeText(this, "Payment details missing", Toast.LENGTH_SHORT).show()
////            return
////        }
////
////        initiatePayment(orderID, paymentSessionID)
////    }
//    private lateinit var textViewMessage: TextView
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_payment_verification)
//
//        textViewMessage = findViewById(R.id.textViewMessage) // Add this TextView in XML
//
//        val orderId = intent.getStringExtra("ORDER_ID")
//        val paymentSessionId = intent.getStringExtra("PAYMENT_SESSION_ID")
//
//        try {
//            CFPaymentGatewayService.getInstance().setCheckoutCallback(this)
//        } catch (e: CFException) {
//            Log.e("PaymentVerification", "CFException: ${e.message}")
//            return // Prevents premature cancellation
//        }
//
//        if (orderId.isNullOrEmpty() || paymentSessionId.isNullOrEmpty()) {
//           // showMessageAndExit("Payment details missing.")
//            processPayment(orderId.toString(), paymentSessionId.toString())
//            return
//        }
//
//        initiatePayment(orderId, paymentSessionId)
//    }
//
//    private fun processPayment(orderId: String, paymentSessionId: String) {
//        // Simulating a failed/cancelled payment for testing
//        val paymentStatus = intent.getStringExtra("PAYMENT_STATUS") ?: "CANCELLED"
//
//        if (paymentStatus == "CANCELLED") {
//            showMessageAndExit("Payment Cancelled. Redirecting...")
//        } else if (paymentStatus == "FAILED") {
//            showMessageAndExit("Payment Failed. Redirecting...")
//        } else {
//            setResult(Activity.RESULT_OK, Intent().apply {
//                putExtra("PAYMENT_STATUS", "VERIFIED")
//                putExtra("ORDER_ID", orderId)
//            })
//            finish() // Close the activity on success
//        }
//    }
//
//
//    private fun showMessageAndExit(message: String) {
//        textViewMessage.text = message // Show the message
//
//        // Close activity after 5 seconds
//        Handler(Looper.getMainLooper()).postDelayed({
//            finish()
//        }, 5000)
//    }
//
////    override fun onBackPressed() {
////        setPaymentCancelled()
////        super.onBackPressed()
////    }
//override fun onBackPressed() {
//    super.onBackPressed()
//    AlertDialog.Builder(this).apply {
//        setTitle("Cancel Payment")
//        setMessage("Are you sure you want to cancel the payment?")
//        setPositiveButton("Yes") { _, _ ->
//            setPaymentCancelled()
//        }
//        setNegativeButton("No", null)
//        show()
//    }
//}
//
//
//    private fun setPaymentCancelled() {
//        val intent = Intent().apply {
//            putExtra("PAYMENT_STATUS", "CANCELLED")
//        }
//        setResult(Activity.RESULT_CANCELED, intent)
//        finish()
//    }
//
//    private fun initiatePayment(orderID: String, paymentSessionID: String) {
//        try {
//            val cfSession = CFSession.CFSessionBuilder()
//                .setEnvironment(CFSession.Environment.SANDBOX)
//                .setPaymentSessionID(paymentSessionID)
//                .setOrderId(orderID)
//                .build()
//
//            val cfTheme = CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
//                .setNavigationBarBackgroundColor("#6A3FD3")
//                .setNavigationBarTextColor("#ffffff")
//                .build()
//
//            val cfWebCheckoutPayment = CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
//                .setSession(cfSession)
//                .setCFWebCheckoutUITheme(cfTheme)
//                .build()
//
//            CFPaymentGatewayService.getInstance().doPayment(this, cfWebCheckoutPayment)
//        } catch (e: CFException) {
//            Log.e("PaymentVerification", "Error during payment initiation: ${e.message}")
//            Toast.makeText(this, "Payment initiation failed", Toast.LENGTH_SHORT).show()
//            finish()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PAYMENT_REQUEST_CODE) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    val paymentStatus = data?.getStringExtra("PAYMENT_STATUS") ?: "Unknown"
//                    Log.d("PaymentVerification", "Payment Status: $paymentStatus")
//                    Toast.makeText(this, "Payment $paymentStatus", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//                Activity.RESULT_CANCELED -> {
//                    Log.d("PaymentVerification", "Payment Cancelled")
//                    Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
//                    finish() // ✅ Closes the blank screen and goes back to the previous screen
//                }
//                else -> {
//                    Log.e("PaymentVerification", "Unknown payment response")
//                    Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//            }
//        }
//    }
//
//
//    override fun onPaymentFailure(errorResponse: CFErrorResponse?, message: String?) {
//        Log.e("PaymentVerification", "Payment failed: ${errorResponse?.message ?: message}")
//        Log.e("onPaymentFailure ", "is Running")
//        val intent = Intent().apply {
//            putExtra("PAYMENT_STATUS", "FAILED")
//            putExtra("ERROR_MESSAGE", errorResponse?.message ?: message)
//        }
//        setResult(Activity.RESULT_CANCELED, intent)
//        finish()
//    }
//
//    override fun onPaymentVerify(orderID: String?) {
//        Log.d("onPaymentVerify", "verifyPayment triggered")
//        orderID?.let {
//            val intent = Intent().apply {
//                putExtra("PAYMENT_STATUS", "VERIFIED")
//                putExtra("ORDER_ID", it)
//            }
//            setResult(Activity.RESULT_OK, intent)
//            finish()
//        } ?: run {
//            setPaymentCancelled()
//        }
//    }
//}


//Working V2
//class PaymentVerificationActivity : AppCompatActivity(), CFCheckoutResponseCallback {
//    private val paymentViewModel: PaymentViewModel by inject()
//
//    companion object {
//        const val PAYMENT_CANCELLED = 101
//        const val PAYMENT_FAILED = 102
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val orderID = intent.getStringExtra("ORDER_ID")
//        val paymentSessionID = intent.getStringExtra("PAYMENT_SESSION_ID")
//
//        try {
//            CFPaymentGatewayService.getInstance().setCheckoutCallback(this)
//        } catch (e: CFException) {
//            Log.e("PaymentVerification", "CFException: ${e.message}")
//            setPaymentCancelled()
//        }
//
//        if (!orderID.isNullOrEmpty() && !paymentSessionID.isNullOrEmpty()) {
//            initiatePayment(orderID, paymentSessionID)
//        } else {
//            setPaymentCancelled()
//        }
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        setPaymentCancelled()
//    }
//
//    private fun setPaymentCancelled() {
//        val intent = Intent().apply {
//            putExtra("PAYMENT_STATUS", "CANCELLED")
//        }
//        setResult(Activity.RESULT_CANCELED, intent)
//        finish()
//    }
//
//    //Original and worked
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////
////        val orderID = intent.getStringExtra("ORDER_ID")
////        val paymentSessionID = intent.getStringExtra("PAYMENT_SESSION_ID")
////
////        try {
////            CFPaymentGatewayService.getInstance().setCheckoutCallback(this)
////        } catch (e: CFException) {
////            e.printStackTrace()
////            finish()
////        }
////
////        if (!orderID.isNullOrEmpty() && !paymentSessionID.isNullOrEmpty()) {
////            initiatePayment(orderID, paymentSessionID)
////        } else {
////            Log.e("PaymentVerification", "Missing orderID or paymentSessionID")
////            setResult(Activity.RESULT_CANCELED)
////            finish()
////        }
////    }
//
//    private fun initiatePayment(orderID: String, paymentSessionID: String) {
//        try {
//            val cfSession = CFSession.CFSessionBuilder()
//                .setEnvironment(CFSession.Environment.SANDBOX)
//                .setPaymentSessionID(paymentSessionID)
//                .setOrderId(orderID)
//                .build()
//
//            val cfTheme = CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
//                .setNavigationBarBackgroundColor("#6A3FD3")
//                .setNavigationBarTextColor("#ffffff")
//                .build()
//
//            val cfWebCheckoutPayment = CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
//                .setSession(cfSession)
//                .setCFWebCheckoutUITheme(cfTheme)
//                .build()
//
//            CFPaymentGatewayService.getInstance().doPayment(this, cfWebCheckoutPayment)
//        } catch (e: CFException) {
//            Log.e("PaymentVerification", "Error during payment initiation: ${e.message}")
//            e.printStackTrace()
//            setResult(Activity.RESULT_CANCELED)
//            finish()
//        }
//    }
//
//    override fun onPaymentFailure(errorResponse: CFErrorResponse?, message: String?) {
//        Log.e("PaymentVerification", "Payment failed: ${errorResponse?.message ?: message}")
//        val intent = Intent().apply {
//            putExtra("PAYMENT_STATUS", "FAILED")
//            putExtra("ERROR_MESSAGE", errorResponse?.message ?: message)
//        }
//        setResult(Activity.RESULT_CANCELED, intent)
//        finish()
//    }
//
//    override fun onPaymentVerify(orderID: String?) {
//        orderID?.let {
//            val intent = Intent().apply {
//                putExtra("PAYMENT_STATUS", "VERIFIED")
//                putExtra("ORDER_ID", it)
//            }
//            setResult(Activity.RESULT_OK, intent)
//            finish()
//        } ?: run {
//            setPaymentCancelled()
//        }
//    }
//
//    //Worked V2
////    override fun onPaymentVerify(orderID: String?) {
////        orderID?.let {
////            paymentViewModel.verifyPayment(it)
////            val resultIntent = Intent().apply {
////                putExtra("PAYMENT_STATUS", "VERIFIED")
////                putExtra("ORDER_ID", it)
////            }
////            setResult(Activity.RESULT_OK, resultIntent)
////            finish()
////        } ?: run {
////            setResult(Activity.RESULT_CANCELED)
////            finish()
////        }
////    }
//
////    override fun onPaymentVerify(orderID: String?) {
////        orderID?.let {
////            Log.d("PaymentVerification", "Payment verified for OrderID: $it")
////            paymentViewModel.verifyPayment(it)
////            finish()
////        } ?: run {
////            Log.e("PaymentVerification", "OrderID is null during verification")
////            setResult(Activity.RESULT_CANCELED)
////            finish()
////        }
////    }
//
//    //Worked V2
////    @Deprecated("Deprecated in Java")
////    override fun onBackPressed() {
////        super.onBackPressed()
////        setResult(PAYMENT_CANCELLED)
////        finish()
////    }
//
////    override fun onBackPressed() {
////        setResult(Activity.RESULT_CANCELED) // Inform the payment screen of cancellation
////        super.onBackPressed() // Default back navigation behavior
////    }
//
////    override fun onPaymentFailure(errorResponse: CFErrorResponse?, message: String?) {
////        Log.e("PaymentVerification", "Payment failed: ${errorResponse?.message ?: message}")
////        Toast.makeText(this, "Payment Error: ${errorResponse?.message ?: message}", Toast.LENGTH_SHORT).show()
////        setResult(Activity.RESULT_CANCELED)
////        finish()
////    }
//
//
//    //Worked V2
////    override fun onPaymentFailure(errorResponse: CFErrorResponse?, message: String?) {
////        val resultIntent = Intent().apply {
////            putExtra("PAYMENT_STATUS", "FAILED")
////            putExtra("ERROR_MESSAGE", errorResponse?.message ?: message)
////        }
////        setResult(PAYMENT_FAILED, resultIntent)
////        finish()
////    }
//}


//class PaymentVerificationActivity : AppCompatActivity(), CFCheckoutResponseCallback {
//    private val paymentViewModel: PaymentViewModel by viewModel()
//    private val cashfreeApiService: CashfreeApiService = RetrofitInstance.cashfreeApiService
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//
//        val orderID = intent.getStringExtra("ORDER_ID")
//        val paymentSessionID = intent.getStringExtra("PAYMENT_SESSION_ID")
//        val bookingDetails = paymentViewModel.bookingDetails
//        val userEmail = paymentViewModel.userEmail
//
//        if (bookingDetails == null || userEmail.isNullOrEmpty()) {
//            Log.e("PaymentVerification", "Missing booking details or user email!")
//            finish()
//            return
//        }
//
//        Log.d("PaymentVerification", "Booking details: $bookingDetails, UserEmail: $userEmail")
//
//
//        try {
//            CFPaymentGatewayService.getInstance().setCheckoutCallback(this)
//        } catch (e: CFException) {
//            e.printStackTrace()
//            finish()
//        }
//
//        if (!orderID.isNullOrEmpty() && !paymentSessionID.isNullOrEmpty()) {
//            initiatePayment(orderID, paymentSessionID)
//        } else {
//            Log.e("PaymentVerification", "Missing orderID or paymentSessionID")
//            finish()
//        }
//    }
//
//    private fun initiatePayment(orderID: String, paymentSessionID: String) {
//        try {
//            val cfSession = CFSession.CFSessionBuilder()
//                .setEnvironment(CFSession.Environment.SANDBOX)
//                .setPaymentSessionID(paymentSessionID)
//                .setOrderId(orderID)
//                .build()
//
//            val cfTheme = CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
//                .setNavigationBarBackgroundColor("#6A3FD3")
//                .setNavigationBarTextColor("#ffffff")
//                .build()
//
//            val cfWebCheckoutPayment = CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
//                .setSession(cfSession)
//                .setCFWebCheckoutUITheme(cfTheme)
//                .build()
//
//            CFPaymentGatewayService.getInstance().doPayment(this, cfWebCheckoutPayment)
//        } catch (e: CFException) {
//            Log.e("PaymentVerification", "Error during payment initiation: ${e.message}")
//            e.printStackTrace()
//            finish()
//        }
//    }
//
//    override fun onPaymentVerify(orderID: String?) {
//        orderID?.let {
//            Log.d("PaymentVerification", "Payment verified for OrderID: $it")
//            verifyPayment(it)
//        } ?: run {
//            Log.e("PaymentVerification", "OrderID is null during verification")
//            finish()
//        }
//    }
//
//    override fun onPaymentFailure(errorResponse: CFErrorResponse?, message: String?) {
//        Log.e("PaymentVerification", "Payment failed: ${errorResponse?.message ?: message}")
//        Toast.makeText(this, "Payment Error: ${errorResponse?.message ?: message}", Toast.LENGTH_SHORT).show()
//        finish()
//    }
//
//    private fun verifyPayment(orderKey: String) {
//        Log.d("PaymentVerification", "Starting verification for OrderKey: $orderKey")
//
//        // Ensure proper initialization of this service
//
//        cashfreeApiService.create(orderKey).enqueue(object : Callback<PaymentStatusModel> {
//            override fun onResponse(call: Call<PaymentStatusModel>, response: Response<PaymentStatusModel>) {
//                response.body()?.let {
//                    if (it.order_status == "PAID") {
//                        Log.d("PaymentVerification", "Payment verified successfully")
//
//                        paymentViewModel.saveTicketsToFirebase()
//                    } else {
//                        Log.e("PaymentVerification", "Payment failed: ${it.order_status}")
//                        Toast.makeText(this@PaymentVerificationActivity, "Payment Failed!", Toast.LENGTH_SHORT).show()
//                    }
//                } ?: run {
//                    Log.e("PaymentVerification", "Response body is null")
//                }
//            }
//
//            override fun onFailure(call: Call<PaymentStatusModel>, t: Throwable) {
//                Log.e("PaymentVerification", "Verification failed: ${t.message}")
//                Toast.makeText(this@PaymentVerificationActivity, "Verification failed", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//
//}

//...................................................................................................
//THIS WAS WORKING:
//class PaymentVerificationActivity : AppCompatActivity(), CFCheckoutResponseCallback {
//    //private val paymentViewModel: PaymentViewModel by viewModel()
//    private val paymentViewModel: PaymentViewModel by inject() // `inject()` is for singletons
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val orderID = intent.getStringExtra("ORDER_ID")
//        val paymentSessionID = intent.getStringExtra("PAYMENT_SESSION_ID")
//
//        try {
//            CFPaymentGatewayService.getInstance().setCheckoutCallback(this)
//        } catch (e: CFException) {
//            e.printStackTrace()
//            finish()
//        }
//
//        if (!orderID.isNullOrEmpty() && !paymentSessionID.isNullOrEmpty()) {
//            initiatePayment(orderID, paymentSessionID)
//        } else {
//            Log.e("PaymentVerification", "Missing orderID or paymentSessionID")
//            finish()
//        }
//    }
//
//    private fun initiatePayment(orderID: String, paymentSessionID: String) {
//        try {
//            val cfSession = CFSession.CFSessionBuilder()
//                .setEnvironment(CFSession.Environment.SANDBOX)
//                .setPaymentSessionID(paymentSessionID)
//                .setOrderId(orderID)
//                .build()
//
//            val cfTheme = CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
//                .setNavigationBarBackgroundColor("#6A3FD3")
//                .setNavigationBarTextColor("#ffffff")
//                .build()
//
//            val cfWebCheckoutPayment = CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
//                .setSession(cfSession)
//                .setCFWebCheckoutUITheme(cfTheme)
//                .build()
//
//            CFPaymentGatewayService.getInstance().doPayment(this, cfWebCheckoutPayment)
//        } catch (e: CFException) {
//            Log.e("PaymentVerification", "Error during payment initiation: ${e.message}")
//            e.printStackTrace()
//            finish()
//        }
//    }
//
//    override fun onBackPressed() {
//        // Cancel payment and navigate back with a result
//        setResult(Activity.RESULT_CANCELED, Intent().apply {
//            putExtra("PAYMENT_STATUS", "Failed")
//        })
//        finish()
//        super.onBackPressed()
//    }
//
//    override fun onPaymentFailure(errorResponse: CFErrorResponse?, message: String?) {
//        Log.e("PaymentVerification", "Payment failed: ${errorResponse?.message ?: message}")
//        setResult(Activity.RESULT_CANCELED, Intent().apply {
//            putExtra("PAYMENT_STATUS", "Failed")
//        })
//        finish()
//    }
//
//    override fun onPaymentVerify(orderID: String?) {
//        orderID?.let {
//            Log.d("PaymentVerification", "Payment verified for OrderID: $it")
//            val intent = Intent().apply {
//                putExtra("orderID", it)
//            }
//            setResult(RESULT_OK, intent)
//
//        }
//        finish()
//    }
//
////    override fun onPaymentFailure(errorResponse: CFErrorResponse?, message: String?) {
////        Log.e("PaymentVerification", "Payment failed: ${errorResponse?.message ?: message}")
////        Toast.makeText(this, "Payment Error: ${errorResponse?.message ?: message}", Toast.LENGTH_SHORT).show()
////        setResult(Activity.RESULT_CANCELED)
////        finish()
////    }
//
////        override fun onPaymentVerify(orderID: String?) {
////        orderID?.let {
////            Log.d("PaymentVerification", "Payment verified for OrderID: $it")
////            paymentViewModel.verifyPayment(it)
////            finish()
////        } ?: run {
////            Log.e("PaymentVerification", "OrderID is null during verification")
////            finish()
////        }
////    }
////
////    @Deprecated("Deprecated in Java")
////    override fun onBackPressed() {
////        // Cancel payment on back press
////        super.onBackPressed()
////        setResult(Activity.RESULT_CANCELED)
////        finish()
////    }
//
//
//}

