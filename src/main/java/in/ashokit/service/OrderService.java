package in.ashokit.service;

import in.ashokit.dto.*;
import in.ashokit.entity.OrderEntity;
import in.ashokit.entity.OrderItemsEntity;
import in.ashokit.entity.ShippingAddressEntity;
import in.ashokit.entity.UserEntity;
import in.ashokit.mapper.AddressMapper;
import in.ashokit.mapper.OrderItemsMapper;
import in.ashokit.mapper.OrderMapper;
import in.ashokit.repository.AddressRepository;
import in.ashokit.repository.OrderItemsRepository;
import in.ashokit.repository.OrderRepository;
import in.ashokit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemsRepository itemsRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AddressRepository addrRepo;

    @Autowired
    private RazorPayService razorpayService;

    @Autowired
    private ProductService productService;

//    private KafkaTemplate<String, OrderDto> kafkaTemplate;

    public PurchaseOrderResponseDto createOrder(PurchaseOrderRequestDto orderReqDto) throws Exception {

        UserDto userDto = orderReqDto.getUser();
        UserEntity userEntity = userRepo.findByEmail(userDto.getEmail());

        ShippingAddressEntity addressEntity = null;

        ShippingAddressDto addrDto = orderReqDto.getShippingAddress();
        if (addrDto.getAddrId() == null) {
            addressEntity = AddressMapper.dtoToEntity(addrDto);
            addressEntity.setUser(userEntity);
            addrRepo.save(addressEntity);
        } else {
            addressEntity = addrRepo.findById(addrDto.getAddrId()).get();
        }

        OrderDto orderDto = orderReqDto.getOrder();
        String razorpayOrderId = razorpayService.createRazorpayOrder(orderDto.getTotalPrice());

        OrderEntity orderEntity = OrderMapper.toEntity(orderDto);

        orderEntity.setOrderTrackingNum(generateOrderTrackingNumber());
        orderEntity.setOrderStatus("CREATED");
        orderEntity.setPaymentStatus("PENDING");
        orderEntity.setRazorpayOrderId(razorpayOrderId);

        orderEntity.setUser(userEntity);
        orderEntity.setShippingAddress(addressEntity);

        OrderEntity savedOrder = orderRepo.save(orderEntity);

        List<OrderItemsDto> orderItems = orderReqDto.getOrderItems();

        for (OrderItemsDto itemDto : orderItems) {
            Integer productId = itemDto.getProductId();
            ProductDto productById = productService.getProductById(productId);
            OrderItemsEntity itemEntity = OrderItemsMapper.dtoToEntity(itemDto);
            itemEntity.setImageUrl(productById.getImageUrl());
            itemEntity.setOrder(savedOrder);
            itemsRepo.save(itemEntity);
        }

//        kafkaTemplate.send("MY_MART_ORDER_TOPIC", OrderMapper.toDto(orderEntity));

        // final Response

        PurchaseOrderResponseDto responseDto = new PurchaseOrderResponseDto();
        responseDto.setOrderId(savedOrder.getOrderId());
        responseDto.setRazorpayOrderId(razorpayOrderId);
        responseDto.setOrderStatus(savedOrder.getOrderStatus());
        responseDto.setPaymentStatus(savedOrder.getPaymentStatus());
        responseDto.setOrderTrackingNumber(savedOrder.getOrderTrackingNum());

        return responseDto;
    }

    public OrderDto updateOrder(Integer orderId, OrderDto orderDto) {
        Optional<OrderEntity> byId = orderRepo.findById(orderId);
        if (byId.isPresent()) {
            OrderEntity orderEntity = byId.get();
            orderEntity.setOrderStatus(orderDto.getOrderStatus());
            orderEntity.setPaymentStatus(orderDto.getPaymentStatus());
            orderEntity.setRazorpayPaymentId(orderDto.getRazorpayPaymentId());
            OrderEntity updatedOrder = orderRepo.save(orderEntity);

            OrderDto updatedOrderDto = OrderMapper.toDto(updatedOrder);

//            kafkaTemplate.send("MY_MART_ORDER_TOPIC", updatedOrderDto);

            return updatedOrderDto;
        }
        return null;
    }

    public OrderDto cancelOrder(Integer orderId) throws Exception {
        Optional<OrderEntity> byId = orderRepo.findById(orderId);
        if (byId.isPresent()) {
            OrderEntity orderEntity = byId.get();
            razorpayService.refundPayment(orderEntity.getRazorpayPaymentId(), orderEntity.getTotalPrice());
            orderEntity.setOrderStatus("CANCELLED");
            orderEntity.setPaymentStatus("REFUND-IN-PROGRESS");
            OrderEntity cancelledOrder = orderRepo.save(orderEntity);
            // TO DO : Send email
            return OrderMapper.toDto(cancelledOrder);
        }
        return null;
    }

    public OrderDto trackOrder(String trackingNumber) {
        OrderEntity entity = orderRepo.findByOrderTrackingNum(trackingNumber);
        if (entity != null) {
            return OrderMapper.toDto(entity);
        }
        return null;
    }

    public List<OrderDto> getCustomerOrders(String email) {
        return orderRepo.findByUserEmail(email).stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    private String generateOrderTrackingNumber() {
        return UUID.randomUUID().toString();
    }
}
