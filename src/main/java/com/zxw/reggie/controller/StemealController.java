package com.zxw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxw.reggie.common.R;
import com.zxw.reggie.dto.SetmealDto;
import com.zxw.reggie.entity.Category;
import com.zxw.reggie.entity.Setmeal;
import com.zxw.reggie.service.CategoryService;
import com.zxw.reggie.service.SetmealDishService;
import com.zxw.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐管理相关接口 ")
public class StemealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)//用于在方法执行后清除缓存
    @ApiOperation(value = "新增套餐接口")
//    指定缓存名称（value属性）和是否清除所有条目（allEntries属性）。如果allEntries为true，则清除所有条目，否则只清除指定方法（通过@Cacheable注解）的缓存。
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        //使用setmealDto对象保存数据到setmealService中。具体实现可能包括检查setmealDto是否有效，创建或更新相应的记录，并返回相应的结果。
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "name", value = "套餐名称", required = false)
    })
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        //因为categoryName需要展示
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，通过name进行like模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);
        //对象拷贝(pageInfo->dtoPage),records属性需要单独处理不需要拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map(item -> {
            //分类id查询分类数据拿到分类名称
            Long categoryId = item.getCategoryId();
            //分类名称查询分类对象
            Category category = categoryService.getById(categoryId);
            //判断
            SetmealDto setmealDto = null;
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                //封装到dto对象中
                setmealDto = new SetmealDto();
                //对象拷贝
                BeanUtils.copyProperties(item, setmealDto);
                //赋值
                setmealDto.setCategoryName(categoryName);

            }

            return setmealDto;
        }).collect(Collectors.toList());
        //赋值进dtoPage
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    @ApiOperation(value = "删除套餐")
//    指定缓存名称（value属性）和是否清除所有条目（allEntries属性）。如果allEntries为true，则清除所有条目，否则只清除指定方法（通过@Cacheable注解）的缓存。
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids:{}", ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件来查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "根据条件查询套餐数据")
//    缓存一个名为setmeal的对象，缓存值的名称是setmealCache，缓存的键是setmeal.categoryId+'_'+setmeal.status的拼接结果。
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list( Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);



    }

}