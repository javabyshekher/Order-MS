package in.ashokit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDto {

    private Integer orderId;
    private String orderTrackingNum;
    private Integer totalQuantity;
    private Double totalPrice;
    private String orderStatus;
    private LocalDateTime deliveryDate;
    private String deliveryOtp;
    private String paymentStatus;
    private String razorpayOrderId;
    private String razorpayPaymentId;
}
