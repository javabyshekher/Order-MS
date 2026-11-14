package in.ashokit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ORDER_ITEMS")
@Getter
@Setter
public class OrderItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;

    private String imageUrl;

    private Double unitPrice;

    private Integer quantity;

    private Integer productId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}
