package com.zxw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxw.reggie.entity.Category;



public interface CategoryService extends IService<Category> {


    void removeCategory(Long id);


}
