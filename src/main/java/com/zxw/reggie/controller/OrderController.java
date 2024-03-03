package com.zxw.reggie.controller;


import com.zxw.reggie.common.R;
import com.zxw.reggie.entity.Orders;
import com.zxw.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController
@Slf4j

public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param order
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order) {
        log.info("订单数据：{}", order);
        orderService.submit(order);


        return R.success("下单成功");
    }
}
