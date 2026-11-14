package in.ashokit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="ORDERS")
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    private String orderTrackingNum;

    private Integer totalQuantity;

    private Double totalPrice;

    private String orderStatus;

    private String deliveryOtp;

    private LocalDateTime deliveryDate;

    private String paymentStatus;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "shipping_addr_id")
    private ShippingAddressEntity shippingAddress;

}
