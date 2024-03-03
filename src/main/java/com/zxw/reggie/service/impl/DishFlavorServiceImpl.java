package com.zxw.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxw.reggie.entity.DishFlavor;
import com.zxw.reggie.mapper.DishFlavorMapper;
import com.zxw.reggie.service.DishFlavorService;
import com.zxw.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper , DishFlavor> implements DishFlavorService{
}
