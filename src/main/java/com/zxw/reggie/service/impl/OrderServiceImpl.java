package com.zxw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxw.reggie.common.BaseContext;
import com.zxw.reggie.common.CustomException;
import com.zxw.reggie.entity.*;
import com.zxw.reggie.mapper.OrderMapper;
import com.zxw.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     *
     * @param orders
     */
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户的id
        Long userId = BaseContext.getCurrentId();

        //查询购物车表中的数据
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        //判断
        if (list == null || list.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBookServiceById = addressBookService.getById(addressBookId);
        if (addressBookServiceById == null) {
            throw new CustomException("地址信息有误，不能下单");
        }

        long orderid = IdWorker.getId();//生成订单号

        //总金额（默认是0）；原子整数变量，原子整数是一种不可被其他线程修改的整数类型，在多线程环境下保证线程安全和一致性
        AtomicInteger amount = new AtomicInteger(0);

        //遍历购物车中的数据并且计算总值
        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderid);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());//.multiply（）相当于乘法，金额乘以数量然后封装给BigDecimal
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderid);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderid));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBookServiceById.getConsignee());
        orders.setPhone(addressBookServiceById.getPhone());
        orders.setAddress((addressBookServiceById.getProvinceName() == null ? "" : addressBookServiceById.getProvinceName())
                + (addressBookServiceById.getCityName() == null ? "" : addressBookServiceById.getCityName())
                + (addressBookServiceById.getDistrictName() == null ? "" : addressBookServiceById.getDistrictName())
                + (addressBookServiceById.getDetail() == null ? "" : addressBookServiceById.getDetail()));
        //保存订单表（向订单表插入数据）





        this.save(orders);

        //保存订单明细表...（多条数据）
//        批量保存订单详细信息
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(lambdaQueryWrapper);

    }
}
