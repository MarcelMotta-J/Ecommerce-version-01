package com.mrcl.store1.dto;

import com.mrcl.store1.entity.Address;
import com.mrcl.store1.entity.Customer;
import com.mrcl.store1.entity.Order;
import com.mrcl.store1.entity.OrderItem;
import lombok.Data;

import java.util.Set;

@Data
public class Purchase {

private Customer customer;

private Address shippingAddress;

private Address billingAddress;

private Order order;

private Set<OrderItem> orderItems;

}
