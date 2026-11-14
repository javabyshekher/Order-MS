package in.ashokit.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorPayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private RazorpayClient razorpayClient;

    public String createRazorpayOrder(Double amount) throws Exception {

        this.razorpayClient = new RazorpayClient(keyId, keySecret);

        JSONObject orderReq = new JSONObject();
        orderReq.put("amount", amount * 100);
        orderReq.put("currency", "INR");
        orderReq.put("payment_capture", 1);

        Order order = razorpayClient.Orders.create(orderReq);

        return order.get("id");
    }

    public String refundPayment(String paymentId, Double amount) throws Exception {

        this.razorpayClient = new RazorpayClient(keyId, keySecret);

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("payment_id", paymentId);
        refundRequest.put("amount", amount);

        Refund refund = razorpayClient.Payments.refund(refundRequest);

        return refund.get("id)");
    }
}
