package com.zxw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxw.reggie.common.CustomException;
import com.zxw.reggie.entity.Category;
import com.zxw.reggie.entity.Dish;
import com.zxw.reggie.entity.Setmeal;
import com.zxw.reggie.mapper.CategoryMapper;
import com.zxw.reggie.service.CategoryService;
import com.zxw.reggie.service.DishService;
import com.zxw.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;



    /**
     * 根据id删除分类,删除前进行判断
     * @param id
     */
    @Override
    public void removeCategory(Long id) {
        //构造查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件,根据分类id  (eq方法等值查询)
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //查询当前是否关联菜品，如果关联，抛出异常
        if (count1 > 0){
            //说明已经关联菜品
            throw new CustomException("当前分类项已关联菜品");

        }
        //查询当前是否关联套餐，如果关联，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0){
            //已经关联套餐,抛异常
           throw new CustomException("当前分类项已关联套餐");
        }
        //正常删除
        super.removeById(id);
    }



    }



