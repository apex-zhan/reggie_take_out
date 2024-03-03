package com.zxw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxw.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders order);
}
